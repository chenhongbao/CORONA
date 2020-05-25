package com.nabiki.corona.object.tool;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.nabiki.corona.system.api.KerError;

public class PacketClient {
	private enum SocketState {
		OPENING, OPEN, CLOSING, CLOSED
	}

	private static class CachePayload {
		public final short type;
		public final byte[] bytes;

		private CachePayload(short type, byte[] bytes) {
			this.type = type;
			this.bytes = bytes;
		}

		static CachePayload create(short type, byte[] bytes) {
			return new CachePayload(type, bytes);
		}
	}

	private PacketSocket remote;
	private Instant closeTime;
	private Thread queueDaemon;
	private AtomicReference<SocketState> state;
	private final Instant openTime;
	private final Queue<CachePayload> queue;

	private Lock lck;
	private Condition condition;

	public PacketClient(Socket connection) throws KerError {
		this.remote = new PacketSocket(connection);
		this.state = new AtomicReference<>(SocketState.OPEN);
		this.openTime = Instant.now();

		// Lock and condition.
		this.lck = new ReentrantLock();
		this.condition = this.lck.newCondition();

		// Create blocking queue to cache data.
		this.queue = new ConcurrentLinkedQueue<>();
		this.queueDaemon = new Thread(new Runnable() {

			@Override
			public void run() {
				while (state.get() != SocketState.CLOSED) {
					try {
						switch (state.get()) {
						case OPENING:
							openSocket();
							if (state.get() != SocketState.OPEN)
								// Need to wait some time, don't be busy connecting.
								try {
									Thread.sleep(Duration.ofSeconds(5).toMillis());
								} catch (InterruptedException e1) {
								}
							break;
						case CLOSING:
							closeSocket();
							break;
						case OPEN:
							// If all cache is sent successfully, await on condition.
							if (sendCache())
								condition.await();
							break;
						default:
							break;
						}
					} catch (InterruptedException e) {
					}
				}
			}

		});

	}
	
	public Packet receive() throws KerError {
		try {
			return this.remote.receive();
		} catch (KerError e) {
			this.state.set(SocketState.OPENING);
			this.condition.signal();
			// Throw exception to notify caller.
			throw e;
		}
	}

	public void close() {
		// Signal daemon to close connection.
		this.state.set(SocketState.CLOSING);
		this.condition.signal();
		// Join thread.
		if (this.queueDaemon.isAlive())
			try {
				this.queueDaemon.join(1000);
			} catch (InterruptedException e) {
			}
	}

	private void closeSocket() {
		this.closeTime = Instant.now();
		// Validate state.
		if (this.remote == null || this.remote.socket().isClosed() || !this.remote.socket().isConnected())
			return;
		// Close.
		this.remote.close();
		this.state.set(SocketState.CLOSED);
	}

	private void openSocket() {
		var addr = this.remote.socket().getRemoteSocketAddress();
		try {
			this.remote.socket().connect(addr);
			this.state.set(SocketState.OPEN);
		} catch (IOException e) {
		}
	}
	
	private boolean sendCache() {
		while (this.queue.size() > 0) {
			var c = this.queue.poll();
			try {
				// Use packet client's send method. Don't use class method because it sets the socket
				// state in a different way.
				this.remote.send(c.type, c.bytes, 0, c.bytes.length);
			} catch (KerError e) {
				System.err.println(
						"Socket sending failure, inconsistent state, found OPEN. " + e.message());				
				// Mark state as OPENING so it will reconnect at next sending.
				this.state.set(SocketState.OPENING);
				return false;
			}
		}
		
		return true;
	}

	public boolean available() {
		return this.state.get() == SocketState.OPEN;
	}
	
	public boolean closed() {
		return this.state.get() == SocketState.CLOSING || this.state.get() == SocketState.CLOSED;
	}

	public synchronized void send(short type, byte[] bytes, int offset, int length) throws KerError {
		// Silently return because it is verification error.
		// Here any exception is taken as IO-related error.
		if (bytes == null || bytes.length < offset + length)
			return;
		
		if (available()) {
			try {
				this.remote.send(type, bytes, offset, length);
			} catch (KerError e) {
				cache(type, bytes, offset, length);
				// Notify reconnecting.
				this.state.set(SocketState.OPENING);
				this.condition.signal();
				// Re-throw exception to notify caller about error.
				throw e;
			}
		} else if (this.state.get() == SocketState.OPENING) {
			cache(type, bytes, offset, length);
			// When state is OPENING, it will open connection and set state to OPEN.
			// And it will send all cached data, then await on condition.
		}
	}

	private void cache(short type, byte[] payload, int offset, int length) {
		// Create new byte array because the original array may be used elsewhere.
		ByteBuffer bb = ByteBuffer.allocate(length);
		bb.put(payload, offset, length);
		this.queue.add(CachePayload.create(type, bb.array()));
	}

	public Instant openTime() {
		return this.openTime;
	}

	public Instant closeTime() {
		return this.closeTime;
	}
}

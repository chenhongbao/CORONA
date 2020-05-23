package com.nabiki.corona.candle.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.object.DefaultDataCodec;
import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.object.tool.Packet;
import com.nabiki.corona.object.tool.PacketConnector;
import com.nabiki.corona.system.api.DataCodec;
import com.nabiki.corona.system.api.DataFactory;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerTick;
import com.nabiki.corona.system.info.api.RemoteConfig;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.system.packet.api.RxTickMessage;
import com.nabiki.corona.system.packet.api.TxSubscribeSymbolMessage;

public class TickEngine implements Runnable {
	private EngineState state = EngineState.STOPPED;
	private PacketConnector remote;
	
	private final RuntimeInfo runtime;
	private final TickEngineListener listener;
	private final DataCodec codec = DefaultDataCodec.create();
	private final DataFactory factory = DefaultDataFactory.create();
	
	// Data queue.
	private Thread queDaemon;
	private final BlockingQueue<KerTick> dataQueue = new LinkedBlockingQueue<>();

	public TickEngine(TickEngineListener l, RuntimeInfo info) {
		this.listener = l;
		this.runtime = info;
	}
	
	public void sendSymbols() throws KerError {
		var symbols = this.factory.create(TxSubscribeSymbolMessage.class);
		symbols.values(this.runtime.symbols());
		symbols.last(true);
		
		// Encode.
		var bytes = this.codec.encode(symbols);
		if (this.remote.closed())
			throw new KerError("Can't send symbols because remote connection is closed.");
		
		this.remote.send(MessageType.TX_SET_SUBSCRIBE_SYMBOLS, bytes, 0, bytes.length);
	}

	@Override
	public void run() {
		this.state = EngineState.STARTING;
		callListener(this.state);
		
		// Queue daemon.
		this.queDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (state != EngineState.STOPPING) {
					try {
						var tick = dataQueue.poll(24, TimeUnit.HOURS);
						callListener(tick);
					} catch (InterruptedException e) {
						if (state != EngineState.STOPPING)
							callListener(new KerError("Date queue waiting for tick interrupted.", e));
					}
				}
			}
		});
		
		// Connect remote.
		try {
			this.remote = connect();
		} catch (KerError e) {
			callListener(e);
			return;
		}
			
		// Receive packet and process it.
		while (this.state != EngineState.STOPPING) {
			try {
				processPacket(this.remote.receive());
			} catch (KerError e) {
				if (this.state != EngineState.STOPPING) {
					callListener(e);
				} else {
					callListener(this.state);
				}
			}
		}
		
		// Try closing connection if it comes here by other error.
		tryClose();
		
		// Interrupt data queue thread.
		if (!this.queDaemon.isInterrupted())
			this.queDaemon.interrupt();
		
		try {
			this.queDaemon.join();
		} catch (InterruptedException e) {}
		
		// Call listener on state change.
		this.state = EngineState.STOPPED;
		callListener(this.state);
	}
	
	private PacketConnector connect() throws KerError{
		// Find connection config to remote.
		RemoteConfig conf = null;
		for (var c : this.runtime.remoteConfig().configs()) {
			if (c.name().toLowerCase().matches("(md)|(tick)|((market)[\\s_-]?(data))")) {
				conf = c;
				break;
			}
		}
		
		if (conf == null)
			throw new KerError("Remote connection configuration not found. "
					+ "Need name like \'tick\', \'market-data\', \'market_data\' or \'marketdata\'");
		
		try {
			var address = new InetSocketAddress(InetAddress.getByName(conf.host()), conf.port());
			
			// Connect remote.
			var connection = new Socket();
			connection.connect(address);
			
			// Wrap connection into packet socket.
			return new PacketConnector(connection);
		} catch (UnknownHostException e) {
			throw new KerError("Can't find remote host " + conf.host() + ":" + conf.port());
		} catch (IOException e) {
			throw new KerError("Fail connecting remote host " + conf.host() + ":" + conf.port());
		}
	}
	
	private void processPacket(Packet packet) {
		if (packet.type() != MessageType.RX_TICK) {
			callListener(new KerError("Wrong packet type, need TICK."));
			return;
		}
		
		try {
			var ticks = this.codec.decode(packet.bytes(), RxTickMessage.class);
			for (var tick : ticks.values()) {
				if (!this.dataQueue.offer(tick))
					callListener(new KerError("Fail enqueuing the tick."));
			}
		} catch (KerError e) {
			callListener(e);
		}
	}
	
	private void callListener(KerError e) {
		if (this.listener == null)
			return;
		
		try {
			this.listener.error(e);
		} catch (Exception ex) {}
	}
	
	private void callListener(EngineState s) {
		if (this.listener == null)
			return;
		
		try {
			this.listener.state(s);
		} catch (Exception ex) {}
	}
	
	private void callListener(KerTick tick) {
		if (this.listener == null)
			return;
		
		try {
			this.listener.tick(tick);
		} catch (Exception ex) {}
	}
	
	private void tryClose() {
		if (this.remote.closed())
			return;
		
		this.remote.close();
	}

	public void tellStopping() {
		this.state = EngineState.STOPPING;
		tryClose();
	}

	public EngineState state() {
		return this.state;
	}
}

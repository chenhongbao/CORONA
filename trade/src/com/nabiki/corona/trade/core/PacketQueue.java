package com.nabiki.corona.trade.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.tools.Packet;
import com.nabiki.corona.trade.TradeLauncher;

public class PacketQueue implements Runnable {
	private final static int MAX_QUERY_PER_SEC = 1;
	private final static int MAX_REQUEST_PER_SEC = 6;

	private final TradeLauncher launcher;

	// Sent counter for last second.
	private final AtomicInteger qryCounter = new AtomicInteger(0);
	private final AtomicInteger reqCounter = new AtomicInteger(0);

	// Packet queue.
	private final Queue<Packet> reqQueue = new ConcurrentLinkedQueue<>();
	private final Queue<Packet> qryQueue = new ConcurrentLinkedQueue<>();

	private boolean stopped = true;

	public PacketQueue(TradeLauncher launcher) {
		this.launcher = launcher;
	}

	public int enqueue(Packet packet) throws KerError {
		if (101 <= packet.type() && packet.type() <= 200) {
			// Query packet.
			if (this.qryCounter.get() < PacketQueue.MAX_QUERY_PER_SEC) {
				// Increase counter.
				this.qryCounter.incrementAndGet();
			} else {
				this.qryQueue.add(packet);
				return this.qryQueue.size();
			}
		} else if (201 <= packet.type() && packet.type() <= 300) {
			// Request.
			if (this.reqCounter.get() < PacketQueue.MAX_REQUEST_PER_SEC) {
				// Increase counter.
				this.reqCounter.incrementAndGet();
			} else {
				this.reqQueue.add(packet);
				return this.reqQueue.size();
			}
		}

		// Send now and return true.
		this.launcher.remote().send(packet.type(), packet.bytes(), 0, packet.bytes().length);
		return 0;
	}

	@Override
	public void run() {
		this.stopped = false;

		while (!this.stopped) {
			try {
				// Sleep 1 second.
				Thread.sleep(1000);
				
				sendPackets(this.reqQueue, this.reqCounter, PacketQueue.MAX_REQUEST_PER_SEC);
				sendPackets(this.qryQueue, this.qryCounter, PacketQueue.MAX_QUERY_PER_SEC);
			} catch (InterruptedException e) {
				// Interrupted by trade launcher to exit.
				// Do nothing here. The while loop will exit if thread is interrupted for close.
			}
		}
	}
	
	private void sendPackets(Queue<Packet> packets, AtomicInteger countToSet, int maxCount) {
		int count = 0;
		
		// Process packets.
		while (packets.size() > 0 && count < maxCount) {
			var p = packets.poll();
			if (p  == null)
				continue;
			
			try {
				this.launcher.remote().send(p.type(), p.bytes(), 0, p.bytes().length);
				++count;
			} catch (KerError e) {}
		}
		
		countToSet.set(count);
	}

	public void tellStop() {
		this.stopped = true;
		Thread.currentThread().interrupt();
	}
}

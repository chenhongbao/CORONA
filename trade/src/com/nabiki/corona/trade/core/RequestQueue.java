package com.nabiki.corona.trade.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.trade.TradeLauncher;

public class RequestQueue implements Runnable {
	private final static int MAX_QUERY_PER_SEC = 1;
	private final static int MAX_REQUEST_PER_SEC = 6;

	private final TradeLauncher launcher;

	// Sent counter for last second.
	private final AtomicInteger qryCounter = new AtomicInteger(0);
	private final AtomicInteger reqCounter = new AtomicInteger(0);

	// Packet queue.
	private final Queue<Request<?>> reqQueue = new ConcurrentLinkedQueue<>();
	private final Queue<Request<?>> qryQueue = new ConcurrentLinkedQueue<>();

	private boolean stopped = true;

	public RequestQueue(TradeLauncher launcher) {
		this.launcher = launcher;
	}

	public int enqueue(Request<?> request) throws KerError {
		switch (request.type()) {
		case Order:
		case Action:
			// Request.
			if (this.reqCounter.get() < RequestQueue.MAX_REQUEST_PER_SEC) {
				// Increase counter.
				this.reqCounter.incrementAndGet();
			} else {
				this.reqQueue.add(request);
				return this.reqQueue.size();
			}
			break;
		case Unknown:
			throw new KerError("Unknown request type.");
		default:
			// Query packet.
			if (this.qryCounter.get() < RequestQueue.MAX_QUERY_PER_SEC) {
				// Increase counter.
				this.qryCounter.incrementAndGet();
			} else {
				this.qryQueue.add(request);
				return this.qryQueue.size();
			}
			break;
		}

		// Send now and return true.
		this.launcher.remote().send(request);
		return 0;
	}

	@Override
	public void run() {
		this.stopped = false;

		while (!this.stopped) {
			try {
				// Sleep 1 second.
				Thread.sleep(1000);

				sendRequest(this.reqQueue, this.reqCounter, RequestQueue.MAX_REQUEST_PER_SEC);
				sendRequest(this.qryQueue, this.qryCounter, RequestQueue.MAX_QUERY_PER_SEC);
			} catch (InterruptedException e) {
				// Interrupted by trade launcher to exit.
				// Do nothing here. The while loop will exit if thread is interrupted for close.
			}
		}
	}

	private void sendRequest(Queue<Request<?>> requests, AtomicInteger countToSet, int maxCount) {
		int count = 0;

		// Process packets.
		while (requests.size() > 0 && count < maxCount) {
			var r = requests.poll();
			if (r == null)
				continue;

			try {
				this.launcher.remote().send(r);
				++count;
			} catch (KerError e) {
			}
		}

		countToSet.set(count);
	}

	public void tellStop() {
		this.stopped = true;
		Thread.currentThread().interrupt();
	}
}

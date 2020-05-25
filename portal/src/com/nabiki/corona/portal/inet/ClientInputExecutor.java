package com.nabiki.corona.portal.inet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.object.tool.Packet;

public class ClientInputExecutor implements Runnable {
	// Client input information keeper.
	public class ClientInput {
		public Packet input;
		public PacketService service;
		
		ClientInput(Packet input, PacketService service) {
			this.input = input;
			this.service = service;
		}
	}
	
	private boolean stopped;
	private final Thread daemon;
	private final BlockingQueue<ClientInput> queue = new LinkedBlockingQueue<>();
	
	public ClientInputExecutor(ClientInputHandler handler) {
		this.daemon = new Thread(this);
		this.daemon.start();
		this.daemon.setDaemon(true);
	}
	
	public void input(Packet input, PacketService service) {
		this.queue.offer(new ClientInput(input, service));
	}
	
	/**
	 * Remove inputs associated with given service after a client disconnects from server
	 * and the related packet service becomes unavailable. It returns number of removed inputs.
	 * 
	 * @param service service
	 * @return number of removed inputs
	 */
	public int remove(PacketService service) {
		int count = 0;
		for (var inp : this.queue) {
			if (inp.service != service)
				continue;
			// Set ref to null so it won't be executed and resources re-collected.
			inp.input = null;
			inp.service = null;
			++count;
		}
		
		return count;
	}

	@Override
	public void run() {
		// Set mark.
		stopped = false;
		
		while(!stopped) {
			try {
				var in = queue.poll(24, TimeUnit.HOURS);
				if (in.input == null || in.service == null)
					continue;
				
				// TODO decode input and call handler.
				switch(in.input.type()) {
				default:
					break;
				}
			} catch (InterruptedException e) {
			}
		}
	}
}

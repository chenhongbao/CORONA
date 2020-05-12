package com.nabiki.corona.trade.core;

import com.nabiki.corona.kernel.tools.Packet;
import com.nabiki.corona.trade.TradeLauncher;

public class PacketQueue implements Runnable {
	
	private final TradeLauncher launcher;
	
	public PacketQueue(TradeLauncher launcher) {
		this.launcher = launcher;
	}
	
	public int enqueue(Packet packet) {
		return 0;
	}

	@Override
	public void run() {
		// TODO route packet to remote server.
		
	}
}

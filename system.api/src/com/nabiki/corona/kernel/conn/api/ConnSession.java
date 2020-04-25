package com.nabiki.corona.kernel.conn.api;

import java.net.InetSocketAddress;

public interface ConnSession {
	void put(byte[] b);
	
	void terminate();
	
	String name();
	
	Boolean isClosed();
	
	InetSocketAddress peerAddress();
}

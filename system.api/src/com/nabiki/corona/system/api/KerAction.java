package com.nabiki.corona.system.api;

public interface KerAction {
	String orderId();
	
	void orderId(String s);
	
	int sessionId();
	
	void sessionId(int i);
	
	String symbol();
	
	void symbol(String s);
	
	String ipAddress();
	
	void ipAddress(String s);
	
	String macAddress();
	
	void macAddress(String s);
}

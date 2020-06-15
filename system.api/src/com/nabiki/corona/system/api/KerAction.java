package com.nabiki.corona.system.api;

public interface KerAction {
	String orderId();
	
	void orderId(String s);
	
	String sessionId();
	
	void sessionId(String s);
	
	String symbol();
	
	void symbol(String s);
	
	String ipAddress();
	
	void ipAddress(String s);
	
	String macAddress();
	
	void macAddress(String s);
}

package com.nabiki.corona.kernel.conn.api;

public interface ConnProfile {
	String url();
	
	void url(String s);
	
	Integer port();
	
	void port(Integer i);
	
	String user();
	
	void user(String s);
	
	String password();
	
	void password(String s);
	
	String domain();
	
	void domain(String s);
}

package com.nabiki.corona.system.api;

public interface KerLogin {
	String accountId();
	
	void accountId(String s);
	
	String pin();
	
	void pin(String s);
	
	int role();
	
	void role(int i);
	
	String address();
	
	void address(String s);
}

package com.nabiki.corona.system.api;

public interface KerNewAccount {
	String accountId();
	
	void accountId(String id);
	
	String pin();
	
	void pin(String pin);
	
	int role();
	
	void role(int r);
}

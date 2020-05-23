package com.nabiki.corona.system.api;

public interface KerQueryAccount {
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	String currencyId();
	
	void currencyId(String s);
	
	char bizType();
	
	void bizType(char c);
	
	String accountId();
	
	void accountId(String s);
}

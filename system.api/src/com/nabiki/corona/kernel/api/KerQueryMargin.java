package com.nabiki.corona.kernel.api;

public interface KerQueryMargin {
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	String symbol();
	
	void symbol(String s);
	
	char hedgeFlag();
	
	void hedgeFlag(char c);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	String investUnitId();
	
	void investUnitId(String s);
}

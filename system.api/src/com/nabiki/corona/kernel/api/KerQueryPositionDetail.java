package com.nabiki.corona.kernel.api;

public interface KerQueryPositionDetail {
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	String symbol();
	
	void symbol(String s);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	String investUnitId();
	
	void investUnitId(String s);
}

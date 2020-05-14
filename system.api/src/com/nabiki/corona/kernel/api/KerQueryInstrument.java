package com.nabiki.corona.kernel.api;

public interface KerQueryInstrument {
	String symbol();
	
	void symbol(String s);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	String exchangeInstId();
	
	void exchangeInstId(String s);
	
	String productId();
	
	void productId(String s);
}

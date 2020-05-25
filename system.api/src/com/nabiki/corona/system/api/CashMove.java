package com.nabiki.corona.system.api;

public interface CashMove {
	String accountId();
	
	CashMoveType type();
	
	String sessionId();
	
	String currency();
	
	String note();
	
	double amount();
}

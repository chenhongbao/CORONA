package com.nabiki.corona.mgr.api;

public interface CashMoveCommand {
	String accountId();
	
	CashMoveType type();
	
	String sessionId();
	
	String currency();
	
	String note();
	
	double amount();
}

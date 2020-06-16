package com.nabiki.corona.system.api;

public interface CashMove {
	String accountId();
	
	void accountId(String s);
	
	CashMoveType type();
	
	void type(CashMoveType type);
	
	String currencyId();
	
	void currencyId(String s);
	
	String note();
	
	void note(String s);
	
	double amount();
	
	void amount(double d);
}

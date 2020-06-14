package com.nabiki.corona.system.api;

public interface KerCommission {
	String symbol();
	
	void symbol(String s);
	
	char investorRange();
	
	void investorRange(char c);
	
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	double openRatioByMoney();
	
	void openRatioByMoney(double d);
	
	double openRatioByVolume();
	
	void openRatioByVolume(double d);
	
	double closeRatioByMoney();
	
	void closeRatioByMoney(double d);
	
	double closeRatioByVolume();
	
	void closeRatioByVolume(double d);
	
	double closeTodayRatioByMoney();
	
	void closeTodayRatioByMoney(double d);
	
	double closeTodayRatioByVolume();
	
	void closeTodayRatioByVolume(double d);
	
	String exchangeId();
	
	void exchangeId(String s);
}

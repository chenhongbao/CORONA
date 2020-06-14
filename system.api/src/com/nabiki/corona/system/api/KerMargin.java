package com.nabiki.corona.system.api;

public interface KerMargin {
	String symbol();
	
	void symbol(String s);
	
	char investorRange();
	
	void investorRane(char s);
	
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	char hedgeFlag();
	
	void hedgeFlag(char c);
	
	double longMarginRatioByMoney();
	
	void longMarginRatioByMoney(double d);
	
	double longMarginRatioByVolume();
	
	void longMarginRatioByVolume(double d);
	
	double shortMarginRatioByMoney();
	
	void shortMarginRatioByMoney(double d);
	
	double shortMarginRatioByVolume();
	
	void shortMarginRatioByVolume(double d);
	
	boolean isRelative();
	
	void isRelative(boolean b);
	
	String exchangeId();
	
	void exchangeId(String s);
}

package com.nabiki.corona.system.api;

public interface KerAction {
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	String orderId();
	
	void orderId(String s);
	
	int frontId();
	
	void frontId(int i);
	
	int sessionId();
	
	void sessionId(int i);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	String orderSyasId();
	
	void orderSysId(String s);
	
	char actionFlag();
	
	void actionFlag(char c);
	
	double limitPrice();
	
	void limitPrice(double d);
	
	int volumeChange();
	
	void volumeChange(int i);
	
	String userId();
	
	void userId(String s);
	
	String symbol();
	
	void symbol(String s);
	
	String investUnitId();
	
	void investUnitId(String s);
	
	String ipAddress();
	
	void ipAddress(String s);
	
	String macAddress();
	
	void macAddress(String s);
}

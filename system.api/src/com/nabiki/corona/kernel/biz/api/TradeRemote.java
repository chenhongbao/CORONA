package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.Order;

public interface TradeRemote {
	String name();

	void newOrder(Order o);
	
	void instrument(String symbol);
	
	void margin(String symbol);
	
	void commission(String symbol);
	
	void account();
	
	void positionDetails();
}

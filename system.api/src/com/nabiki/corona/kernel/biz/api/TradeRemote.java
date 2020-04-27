package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.NewOrder;

public interface TradeRemote {
	String name();

	void newOrder(NewOrder o);
	
	void instrument(String symbol);
	
	void margin(String symbol);
	
	void commission(String symbol);
	
	void account();
	
	void positionDetails();
}

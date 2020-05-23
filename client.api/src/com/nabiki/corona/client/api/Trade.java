package com.nabiki.corona.client.api;

import java.util.List;

public interface Trade {
	String sessionId();
	
	Order order();

	void send(Order order);
	
	void cancel();

	List<OrderStatus> status();

	List<TradeReport> tradeReport();
}

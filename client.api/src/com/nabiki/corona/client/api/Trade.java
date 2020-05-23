package com.nabiki.corona.client.api;

import java.util.Collection;

public interface Trade {
	String sessionId();
	
	Order order();

	void send(Order order);
	
	void cancel();

	OrderStatus status();

	Collection<OrderStatus> statuses();

	TradeReport tradeReport();

	Collection<TradeReport> tradeReports();
}

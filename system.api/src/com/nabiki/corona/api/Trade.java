package com.nabiki.corona.api;

import java.util.Collection;

public interface Trade {
	/**
	 * Identifier for this trade across system.
	 * @return trade id
	 */
	String id();
	
	/**
	 * Send order and wait for complete or cancel.
	 * 
	 * @param order new order
	 * @return order status
	 * @throws TradeException order execution error
	 */
	OrderStatus sendAndWait(NewOrder order);
	
	OrderStatus send(NewOrder order);
	
	OrderStatus lastStatus();
	
	Collection<OrderStatus> statuses();
	
	TradeReport lastTrade();
	
	Collection<TradeReport> trades();
	
	OrderStatus cancel();
}

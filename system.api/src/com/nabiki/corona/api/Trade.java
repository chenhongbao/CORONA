package com.nabiki.corona.api;

import java.util.Collection;

public interface Trade {
	/**
	 * Identifier for this trade across system.
	 * @return trade id
	 */
	String sessionId();
	
	/**
	 * Create an new order associated with this trade session. The return order object has some field already set by
	 * session. Don't modify those fields if you don't mean to manipulate the internal session data.
	 * <p>
	 * A trade session can have only one order. Call the method a second time will return null.
	 * 
	 * @return new order associated with the calling session
	 */
	Order order();

	/**
	 * Send order and wait for complete or cancel.
	 * 
	 * @param order new order
	 * @return order status
	 * @throws TradeException order execution error
	 */
	OrderStatus sendAndWait(Order order);

	OrderStatus send(Order order);

	OrderStatus lastStatus();

	Collection<OrderStatus> statuses();

	TradeReport lastTrade();

	Collection<TradeReport> trades();

	OrderStatus cancel();
}

package com.nabiki.corona.kernel.biz.api;

import java.time.LocalDate;

import com.nabiki.corona.api.Order;

public interface TradeRemote {
	/**
	 * Get name of the remote counter service.
	 * 
	 * @return service name
	 */
	String name();
	
	/**
	 * Get next valid order ID. The order ID increases per call.
	 * 
	 * @return order ID
	 */
	String nextOrderId();
	
	/**
	 * Get trading day.
	 * 
	 * @return trading day
	 */
	LocalDate tradingDay();

	/**
	 * Send new order to remote counter and return the number of currently pending order requests in queue.
	 * 
	 * @param o new oder
	 * @return number of pending order request in queue
	 */
	int order(Order o);

	/**
	 * Query instrument information of the given symbol from remote counter and return the number of currently pending
	 * query requests in queue.
	 * 
	 * @param symbol symbol
	 * @return the number of currently pending query in queue
	 */
	int instrument(String symbol);

	/**
	 * Query margin information of the given symbol from remote counter and return the number of currently pending query
	 * requests in queue.
	 * 
	 * @param symbol symbol
	 * @return the number of currently pending query in queue
	 */
	int margin(String symbol);

	/**
	 * Query commission information of the given symbol from remote counter and return the number of currently pending
	 * query requests in queue.
	 * 
	 * @param symbol symbol
	 * @return the number of currently pending query in queue
	 */
	int commission(String symbol);

	/**
	 * Query account on remote counter. The return account is the total account for all sub accounts in the system.
	 */
	void account();

	/**
	 * Query total position from remote counter under current account.
	 */
	void position();
}

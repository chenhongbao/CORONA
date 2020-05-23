package com.nabiki.corona.system.biz.api;

import com.nabiki.corona.system.api.KerAction;
import com.nabiki.corona.system.api.KerOrder;

public interface TradeRemote {
	/**
	 * Get name of the remote counter service.
	 * 
	 * @return service name
	 */
	String name();

	/**
	 * Send new order to remote counter and return the number of currently pending order requests in queue. If the order
	 * can't be enqueued due to some error, return -1.
	 * 
	 * @param order new oder
	 * @return number of pending order request in queue, or -1 on error.
	 */
	int order(KerOrder order);
	
	/**
	 * Cancel orders denoted by given order ID.
	 * 
	 * @param action action
	 * @return number of pending action request in queue
	 */
	int action(KerAction action);

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

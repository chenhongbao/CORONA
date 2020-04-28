package com.nabiki.corona.trade.core;

import com.nabiki.corona.api.Order;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerTradeReport;

public class AccountEngine {
	
	private PositionEngine position;

	public AccountEngine() {
		// TODO account engine: compute account upon newly arriving trade
	}
	
	public void trade(KerTradeReport rep) throws KerError {
		// Set the trade session ID and update position.
		rep.sessionId(sessionId(rep.orderId()));
		this.position.trade(rep);
		
		// TODO trade
	}
	
	/**
	 * Evaluate the order and try to allocate resource. The new order must have an order ID and the engine generates an
	 * trade session ID for the new order and maps to order ID.
	 * 
	 * @param order order, must have an order ID
	 * @return evaluation result
	 */
	public KerOrderEvalue evaluateOrder(Order order) {
		// TODO evaluate order
		return null;
	}

	/**
	 * Get trade session ID with an order ID returned from remote. Trade session ID is generated and used within this
	 * system only.
	 * @param orderId order ID(order reference)
	 * @return trade session ID
	 */
	private String sessionId(String orderId) {
		return "";
	}
}

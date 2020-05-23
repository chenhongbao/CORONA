package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.nabiki.corona.system.Utils;

public class IdKeeper {
	// Order ID -> Session ID.
	private Map<String, String> o2s = new ConcurrentHashMap<>();
	// Session ID -> Order ID.
	private Map<String, String> s2o = new ConcurrentHashMap<>();
	// Session ID -> Account ID.
	private Map<String, String> s2a = new ConcurrentHashMap<>();
	
	private AtomicInteger origin = new AtomicInteger(0);
	
	public IdKeeper() {}
	
	public void resetId(int origin) {
		this.origin.set(origin);
	}
	
	/**
	 * Remove mapping for order ID. The method is called after session ends.
	 * 
	 * @param orderId order ID
	 */
	public void removeOrderId(String orderId) {
		this.o2s.remove(orderId);
		
		var sid = getSessionIdWithOrderId(orderId);
		if (sid == null)
			return;
		
		this.s2o.remove(sid);
		this.s2a.remove(sid);
	}
	
	public String createOrderId(String accountId) {
		var newId = Integer.toString(this.origin.incrementAndGet());
		var sid = Utils.sessionId();
		
		// Save mapping.
		this.o2s.put(newId, sid);
		this.s2o.put(sid, newId);
		this.s2a.put(sid, accountId);
		
		return newId;
	}
	
	public String getSessionIdWithOrderId(String orderId) {		
		return this.o2s.get(orderId);
	}
	
	// Return order ID associated with the given session ID, or null if no such mapping.
	public String getOrderIdWithSessionId(String sessionId) {
		return this.s2o.get(sessionId);
	}
	
	// Get account ID with session ID.
	public String getAccountIdWithSessionId(String sessionId) {
		return this.s2a.get(sessionId);
	}
}

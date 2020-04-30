package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.Utils;

public class SessionIdManager {
	// Order ID -> Session ID.
	private Map<String, String> sessionIds = new ConcurrentHashMap<>();
	// Session ID -> Order ID.
	private Map<String, String> orderIds = new ConcurrentHashMap<>();
	
	SessionIdManager() {}
	
	String sid(String orderId) {
		if (!this.sessionIds.containsKey(orderId)) {
			var sid = Utils.sessionId();
			this.sessionIds.put(orderId, sid);
		}
		
		return this.sessionIds.get(orderId);
	}
	
	// Return order ID associated with the given session ID, or null if not such mapping.
	String oid(String sessionId) {
		return this.orderIds.get(sessionId);
	}
}

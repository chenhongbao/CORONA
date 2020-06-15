package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.KerError;

public class IdKeeper {
	// Order ID -> Session ID.
	private Map<String, String> o2s = new ConcurrentHashMap<>();
	// Session ID -> Order IDs.
	private Map<String, Collection<String>> s2o = new ConcurrentHashMap<>();
	// Session ID -> Account ID.
	private Map<String, String> s2a = new ConcurrentHashMap<>();
	
	private AtomicInteger origin = new AtomicInteger(0);
	
	public IdKeeper() {}
	
	public void resetId(int origin) {
		this.origin.set(origin);
	}
	
	public void eraseOrderId(String orderId) {
		this.o2s.remove(orderId);
		
		var sid = getSessionIdWithOrderId(orderId);
		if (sid == null)
			return;
		else
			this.s2o.get(sid).remove(orderId);
	}
	
	public String createOrderId(String sessionId) throws KerError {
		if (this.s2o.get(sessionId) == null)
			throw new KerError("Unknown session ID: " + sessionId);
		
		var newId = Integer.toString(this.origin.incrementAndGet());
		
		// Save mapping.
		this.o2s.put(newId, sessionId);
		this.s2o.get(sessionId).add(newId);
		
		return newId;
	}
	
	public String createSessionId(String accountId) {
		var sid = Utils.sessionId();
		this.s2o.put(sid, new LinkedList<>());
		this.s2a.put(sid,  accountId);
		return sid;
	}
	
	/**
	 * Get sessions Ids of the given account.
	 * 
	 * @param accountId account id
	 * @return collection of session ids
	 */
	public Collection<String> getSessionIdsOfAccount(String accountId) {
		var r = new HashSet<String>();
		for (var entry : this.s2a.entrySet())
			if (entry.getValue().compareTo(accountId) == 0)
				r.add(entry.getKey());
		
		return r;
	}
	
	public String getSessionIdWithOrderId(String orderId) {		
		return this.o2s.get(orderId);
	}
	
	// Return order ID associated with the given session ID, or null if no such mapping.
	public Collection<String> getOrderIdsWithSessionId(String sessionId) {
		return this.s2o.get(sessionId);
	}
	
	// Get account ID with session ID.
	public String getAccountIdWithSessionId(String sessionId) {
		return this.s2a.get(sessionId);
	}
}

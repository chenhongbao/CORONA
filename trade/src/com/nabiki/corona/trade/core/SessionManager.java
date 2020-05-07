package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.ErrorMessage;
import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.KerError;

public class SessionManager {
	// Order ID -> Session ID.
	private Map<String, String> sessionIds = new ConcurrentHashMap<>();
	// Session ID -> Order ID.
	private Map<String, String> orderIds = new ConcurrentHashMap<>();
	// Order ID -> Account ID.
	private Map<String, String> accountIds = new ConcurrentHashMap<>();
	
	public SessionManager() {}
	
	public String createSessionId(String orderId, String accountId) throws KerError {
		if (!this.sessionIds.containsKey(orderId))
			throw new KerError(ErrorCode.DUPLICATE_ORDER_REF, ErrorMessage.DUPLICATE_ORDER_REF);
		
		var sid = Utils.sessionId();
		this.sessionIds.put(orderId, sid);
		this.accountIds.put(orderId, accountId);
		return sid;
	}
	
	public String querySessionId(String orderId) throws KerError {
		if (!this.sessionIds.containsKey(orderId))
			throw new KerError(ErrorCode.ORDER_NOT_FOUND, ErrorMessage.ORDER_NOT_FOUND);
		
		return this.sessionIds.get(orderId);
	}
	
	// Return order ID associated with the given session ID, or null if not such mapping.
	public String getOrderId(String sessionId) throws KerError {
		if (!this.orderIds.containsKey(sessionId)) {
			throw new KerError(ErrorCode.INCONSISTENT_INFORMATION, ErrorMessage.INCONSISTENT_INFORMATION);
		}
		
		return this.orderIds.get(sessionId);
	}
	
	public String getAccountId(String orderId) {
		return this.accountIds.get(orderId);
	}
}

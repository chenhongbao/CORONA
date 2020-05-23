package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.system.api.KerOrderStatus;

public class OrderStatusKeeper {
	private final Map<String, KerOrderStatus> statuses = new ConcurrentHashMap<>();

	public OrderStatusKeeper() {
	}

	public void setStatus(String sid, KerOrderStatus status) {
		this.statuses.put(sid, status);
	}
	
	public KerOrderStatus getStatus(String sid) {
		return this.statuses.get(sid);
	}
}

package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.kernel.api.KerOrderStatus;

public class OrderStatusKeeper {
	private final Map<String, KerOrderStatus> statuses = new ConcurrentHashMap<>();

	public OrderStatusKeeper() {
	}

	public void setStatus(String sid, KerOrderStatus status) {
		this.statuses.put(sid, status);
	}
	
	public void getStatus(String sid) {
		this.statuses.get(sid);
	}
}

package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.KerAction;

public class KerActionGson implements KerAction {
	public String orderId;
	public String sessionId;
	public String accountId;
	public String symbol;
	public String ipAddress;
	public String macAddress;

	public KerActionGson() {
	}

	@Override
	public String orderId() {
		return this.orderId;
	}

	@Override
	public void orderId(String s) {
		this.orderId = s;
	}

	@Override
	public String sessionId() {
		return this.sessionId;
	}

	@Override
	public void sessionId(String s) {
		this.sessionId = s;
	}

	@Override
	public String accountId() {
		return this.accountId;
	}

	@Override
	public void accountId(String id) {
		this.accountId = id;
	}

	@Override
	public String symbol() {
		return this.symbol;
	}

	@Override
	public void symbol(String s) {
		this.symbol = s;
	}

	@Override
	public String ipAddress() {
		return this.ipAddress;
	}

	@Override
	public void ipAddress(String s) {
		this.ipAddress = s;
	}

	@Override
	public String macAddress() {
		return this.macAddress;
	}

	@Override
	public void macAddress(String s) {
		this.macAddress = s;
	}

}

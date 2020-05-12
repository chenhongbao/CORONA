package com.nabiki.corona.trade.core;

public class TradeEngine {
	
	private final TradeEngineListener listener;
	
	public TradeEngine(TradeEngineListener listener) {
		this.listener = listener;
	}

	public void send(short type, byte[] bytes, int offset, int length) {
		// TODO send encoded bytes to remote
	}
}

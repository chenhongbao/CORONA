package com.nabiki.corona.trade;

import org.osgi.service.component.annotations.Component;

import com.nabiki.corona.trade.core.TradeEngine;
import com.nabiki.corona.trade.core.TradeEngineListener;

@Component(service = {})
public class TradeLauncher implements Runnable {
	// TODO launch connection to remote
	
	private final TradeEngineListener listener;
	
	public TradeLauncher(TradeEngineListener listener) {
		this.listener = listener;
	}
	
	public TradeEngine remote() {
		// TODO return instance of trade engine
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
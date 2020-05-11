package com.nabiki.corona.trade;

import org.osgi.service.component.annotations.Component;

import com.nabiki.corona.trade.core.TradeEngine;

@Component(service = {})
public class TradeLauncher {
	// TODO launch connection to remote
	
	public TradeEngine remote() {
		// TODO return instance of trade engine
		return null;
	}
}

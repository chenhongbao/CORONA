package com.nabiki.corona.kernel.api;

import java.util.Collection;

import com.nabiki.corona.api.MarketDataSession;

/**
 * @author Hongbao Chen
 *
 */
public interface KerMarketDataSession extends MarketDataSession {
	Boolean isRunning();
	
	Collection<String> subscribedSymbols();
	
	void asyncRun();
	
	void run();
	
	void stop();
}

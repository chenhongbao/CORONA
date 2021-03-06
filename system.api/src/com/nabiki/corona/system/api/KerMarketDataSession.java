package com.nabiki.corona.system.api;

import java.util.Collection;

import com.nabiki.corona.client.api.MarketDataSession;

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

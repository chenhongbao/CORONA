package com.nabiki.corona.kernel.api;

import java.util.Collection;

import com.nabiki.corona.api.MarketDataSession;
import com.nabiki.corona.kernel.conn.api.Connection;

/**
 * @author Hongbao Chen
 *
 */
public interface KerMarketDataSession extends MarketDataSession {
	Boolean isRunning();
	
	Collection<String> subscribedSymbols();
	
	void underlyingConnection(Connection c);
	
	void asyncRun();
	
	void run();
	
	void stop();
}

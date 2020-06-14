package com.nabiki.corona.client.api;

/**
 * @author Hongbao Chen
 *
 */
public interface MarketDataSubscriber {
	void subscribe(String symbol, TickListener listener, MarketDataMode mode);

	void subscribe(String symbol, CandleListener listener, Integer minutePeriod, MarketDataMode mode);

	void unsubscribe(String symbol);
}

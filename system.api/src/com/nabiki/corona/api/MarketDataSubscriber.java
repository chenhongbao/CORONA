package com.nabiki.corona.api;
/**
 * @author Hongbao Chen
 *
 */
public interface MarketDataSubscriber {
	void subscribe(String symbol, TickListener listener, MarketDataSubsMode mode);
	
	void subscribe(String symbol, CandleListener listener, Integer minutePeriod, MarketDataSubsMode mode);
	
	void unsubscribe(String symbol);
	
	void recall(String symbol, TickListener listener);
	
	void recall(String symbol, CandleListener listener, Integer minutePeriod);
}

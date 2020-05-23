package com.nabiki.corona.client.api;

/**
 * Operations exposed to clients to get pre-trade data.
 * 
 * @author Hongbao Chen
 *
 */
public interface MarketDataSession {
	String accountId();

	MarketDataSubscriber marketDataSubscriber();

	Error error();
}

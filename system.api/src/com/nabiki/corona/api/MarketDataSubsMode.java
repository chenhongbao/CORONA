package com.nabiki.corona.api;

/**
 * @author Hongbao Chen
 *
 */
public enum MarketDataSubsMode {
	/**
	 * Receive market data before and during market opens.
	 */
	PRE_MARKET,

	/**
	 * Receive market data during market opens and after market closes.
	 */
	POST_MARKET,

	/**
	 * Receive market data at any time.
	 */
	PRE_POST_MARKET,

	/**
	 * Only receive market data before market opens.
	 */
	PRE_MARKET_ONLY,

	/**
	 * Only receive market data after market closes.
	 */
	POST_MARKET_ONLY;
}

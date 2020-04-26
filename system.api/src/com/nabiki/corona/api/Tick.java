package com.nabiki.corona.api;

import java.util.Date;
import java.time.Instant;

/**
 * Any object implementing this interface provides concrete data access to the tick flow from market.
 * 
 * @author Hongbao Chen
 *
 */
public interface Tick {
	String symbol();

	int askVolume();

	double askPrice();

	int bidVolume();

	double bidPrice();

	int askVolume2();

	double askPrice2();

	int bidVolume2();

	double bidPrice2();

	int askVolume3();

	double askPrice3();

	int bidVolume3();

	double bidPrice3();

	int askVolume4();

	double askPrice4();

	int bidVolume4();

	double bidPrice4();

	int askVolume5();

	double askPrice5();

	int bidVolume5();

	double bidPrice5();

	int openInterest();

	int preOpenInterest();

	int volume();

	double lowestPrice();

	double highestPrice();

	double upperLimitPrice();

	double lowerLimitPrice();

	double averagePrice();

	double lastPrice();

	double openPrice();

	double closePrice();

	double preClosePrice();

	double settlementPrice();

	double preSettlementPrice();

	boolean isPreMarket();

	boolean isPostMarket();

	boolean isRealTime();

	Instant updateTime();

	Date tradingDay();

	Date actionDay();
}

package com.nabiki.corona.client.api;

import java.time.Instant;
import java.time.LocalDate;

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

	LocalDate tradingDay();

	LocalDate actionDay();
}

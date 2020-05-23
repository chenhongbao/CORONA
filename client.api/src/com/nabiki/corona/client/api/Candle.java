package com.nabiki.corona.client.api;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Candle generated from ticks.
 * 
 * @author Hongbao Chen
 *
 */
public interface Candle {
	String symbol();

	double openPrice();

	double highPrice();

	double lowPrice();

	double closePrice();

	int openInterest();

	int volume();

	int minutePeriod();

	boolean isDay();

	boolean isLastOfDay();

	boolean isRealTime();

	Instant updateTime();

	LocalDate tradingDay();

	LocalDate actionDay();
}

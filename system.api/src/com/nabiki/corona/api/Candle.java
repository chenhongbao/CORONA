package com.nabiki.corona.api;

import java.util.Date;
import java.time.Instant;

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

	Date tradingDay();
	
	Date actionDay();
}

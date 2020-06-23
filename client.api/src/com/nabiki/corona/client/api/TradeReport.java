package com.nabiki.corona.client.api;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Trade report to client.
 * 
 * @author Hongbao Chen
 *
 */
public interface TradeReport {
	String orderId();
	
	String sessionId();
	
	String symbol();

	char direction();

	char offsetFlag();

	char hedgeFlag();

	double price();

	int volume();
	
	LocalDate tradeDate();

	LocalTime tradeTime();

	LocalDate tradingDay();
}

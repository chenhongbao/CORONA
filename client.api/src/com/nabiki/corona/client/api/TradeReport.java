package com.nabiki.corona.client.api;

import java.time.Instant;
import java.time.LocalDate;

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

	Instant tradeTime();

	LocalDate tradingDay();
}

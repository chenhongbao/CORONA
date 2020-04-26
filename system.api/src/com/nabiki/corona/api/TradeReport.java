package com.nabiki.corona.api;

import java.time.Instant;
import java.util.Date;

/**
 * Trade report to client.
 * 
 * @author Hongbao Chen
 *
 */
public interface TradeReport {
	String orderId();

	String tradeId();

	char direction();

	char offsetFlag();

	char hedgeFlag();

	double price();

	int volume();

	Instant tradeTime();

	Date tradingDay();

	int sequenceNo();

	int brokerOrderSequence();
}

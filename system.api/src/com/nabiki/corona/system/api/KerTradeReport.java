package com.nabiki.corona.system.api;

import java.time.LocalTime;
import java.time.LocalDate;

import com.nabiki.corona.client.api.TradeReport;

/**
 * Internally used trade report with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerTradeReport extends TradeReport {
	void sessionId(String s);

	void symbol(String s);

	void orderId(String s);

	void direction(char t);

	void offsetFlag(char t);

	void hedgeFlag(char t);

	void price(double d);

	void volume(int i);
	
	void tradeDate(LocalDate date);

	void tradeTime(LocalTime t);

	char tradeType();

	void tradeType(char t);

	void tradingDay(LocalDate d);
}

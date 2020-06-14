package com.nabiki.corona.client.api;

import java.time.LocalDate;

/**
 * Position detail per trade.
 * 
 * @author Hongbao Chen
 *
 */
public interface PositionDetail {
	String symbol();
	
	String sessionId();

	char hedgeFlag();

	char direction();

	LocalDate openDate();

	int volume();

	double openPrice();

	LocalDate tradingDay();

	char tradeType();

	double closeProfitByDate();

	double closeProfitByTrade();

	double positionProfitByDate();

	double positionProfitByTrade();
	
	double openCommission();
	
	double closeCommission();

	double margin();

	double exchangeMargin();

	double marginRateByMoney();

	double marginRateByVolume();

	double lastSettlementPrice();

	double settlementPrice();

	int closeVolume();

	double closeAmount();
}

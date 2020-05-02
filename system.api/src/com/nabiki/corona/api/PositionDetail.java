package com.nabiki.corona.api;

import java.time.LocalDate;

/**
 * Position detail per trade.
 * 
 * @author Hongbao Chen
 *
 */
public interface PositionDetail {
	String symbol();

	char hedgeFlag();

	char direction();

	LocalDate openDate();

	String tradeId();

	int volume();

	double openPrice();

	LocalDate tradingDay();

	String settlementId();

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

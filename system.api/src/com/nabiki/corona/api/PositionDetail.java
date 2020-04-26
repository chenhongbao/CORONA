package com.nabiki.corona.api;

import java.util.Date;

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

	Date openDate();

	String tradeId();

	int volume();

	double openPrice();

	Date tradingDay();

	String settlementId();

	char tradeType();

	double closeProfitByDate();

	double closeProfitByTrade();

	double positionProfitByDate();

	double positionProfitByTrade();

	double margin();

	double exchangeMargin();

	double marginRateByMoney();

	double maringRateByVolume();

	double lastSettlementPrice();

	double settlementPrice();

	int closeVolume();

	double closeAmount();
}

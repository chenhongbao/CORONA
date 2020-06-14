package com.nabiki.corona.system.api;

import java.time.LocalDate;

import com.nabiki.corona.client.api.PositionDetail;

/**
 * Position detail with full attributes and getters.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerPositionDetail extends PositionDetail {
	void symbol(String s);
	
	void sessionId(String s);

	void hedgeFlag(char t);

	void direction(char t);

	void openDate(LocalDate d);

	void volume(int i);

	void openPrice(double d);

	void tradingDay(LocalDate d);

	void tradeType(char t);

	String combSymbol();

	void combSymbol(String s);
	
	int volumeMultiple();
	
	void volumeMultiple(int i);

	void closeProfitByDate(double d);

	void closeProfitByTrade(double d);

	void positionProfitByDate(double d);

	void positionProfitByTrade(double d);
	
	void openCommission(double d);
	
	void closeCommission(double d);

	void margin(double d);

	void exchangeMargin(double d);

	void marginRateByMoney(double d);

	void marginRateByVolume(double d);

	void lastSettlementPrice(double d);

	void settlementPrice(double d);

	void closeVolume(int i);

	void closeAmount(double d);

	int timeFirstVolume();

	void timeFirstVolume(int i);
}

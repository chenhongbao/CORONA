package com.nabiki.corona.kernel.api;

import java.time.LocalDate;

import com.nabiki.corona.api.PositionDetail;

/**
 * Position detail with full attributes and getters.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerPositionDetail extends PositionDetail {
	void symbol(String s);

	String brokerId();

	void brokerId(String s);

	String investorId();

	void investorId(String s);

	void hedgeFlag(char t);

	void direction(char t);

	void openDate(LocalDate d);

	void tradeId(String s);

	void volume(int i);

	void openPrice(double d);

	void tradingDay(LocalDate d);

	void settlementId(String s);

	void tradeType(char t);

	String combSymbol();

	void combSymbol(String s);

	String exchangeId();

	void exchangeId(String s);

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

	String investUnitId();

	void investUnitId(String s);
	
	String tradeSessionId();
	
	void tradeSessionId(String s);
}

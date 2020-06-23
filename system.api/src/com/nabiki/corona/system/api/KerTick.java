package com.nabiki.corona.system.api;

import java.time.LocalDate;
import java.time.LocalTime;

import com.nabiki.corona.client.api.Tick;

public interface KerTick extends Tick {
	void symbol(String s);

	void askVolume(int i);

	void askPrice(double d);

	void bidVolume(int i);

	void bidPrice(double d);

	void openInterest(int i);

	void preOpenInterest(int i);

	void volume(int i);

	void lowestPrice(double d);

	void highestPrice(double d);

	void upperLimitPrice(double d);

	void lowerLimitPrice(double d);

	void averagePrice(double d);

	void lastPrice(double d);

	void openPrice(double d);

	void closePrice(double d);

	void preClosePrice(double d);

	void settlementPrice(double d);

	void preSettlementPrice(double d);

	void isPreMarket(boolean b);

	void isPostMarket(boolean b);

	void isRealTime(boolean b);

	void updateTime(LocalTime i);
	
	void updateMillis(int ms);

	void tradingDay(LocalDate d);

	void actionDay(LocalDate d);
}

package com.nabiki.corona.kernel.api;

import java.time.Instant;
import java.time.LocalDate;

import com.nabiki.corona.api.Tick;

public interface KerTick extends Tick {
	void symbol(String s);

	void askVolume(int i);

	void askPrice(double d);

	void bidVolume(int i);

	void bidPrice(double d);

	void askVolume2(int i);

	void askPrice2(double d);

	void bidVolume2(int i);

	void bidPrice2(double d);

	void askVolume3(int i);

	void askPrice3(double d);

	void bidVolume3(int i);

	void bidPrice3(double d);

	void askVolume4(int i);

	void askPrice4(double d);

	void bidVolume4(int i);

	void bidPrice4(double d);

	void askVolume5(int i);

	void askPrice5(double d);

	void bidVolume5(int i);

	void bidPrice5(double d);

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

	void updateTime(Instant i);

	void tradingDay(LocalDate d);

	void actionDay(LocalDate d);
}

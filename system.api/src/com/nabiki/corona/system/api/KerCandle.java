package com.nabiki.corona.system.api;

import java.time.Instant;
import java.time.LocalDate;

import com.nabiki.corona.client.api.Candle;

public interface KerCandle extends Candle {
	void symbol(String s);

	void openPrice(double d);

	void highPrice(double d);

	void lowPrice(double d);

	void closePrice(double d);

	void openInterest(int i);

	void volume(int i);

	void minutePeriod(int i);

	void isDay(boolean b);

	void isLastOfDay(boolean b);

	void isRealTime(boolean b);

	void updateTime(Instant i);

	void tradingDay(LocalDate d);

	void actionDay(LocalDate d);
}

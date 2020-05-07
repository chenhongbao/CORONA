package com.nabiki.corona.kernel.settings.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;

public interface RuntimeInfo {
	String name();
	
	LocalDate tradingDay();
	
	boolean ready(String symbol);
	
	void instrument(KerInstrument in);
	
	void margin(KerMargin margin);
	
	void commission(KerCommission comm);
	
	KerInstrument instrument(String symbol);
	
	KerMargin margin(String symbol);
	
	KerCommission commission(String symbol);
	
	Tick lastTick(String symbol);
	
	boolean candleNow(String symbol, int min, Instant now, int margin, TimeUnit marginUnit);

	Collection<String> symbols();
	
	boolean marketOpen(Instant now);

	boolean marketClosed(Instant now);

	/**
	 * Return true if now is after market close of the day and before market open of the next day.
	 * 
	 * @param now current instant
	 * @return true if now is the end of the current trading day.
	 */
	boolean endOfDay(Instant now);
}

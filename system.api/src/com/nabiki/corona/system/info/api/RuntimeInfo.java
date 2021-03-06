package com.nabiki.corona.system.info.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;

import com.nabiki.corona.system.api.KerCommission;
import com.nabiki.corona.system.api.KerInstrument;
import com.nabiki.corona.system.api.KerMargin;
import com.nabiki.corona.system.api.KerTick;

public interface RuntimeInfo {
	String name();
	
	LocalDate tradingDay();
	
	boolean ready(String symbol);
	
	void instrument(KerInstrument in, boolean last);
	
	void margin(KerMargin margin);
	
	void commission(KerCommission comm);
	
	KerInstrument instrument(String symbol);
	
	KerMargin margin(String symbol);
	
	KerCommission commission(String symbol);
	
	KerTick lastTick(String symbol);
	
	void lastTick(KerTick tick);
	
	boolean candleNow(String symbol, int min, Instant now);

	Collection<String> symbols();
	
	boolean isMarketOpen(Instant now);

	/**
	 * Return true if now is after market close of the day and before market open of the next day.
	 * 
	 * @param now current instant
	 * @param symbol symbol
	 * @return true if now is the end of the current trading day.
	 */
	boolean endOfDay(Instant now, String symbol);
	
	RemoteConfigSet remoteConfig();
}

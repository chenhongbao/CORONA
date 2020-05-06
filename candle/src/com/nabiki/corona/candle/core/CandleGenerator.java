package com.nabiki.corona.candle.core;

import java.time.Instant;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerCandle;
import com.nabiki.corona.kernel.settings.api.SymbolQuery;

public class CandleGenerator {
	public CandleGenerator(String symbol, SymbolQuery query) {
		// TODO candle generator
	}
	
	public void tick(Tick tick) {
		// TODO update tick
	}
	
	public KerCandle get(int minPeriod, Instant now) {
		// TODO get and pop candle.
		return null;
	}
	
	public KerCandle peak(int minPeriod, Instant now) {
		// TODO get candle, don't pop
		return null;
	}
}

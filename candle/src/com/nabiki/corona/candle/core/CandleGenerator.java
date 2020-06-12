package com.nabiki.corona.candle.core;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.CandleMinute;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;

public class CandleGenerator {
	private final String symbol;
	private final CandleServiceContext context;
	private final DataFactory factory;
	private final Map<Integer, RuntimeCandle> candles = new ConcurrentHashMap<>();
	
	private static int[] periods = new int[] { CandleMinute.MINUTE, CandleMinute.FIVE_MINUTE, CandleMinute.QUARTER,
			CandleMinute.HALF_HOUR, CandleMinute.HALF_QUADTER_HOUR, CandleMinute.HOUR, CandleMinute.TWO_HOUR};
	
	public CandleGenerator(String symbol, CandleServiceContext context, DataFactory factory) throws KerError {
		this.symbol = symbol;
		this.context = context;
		this.factory = factory;
		
		// Initialize runtime candles.
		for(var p : CandleGenerator.periods)
			this.candles.put(p, new RuntimeCandle(this.symbol, p, this.factory));
	}
	
	public void tick(KerTick tick) {
		for (var c : this.candles.values())
			c.update(tick);
	}
	
	public KerCandle get(int min, Instant now) throws KerError {
		if (!this.context.info().candleNow(this.symbol, min, now))
			return null;
		
		var rc = this.candles.get(min);
		if (rc == null)
			return null;
		
		return rc.pop();
	}
	
	public KerCandle peak(int minPeriod, Instant now) throws KerError {
		var rc = this.candles.get(minPeriod);
		if (rc == null)
			return null;
		
		return rc.peak();
	}
	
	private class RuntimeCandle {
		private final int min;
		private final String symbol;
		private final DataFactory factory;
		
		// Candle info.
		private KerCandle rtCandle;
		private int currentVolume = 0;
		private int previousVolume = 0;
		private LocalDate tradingDay;
		private boolean popped;
		
		RuntimeCandle(String symbol, int min, DataFactory factory) throws KerError {
			this.min = min;
			this.factory = factory;
			this.popped = true;
			this.symbol = symbol;
			this.rtCandle = this.factory.create(KerCandle.class);
			
			// Initialize runtime candle.
			this.rtCandle.highPrice(-Double.MAX_VALUE);
			this.rtCandle.lowPrice(Double.MAX_VALUE);
			this.rtCandle.symbol(this.symbol);
			this.rtCandle.minutePeriod(this.min);
			
			if (min == CandleMinute.DAY)
				this.rtCandle.isDay(true);
			else
				this.rtCandle.isDay(false);
		}
		
		void update(KerTick tick) {
			// Filter wrong symbol.
			if (tick.symbol().compareTo(this.symbol) != 0)
				return;
			
			synchronized(this.rtCandle) {
				if (this.popped) {
					this.rtCandle.openPrice(tick.lastPrice());
					this.rtCandle.highPrice(tick.lastPrice());
					this.rtCandle.lowPrice(tick.lastPrice());
					this.rtCandle.closePrice(tick.lastPrice());
					// Save trading day once per candle.
					this.tradingDay = tick.tradingDay();
					// Mark.
					this.popped = false;
				} else {
					this.rtCandle.closePrice(tick.lastPrice());
					this.rtCandle.highPrice(Math.max(this.rtCandle.highPrice(), tick.lastPrice()));
					this.rtCandle.lowPrice(Math.min(this.rtCandle.lowPrice(), tick.lastPrice()));
				}
				
				this.rtCandle.openInterest(tick.openInterest());
				this.rtCandle.updateTime(Instant.now());
				
				// Save volume.
				this.currentVolume = tick.volume();
			}
		}
		
		KerCandle pop() throws KerError {
			var ret = peak();
			
			synchronized(this.rtCandle) {
				// Reset volume.
				this.previousVolume = this.currentVolume;
				this.popped = true;
			}
			
			return ret;
		}
		
		KerCandle peak() throws KerError {
			var ret = this.factory.create(KerCandle.class);
			
			synchronized(this.rtCandle) {
				// Copy runtime candle.
				ret.symbol(this.rtCandle.symbol());
				ret.openPrice(this.rtCandle.openPrice());
				ret.highPrice(this.rtCandle.highPrice());
				ret.lowPrice(this.rtCandle.lowPrice());
				ret.closePrice(this.rtCandle.closePrice());
				ret.openInterest(this.rtCandle.openInterest());
				ret.updateTime(this.rtCandle.updateTime());
				
				// Get volume in this candle.
				ret.volume(this.currentVolume - this.previousVolume);
				// Dates.
				ret.tradingDay(Utils.deepCopy(this.tradingDay));
				ret.actionDay(LocalDate.now());
				// Mark.
				ret.isRealTime(true);
			}
			
			return ret;
		}
	}
}

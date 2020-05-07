package com.nabiki.corona.candle.core;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.Utils;
import com.nabiki.corona.api.CandleMinute;
import com.nabiki.corona.kernel.DefaultDataFactory;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerCandle;
import com.nabiki.corona.kernel.api.KerTick;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class CandleGenerator {
	private final String symbol;
	private final RuntimeInfo info;
	private final Map<Integer, RuntimeCandle> candles = new ConcurrentHashMap<>();
	
	private static int[] periods = new int[] { CandleMinute.MINUTE, CandleMinute.FIVE_MINUTE, CandleMinute.QUARTER,
			CandleMinute.HALF_HOUR, CandleMinute.HALF_QUADTER_HOUR, CandleMinute.HOUR, CandleMinute.TWO_HOUR};
	
	public CandleGenerator(String symbol, RuntimeInfo info) {
		this.symbol = symbol;
		this.info = info;
		
		// Initialize runtime candles.
		for(var p : CandleGenerator.periods)
			this.candles.put(p, new RuntimeCandle(this.symbol, p));
	}
	
	public void tick(KerTick tick) {
		for (var c : this.candles.values())
			c.update(tick);
	}
	
	public KerCandle get(int min, Instant now) {
		if (!this.info.candleNow(this.symbol, min, now))
			return null;
		
		var rc = this.candles.get(min);
		if (rc == null)
			return null;
		
		return rc.pop();
	}
	
	public KerCandle peak(int minPeriod, Instant now) {
		var rc = this.candles.get(minPeriod);
		if (rc == null)
			return null;
		
		return rc.peak();
	}
	
	private class RuntimeCandle {
		private final int min;
		private final String symbol;
		private final DataFactory factory = DefaultDataFactory.create();
		
		// Candle info.
		private KerCandle runtime;
		private int currentVolume = 0;
		private int previousVolume = 0;
		private LocalDate tradingDay;
		private boolean popped;
		
		RuntimeCandle(String symbol, int min) {
			this.min = min;
			this.popped = true;
			this.symbol = symbol;
			this.runtime = this.factory.kerCandle();
			
			// Initialize runtime candle.
			this.runtime.highPrice(-Double.MAX_VALUE);
			this.runtime.lowPrice(Double.MAX_VALUE);
			this.runtime.symbol(this.symbol);
			this.runtime.minutePeriod(this.min);
			
			if (min == CandleMinute.DAY)
				this.runtime.isDay(true);
			else
				this.runtime.isDay(false);
		}
		
		void update(KerTick tick) {
			// Filter wrong symbol.
			if (tick.symbol().compareTo(this.symbol) != 0)
				return;
			
			synchronized(this.runtime) {
				if (this.popped) {
					this.runtime.openPrice(tick.lastPrice());
					this.runtime.highPrice(tick.lastPrice());
					this.runtime.lowPrice(tick.lastPrice());
					this.runtime.closePrice(tick.lastPrice());
					// Save trading day once per candle.
					this.tradingDay = tick.tradingDay();
					// Mark.
					this.popped = false;
				} else {
					this.runtime.closePrice(tick.lastPrice());
					this.runtime.highPrice(Math.max(this.runtime.highPrice(), tick.lastPrice()));
					this.runtime.lowPrice(Math.min(this.runtime.lowPrice(), tick.lastPrice()));
				}
				
				this.runtime.openInterest(tick.openInterest());
				this.runtime.updateTime(Instant.now());
				
				// Save volume.
				this.currentVolume = tick.volume();
			}
		}
		
		KerCandle pop() {
			var ret = peak();
			
			synchronized(this.runtime) {
				// Reset volume.
				this.previousVolume = this.currentVolume;
				this.popped = true;
			}
			
			return ret;
		}
		
		KerCandle peak() {
			var ret = this.factory.kerCandle();
			
			synchronized(this.runtime) {
				// Copy runtime candle.
				ret.symbol(this.runtime.symbol());
				ret.openPrice(this.runtime.openPrice());
				ret.highPrice(this.runtime.highPrice());
				ret.lowPrice(this.runtime.lowPrice());
				ret.closePrice(this.runtime.closePrice());
				ret.openInterest(this.runtime.openInterest());
				ret.updateTime(this.runtime.updateTime());
				
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

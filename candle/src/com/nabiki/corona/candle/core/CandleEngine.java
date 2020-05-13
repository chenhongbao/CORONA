package com.nabiki.corona.candle.core;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.api.CandleMinute;
import com.nabiki.corona.kernel.api.KerCandle;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerTick;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class CandleEngine implements Runnable {

	public final static int DEFAULT_PERIOD_MILLIS = 60 * 1000;

	private final RuntimeInfo runtime;
	private final CandleEngineListener listener;

	// Candle periods.
	private static int[] periods = new int[] { CandleMinute.MINUTE, CandleMinute.FIVE_MINUTE, CandleMinute.QUARTER,
			CandleMinute.HALF_HOUR, CandleMinute.HALF_QUADTER_HOUR, CandleMinute.HOUR, CandleMinute.TWO_HOUR,
			CandleMinute.DAY };

	// Candle generators.
	private final Map<String, CandleGenerator> candles = new ConcurrentHashMap<>();

	// Working mark
	private AtomicBoolean working = new AtomicBoolean(false);

	public CandleEngine(CandleEngineListener l, RuntimeInfo info) throws KerError {
		if (l == null || info == null)
			throw new KerError("Invalid parameters.");

		this.listener = l;
		this.runtime = info;
	}

	private void initCandleGen() throws KerError {
		// Create candle generators.
		for (var s : this.runtime.symbols()) {
			this.candles.put(s, new CandleGenerator(s, this.runtime));
		}
	}
	
	private void destroyCandleGen() {
		this.candles.clear();
	}

	public void state(boolean working) {
		this.working.set(working);
	}

	@Override
	public void run() {
		// Don't generate candles when market is not working.
		if (!this.working.get())
			return;
		
		// Get time and use it across this run().
		Instant now = Instant.now();
		
		// Initialize candle generators when market open.
		if (this.runtime.isMarketOpen(now) && this.candles.isEmpty()) {
			this.listener.error(new KerError(ErrorCode.NONE, "Initialize candle generators."));
			try {
				initCandleGen();
			} catch (KerError e) {
				this.listener.error(e);
			}
		}

		// Try generating candles.
		for (int period : CandleEngine.periods) {
			for (var g : this.candles.values()) {
				try {
					var c = g.get(period, now);
					if (c != null)
						callListener(c);
				} catch (KerError e) {
					this.listener.error(e);
				}
			}
		}
		
		// Destroy candle generators when market closes.
		if (!this.runtime.isMarketOpen(now) && !this.candles.isEmpty()) {
			destroyCandleGen();
			this.listener.error(new KerError(ErrorCode.NONE, "Destroy candle generators."));
		}
	}

	private void callListener(KerCandle c) {
		if (this.listener != null) {
			try {
				this.listener.candle(c);
			} catch (Exception e) {
				this.listener.error(new KerError(e));
			}
		}	
	}

	public void tick(KerTick t) throws KerError {
		if (t == null || t.symbol() == null)
			throw new KerError("Tick or symbol null pointer.");
		
		var g = this.candles.get(t.symbol());
		if (g == null)
			throw new KerError("Candle generator for " + t.symbol() + " not found.");
		
		// Update tick into candle generator.
		g.tick(t);
	}
}

package com.nabiki.corona.candle.core;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nabiki.corona.api.CandleMinute;
import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerCandle;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.SymbolQuery;

public class CandleEngine implements Runnable {

	public final static int DEFAULT_PERIOD_MILLIS = 60 * 1000;

	private final Collection<SymbolQuery> queries;
	private final CandleEngineListener listener;

	// Candle periods.
	private static int[] periods = new int[] { CandleMinute.MINUTE, CandleMinute.FIVE_MINUTE, CandleMinute.QUARTER,
			CandleMinute.HALF_HOUR, CandleMinute.HALF_QUADTER_HOUR, CandleMinute.HOUR, CandleMinute.TWO_HOUR,
			CandleMinute.DAY };

	// Candle generators.
	private final Map<String, CandleGenerator> candles = new ConcurrentHashMap<>();

	// Working mark
	private AtomicBoolean working = new AtomicBoolean(false);

	public CandleEngine(CandleEngineListener l, Collection<SymbolQuery> queries) throws KerError {
		if (l == null || queries == null || queries.size() == 0)
			throw new KerError("Invalid parameters.");

		this.listener = l;
		this.queries = queries;
		initCandleGen();
	}

	private void initCandleGen() {
		// Get all unique symbols.
		var symbols = new HashSet<String>();
		var symbolQuery = new HashMap<String, SymbolQuery>();

		for (var q : this.queries) {
			symbols.addAll(q.symbols());

			for (var s : q.symbols())
				symbolQuery.put(s, q);
		}

		// Create candle generators.
		for (var s : symbols) {
			this.candles.put(s, new CandleGenerator(s, symbolQuery.get(s)));
		}
	}

	public void state(boolean working) {
		this.working.set(working);
	}

	@Override
	public void run() {
		// Get time and use it across this run().
		Instant now = Instant.now();

		// Try generating candles.
		for (int period : this.periods) {
			for (var g : this.candles.values()) {
				KerCandle c = g.get(period, now);
				if (c != null)
					callListener(c);
			}
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

	public void tick(Tick t) throws KerError {
		if (t == null || t.symbol() == null)
			throw new KerError("Tick or symbol null pointer.");
		
		var g = this.candles.get(t.symbol());
		if (g == null)
			throw new KerError("Candle generator for " + t.symbol() + " not found.");
		
		// Update tick into candle generator.
		g.tick(t);
	}
}

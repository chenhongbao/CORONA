package com.nabiki.corona.candle.core;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.SymbolQuery;

public class CandleEngine implements Runnable {

	public final static int DEFAULT_PERIOD_MILLIS = 60 * 1000;

	private Collection<SymbolQuery> queries;
	private CandleEngineListener listener;

	// Working mark
	private AtomicBoolean working = new AtomicBoolean(false);

	public CandleEngine(CandleEngineListener l, Collection<SymbolQuery> queries) {
		this.listener = l;
		this.queries = queries;
	}

	public void state(boolean working) {
		this.working.set(working);
	}

	@Override
	public void run() {
		// TODO generate candle
	}

	public void tick(Tick t) throws KerError {
		// TODO process tick
	}
}

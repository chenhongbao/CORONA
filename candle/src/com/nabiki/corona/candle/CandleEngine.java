package com.nabiki.corona.candle;

import java.util.Collection;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.biz.api.CandleInstantQuery;

public class CandleEngine implements Runnable {
	
	public final static int DEFAULT_PERIOD_MILLIS = 60 * 1000;
	
	private Collection<CandleInstantQuery> queries;
	private CandleEngineListener listener;

	public CandleEngine(CandleEngineListener l, Collection<CandleInstantQuery> queries) {
		this.listener = l;
		this.queries = queries;
	}

	@Override
	public void run() {
		// TODO generate candle
	}

	public void tick(Tick t) throws KerError {
		// TODO process tick
	}
}

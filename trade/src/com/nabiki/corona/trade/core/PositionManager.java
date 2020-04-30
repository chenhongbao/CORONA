package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.trade.RuntimeInfo;

public class PositionManager {
	private final Map<String, PositionEngine> positions = new ConcurrentHashMap<>();
	private final RuntimeInfo runtime;
	
	PositionManager(RuntimeInfo runtime) {
		this.runtime = runtime;
	}
	
	PositionEngine getPositon(String symbol) {
		return this.positions.get(symbol);
	}
	
	void setPosition(String symbol) {
		if (this.positions.containsKey(symbol))
			return;
		
		try {
			this.positions.put(symbol, new PositionEngine(symbol, this.runtime, null));
		} catch (KerError e) {
			// Since init runtime position detail is null, no exception is thrown.
		}
	}
}
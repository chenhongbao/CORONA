package com.nabiki.corona.trade.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class PositionManager {
	private final Map<String, PositionEngine> positions = new ConcurrentHashMap<>();
	private final RuntimeInfo runtime;
	private final DataFactory factory;
	
	PositionManager(RuntimeInfo runtime, DataFactory factory) {
		this.runtime = runtime;
		this.factory = factory;
	}
	
	PositionEngine getPositon(String symbol) {
		return this.positions.get(symbol);
	}
	
	void setPosition(String symbol) {
		if (this.positions.containsKey(symbol))
			return;
		
		try {
			this.positions.put(symbol, new PositionEngine(symbol, this.runtime, null, this.factory));
		} catch (KerError e) {
			// Since init runtime position detail is null, no exception is thrown.
		}
	}
}
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
		
		// TODO try initialize positions
	}
	
	PositionEngine getPositon(String symbol) {
		return this.positions.get(symbol);
	}
}
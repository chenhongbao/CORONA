package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class PositionManager {
	private final Map<String, PositionEngine> positions = new ConcurrentHashMap<>();
	private final RuntimeInfo runtime;
	private final DataFactory factory;
	
	public PositionManager(RuntimeInfo runtime, DataFactory factory) {
		this.runtime = runtime;
		this.factory = factory;
		
		// TODO try initialize positions
	}
	
	public PositionEngine getPositon(String symbol) {
		return this.positions.get(symbol);
	}
	
	public Collection<PositionEngine> positions() {
		return this.positions.values();
	}
	
	public void settle() {
		// TODO settle positions.
		//      after settlement, the positions are in settled status with valid settlement price and updated
		//      margin/profits.
	}
	
	public void init() {
		// TODO before calling init, positions are in settled state.
		//      the method prepare data for a new trading day, includes setting preXxx fields, clear settlement data...
	}
	
	public boolean isSettled() {
		// TODO check settlement state
		return false;
	}
}
package com.nabiki.corona.trade.core;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class PositionManager {
	private final Map<String, PositionEngine> positions = new ConcurrentHashMap<>();
	private final Path directory;
	private final RuntimeInfo runtime;
	private final DataFactory factory;
	
	// Settlement mark.
	private boolean isSettled = false;
	
	public PositionManager(Path dir, RuntimeInfo runtime, DataFactory factory) {
		this.directory = dir;
		this.runtime = runtime;
		this.factory = factory;
		
		// TODO try initialize positions
	}
	
	public PositionEngine getPositon(String symbol) {
		return this.positions.get(symbol);
	}
	
	public void setPosition(String symbol) throws KerError {
		// Set new position engine.
		this.positions.put(symbol, new PositionEngine(symbol, this.runtime, null, this.factory));
	}
	
	public Collection<PositionEngine> positions() {
		return this.positions.values();
	}
	
	public void settle() throws KerError {
		// After settlement, the positions are in settled status with valid settlement price and update margin/profits.
		for (var p : this.positions.values()) {
			var tick = this.runtime.lastTick(p.symbol());
			if (tick == null || !Utils.validPrice(tick.settlementPrice()))
				throw new KerError("Settlement price not ready: " + tick.symbol());
		}
		
		// All settlement prices must be ready before settling.
		for (var p : this.positions.values()) {
			p.settle(this.runtime.lastTick(p.symbol()).settlementPrice());
			p.write(new PositionFile(p.symbol(), posDir(this.directory, p.symbol()), this.runtime, this.factory));
		}
		
		this.isSettled = true;
	}
	
	
	
	public void init() throws KerError {
		for (var p : this.positions.values()) {
			p.read(new PositionFile(p.symbol(), posDir(this.directory, p.symbol()), this.runtime, this.factory));
			p.init();
		}
		
		this.isSettled = false;
	}
	
	public boolean isSettled() {
		return this.isSettled;
	}
	
	private Path posDir(Path dir, String symbol) {
		return Path.of(dir.toAbsolutePath().toString(), "/" + symbol + "/");
	}
}
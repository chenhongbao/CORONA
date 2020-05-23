package com.nabiki.corona.info.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.nabiki.corona.object.DefaultDataCodec;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.info.api.ProductTradingTimeSet;

public class CandleTime {
	// Global setting.
	public static String timeFile = "product_time.json";

	// Product trading time.
	private final Map<String, TimeList> productInstants = new ConcurrentHashMap<>();
	private final Map<String, TimeList> candleInstants = new ConcurrentHashMap<>();
	private LocalDateTime instantUpdateTime;

	// Preserve symbols.
	private Set<String> symbols = new ConcurrentSkipListSet<>();

	public CandleTime(Path root) throws KerError {
		loadProductTradingTime(root);
	}

	// Load trading time from external file and process it into candle instants.
	private void loadProductTradingTime(Path root) throws KerError {
		var fp = Path.of(root.toAbsolutePath().toString(), CandleTime.timeFile);

		try (InputStream is = new FileInputStream(fp.toFile())) {
			// Load JSON.
			var timeSet = DefaultDataCodec.create().decode(is.readAllBytes(), ProductTradingTimeSet.class);

			// Process time to candle instants.
			for (var set : timeSet.productTimes()) {
				var ins = new TimeList(set.times());
				for (var s : set.products())
					this.productInstants.put(s, ins);
			}

			this.instantUpdateTime = timeSet.updateTime();
		} catch (IOException e) {
			throw new KerError("Fail loading product trading time from file: " + fp.toAbsolutePath().toString());
		}
	}

	/**
	 * Filter the option and comb and only accept futures.
	 * 
	 * @param product product id
	 * @param symbol symbol
	 * @throws KerError throw exception if candle instant for the given product not found
	 */
	public void denoteSymbol(String product, String symbol) throws KerError {
		var ins = this.productInstants.get(product);
		if (ins == null)
			throw new KerError("Candle instant for product missing: " + product);

		this.candleInstants.put(symbol, ins);
		this.symbols.add(symbol);
	}

	public LocalDateTime timeStamp() {
		return this.instantUpdateTime;
	}
	
	private TimeList getInstantList(String symbol) throws KerError {
		var l = this.candleInstants.get(symbol);
		if (l == null)
			throw new KerError("Candle instant for symbol not found: " + symbol);
		
		return l;
	}

	public boolean hitSymbolCandle(String s, int minPeriod, Instant now) throws KerError {
		if (s == null)
			return false;
		
		return getInstantList(s).hit(minPeriod, now);
	}

	public Set<String> symbols() {
		return this.symbols;
	}
	
	public LocalTime firstCandleTime(String symbol, int min) throws KerError {
		return getInstantList(symbol).firstOfDay(min);
	}
	
	public LocalTime lastCandleTime(String symbol, int min) throws KerError {
		return getInstantList(symbol).lastOfDay(min);
	}
}

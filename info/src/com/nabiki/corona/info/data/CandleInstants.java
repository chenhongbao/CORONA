package com.nabiki.corona.info.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;

import com.nabiki.corona.kernel.api.KerError;

public class CandleInstants {
	// Global setting.
	public static String timeFile = "product_time.json";

	// Product trading time.
	private final Map<String, InstantList> productInstants = new ConcurrentHashMap<>();
	private final Map<String, InstantList> candleInstants = new ConcurrentHashMap<>();
	private LocalDateTime instantUpdateTime;

	// Preserve symbols.
	private Set<String> symbols = new ConcurrentSkipListSet<>();

	public CandleInstants(Path root) throws KerError {
		loadProductTradingTime(root);
	}

	// Load trading time from external file and process it into candle instants.
	private void loadProductTradingTime(Path root) throws KerError {
		var fp = Path.of(root.toAbsolutePath().toString(), CandleInstants.timeFile);

		try {
			// Load JSON.
			var builder = JsonbBuilder.create();
			var timeSet = builder.fromJson(new FileInputStream(fp.toFile()), ProductTradingTimeSet.class);

			// Process time to candle instants.
			for (var set : timeSet.productTimes) {
				var ins = new InstantList(set.times);
				for (var s : set.products)
					this.productInstants.put(s, ins);
			}

			this.instantUpdateTime = timeSet.updateTime;
		} catch (JsonbException | FileNotFoundException e) {
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

	public boolean hitSymbolCandle(String s, int minPeriod, Instant now, int margin, TimeUnit unit) throws KerError {
		if (s == null)
			return false;

		var l = this.candleInstants.get(s);
		if (l == null)
			throw new KerError("Candle instant for symbol not found: " + s);

		return l.hit(minPeriod, now, margin, unit);
	}

	public Set<String> symbols() {
		return this.symbols;
	}
}

package com.nabiki.corona.info;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.ProductClass;
import com.nabiki.corona.api.CandleMinute;
import com.nabiki.corona.info.data.CandleTime;
import com.nabiki.corona.kernel.DefaultDataCodec;
import com.nabiki.corona.kernel.api.DataCodec;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;
import com.nabiki.corona.kernel.api.KerTick;
import com.nabiki.corona.kernel.settings.api.MarketTimeSet;
import com.nabiki.corona.kernel.settings.api.RemoteConfigSet;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

@Component
public class RuntimeInfoService implements RuntimeInfo {
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// Information keepers.
	private boolean instLast = true;
	private final Map<String, KerInstrument> instruments = new ConcurrentHashMap<>();
	private final Map<String, KerMargin> margins = new ConcurrentHashMap<>();
	private final Map<String, KerCommission> commissions = new ConcurrentHashMap<>();
	
	// Glocal setting.
	private final static Path configRoot = Path.of(".", "configuration");
	private final static String mktTimeFile = "market_time.json";
	private final static String remoteConfigFile = "remote_config.json";
	
	// Codec.
	private final DataCodec codec = DefaultDataCodec.create();
	
	// Candle instants.
	private CandleTime candleInstants;
	
	// Market time.
	//private MarketTimeSet marketTime;
	
	// Remote configuration.
	//private RemoteConfigSet remoteConfigs;
	
	// Last tick preserve.
	private Map<String, KerTick> ticks = new ConcurrentHashMap<>();
	
	// Trading day.
	private LocalDate tradingDay;

	public RuntimeInfoService() {
	}
	
	private void createCandleInstants() {
		try {
			this.candleInstants = null;
			this.candleInstants = new CandleTime(RuntimeInfoService.configRoot);
			this.log.info("Create candle instants.");
		} catch (KerError e) {
			this.log.error("Fail initializing candle instants. {}", e.getMessage(), e);
		}
	}
	
	private MarketTimeSet loadMarketTime(Path root) {
		var fp = Path.of(RuntimeInfoService.configRoot.toAbsolutePath().toString(), RuntimeInfoService.mktTimeFile);
		 try (InputStream is = new FileInputStream(fp.toFile())) {
			 return  DefaultDataCodec.create().decode(is.readAllBytes(), MarketTimeSet.class);
		 } catch (KerError | IOException e) {
			this.log.warn("Fail loading market time config: {}. {}", fp.toAbsolutePath().toString(), e.getMessage(), e);
			return null;
		}
	}
	
	private RemoteConfigSet loadRemoteConfig(Path root) {
		Path file = Path.of(root.toAbsolutePath().toString(), RuntimeInfoService.remoteConfigFile);
		try (InputStream is = new FileInputStream(file.toFile())){
			return this.codec.decode(is.readAllBytes(), RemoteConfigSet.class);
		} catch (KerError | IOException e) {
			this.log.warn("Fail loading remote server configurations: {}.", file.toAbsolutePath(), e);
			return null;
		}
	}

	/**
	 * Check if the information of the given symbol are all ready.
	 * 
	 * @param symbol symbol
	 * @return true if all info are ready, false otherwise.
	 */
	@Override
	public boolean ready(String symbol) {
		return instrument(symbol) != null && margin(symbol) != null && commission(symbol) != null && lastTick(symbol) != null;
	}

	@Override
	public void instrument(KerInstrument in, boolean last) {
		// Filter non-future instruments.
		if (in.productClass() != ProductClass.Futures)
			return;
		
		if (in == null || in.symbol() == null)
			this.log.warn("kernel instrument null pointer.");
		
		// Clear old data when new data arrives.
		if (this.instLast) {
			this.instruments.clear();
			createCandleInstants();
		}

		this.instruments.put(in.symbol(), in);
		this.instLast = last;
		
		// Update symbol into candle instants.
		try {
			this.candleInstants.denoteSymbol(in.productId(), in.symbol());
		} catch (KerError e) {
			this.log.warn("Fail updating symbol {} into candle instants. {}", in.symbol(), e.getMessage(), e);
		}
		
		// Print message.
		if (last)
			this.log.info("Complete instrument configuration.");
	}

	@Override
	public void margin(KerMargin margin) {
		if (margin == null || margin.symbol() == null)
			this.log.warn("Kenerl margin null pointer.");
			
		this.margins.put(margin.symbol(), margin);
	}

	@Override
	public void commission(KerCommission comm) {
		if (comm == null || comm.symbol() == null)
			this.log.warn("Kenerl commission null pointer.");
		
		this.commissions.put(comm.symbol(), comm);
	}

	@Override
	public KerInstrument instrument(String symbol) {
		return this.instruments.get(symbol);
	}

	@Override
	public KerMargin margin(String symbol) {
		return this.margins.get(symbol);
	}

	@Override
	public KerCommission commission(String symbol) {
		return this.commissions.get(symbol);
	}

	@Override
	public KerTick lastTick(String symbol) {
		if (symbol == null)
			return null;
		
		return this.ticks.get(symbol);
	}

	@Override
	public String name() {
		return "Global Runtime";
	}

	@Override
	public LocalDate tradingDay() {
		return this.tradingDay;
	}

	@Override
	public boolean candleNow(String symbol, int min, Instant now) {
		try {
			return this.candleInstants.hitSymbolCandle(symbol, min, now);
		} catch (KerError e) {
			this.log.error("Fail checking candle instants for symbol {} and minute period {}.", symbol, min);
			return false;
		}
	}

	@Override
	public Collection<String> symbols() {
		return this.candleInstants.symbols();
	}

	@Override
	public boolean isMarketOpen(Instant now) {
		// Reload market time per visit.
		var marketTime = loadMarketTime(RuntimeInfoService.configRoot);
		
		for(var t : marketTime.marketTimes()) {
			var from = t.from().toSecondOfDay();
			var to = t.to().toSecondOfDay();
			var ns = LocalTime.ofInstant(now, ZoneId.systemDefault()).toSecondOfDay();
			
			if (from < to) {
				if (from <= ns && ns < to)
					return true;
			} else {
				if (from <= ns || ns < to)
					return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean endOfDay(Instant now, String symbol) {
		LocalTime first, last;
		
		try {
			first = this.candleInstants.firstCandleTime(symbol, CandleMinute.MINUTE);
			last = this.candleInstants.lastCandleTime(symbol, CandleMinute.MINUTE);
		} catch (KerError e) {
			this.log.warn("Fail getting instant for symbol {}. {}", symbol, e.getMessage(), e);
			return false;
		}
		
		first = first.minusMinutes(1);
		var nowTime = LocalTime.ofInstant(now, ZoneId.systemDefault());
		
		if (first.isAfter(last)) {
			// Start trade at night and extends to next day.
			return nowTime.isAfter(last) && nowTime.isBefore(first);
		} else {
			// Day trade only.
			return nowTime.isAfter(last);
		}
	}

	@Override
	public RemoteConfigSet remoteConfig() {	
		// Reload config per visit.
		return loadRemoteConfig(RuntimeInfoService.configRoot);
	}

	@Override
	public void lastTick(KerTick tick) {
		if (tick == null || tick.symbol() == null)
			return;
		
		this.ticks.put(tick.symbol(), tick);
		
		if (this.tradingDay == null)
			this.tradingDay = tick.tradingDay();
	}
}

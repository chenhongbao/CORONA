package com.nabiki.corona.info;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.ProductClass;
import com.nabiki.corona.api.CandleMinute;
import com.nabiki.corona.api.Tick;
import com.nabiki.corona.info.data.CandleInstants;
import com.nabiki.corona.kernel.DefaultDataCodec;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;
import com.nabiki.corona.kernel.biz.api.TickLocal;
import com.nabiki.corona.kernel.biz.api.TradeRemote;
import com.nabiki.corona.kernel.settings.MarketTimeSet;
import com.nabiki.corona.kernel.settings.TimeRange;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

@Component
public class RuntimeInfoService implements RuntimeInfo {
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// Last ticks.
	@Reference(bind = "bindTickLocal", updated = "updatedTickLocal", unbind = "unbindTickLocal",
			policy = ReferencePolicy.DYNAMIC)
	private volatile Collection<TickLocal> ticks = new ConcurrentSkipListSet<>();

	public void bindTickLocal(TickLocal local) {
		if (local == null)
			return;

		this.ticks.add(local);
		this.log.info("Bind local tick: {}.", local.name());
	}

	public void updatedTickLocal(TickLocal local) {
		if (local == null)
			return;

		if (this.ticks.contains(local)) {
			this.log.info("Update local tick: {}.", local.name());
		}
	}

	public void unbindTickLocal(TickLocal local) {
		if (local == null)
			return;

		this.ticks.remove(local);
		this.log.info("Unbind local tick: {}.", local.name());
	}

	@Reference(bind = "bindTradeRemote", updated = "updatedTradeRemote", unbind = "unbindTradeRemote",
			policy = ReferencePolicy.DYNAMIC)
	private volatile TradeRemote tradeRemote;
	
	public void bindTradeRemote(TradeRemote remote) {
		if (remote == null)
			return;
		
		this.tradeRemote = remote;
		this.log.info("Bind trade remote: {}.", remote.name());
	}
	
	public void updatedTradeRemote(TradeRemote remote) {
		if (remote == null)
			return;
		
		this.tradeRemote = remote;
		this.log.info("Update trade remote: {}.", remote.name());
	}
	
	public void unbindTradeRemote(TradeRemote remote) {
		if (remote == null)
			return;
		
		this.tradeRemote = remote;
		this.log.info("Unbind trade remote: {}.", remote.name());
	}
	
	public TradeRemote tradeRemote() {
		return this.tradeRemote;
	}

	// Information keepers.
	private final Map<String, KerInstrument> instruments = new ConcurrentHashMap<>();
	private final Map<String, KerMargin> margins = new ConcurrentHashMap<>();
	private final Map<String, KerCommission> commissions = new ConcurrentHashMap<>();
	
	// Glocal setting.
	private final static Path configRoot = Path.of(".", "configuration");
	private final static String mktTimeFile = "market_time.json";
	
	// Candle instants.
	private CandleInstants candleInstants;
	
	// Market time.
	private MarketTimeSet marketTime;

	public RuntimeInfoService() {
		try {
			this.candleInstants = new CandleInstants(RuntimeInfoService.configRoot);
		} catch (KerError e) {
			this.log.error("Fail initializing candle instants. {}", e.getMessage(), e);
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
	public void instrument(KerInstrument in) {
		// Filter non-future instruments.
		if (in.productClass() != ProductClass.Futures)
			return;
		
		if (in == null || in.symbol() == null)
			this.log.warn("kernel instrument null pointer.");

		this.instruments.put(in.symbol(), in);
		
		// Update symbol into candle instants.
		try {
			this.candleInstants.denoteSymbol(in.productId(), in.symbol());
		} catch (KerError e) {
			this.log.warn("Fail updating symbol {} into candle instants. {}", in.symbol(), e.getMessage(), e);
		}
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
	public Tick lastTick(String symbol) {
		Tick ret = null;
		for (var t : ticks) {
			ret = t.last(symbol);
			if (ret != null)
				break;
		}

		return ret;
	}

	@Override
	public String name() {
		return "Global Runtime";
	}

	@Override
	public LocalDate tradingDay() {
		LocalDate day = null;
		for (var t : this.ticks) {
			if (t != null) {
				day = t.tradingDay();
				if (day != null)
					break;
			}
		}
		
		return day;
	}

	@Override
	public boolean candleNow(String symbol, int min, Instant now, int margin, TimeUnit marginUnit) {
		try {
			return this.candleInstants.hitSymbolCandle(symbol, min, now, margin, marginUnit);
		} catch (KerError e) {
			this.log.error("Fail checking candle instants for symbol {} and minute period {}.", symbol, min);
			return false;
		}
	}

	@Override
	public Collection<String> symbols() {
		return this.candleInstants.symbols();
	}
	
	private void loadMarketTime(Path root) {
		var fp = Path.of(RuntimeInfoService.configRoot.toAbsolutePath().toString(), RuntimeInfoService.mktTimeFile);
		 try (InputStream is = new FileInputStream(fp.toFile())) {
			 this.marketTime = DefaultDataCodec.create().decode(is.readAllBytes(), MarketTimeSet.class);
		 } catch (KerError | IOException e) {
			this.log.warn("Fail loading market time config: {}. {}", fp.toAbsolutePath().toString(), e.getMessage(), e);
		}
	}

	@Override
	public boolean isMarketOpen(Instant now) {
		if (this.marketTime == null)
			loadMarketTime(RuntimeInfoService.configRoot);
		
		for(var t : this.marketTime.marketTimes) {
			var from = t.from.toSecondOfDay();
			var to = t.to.toSecondOfDay();
			var ns = LocalTime.ofInstant(now, ZoneId.systemDefault()).toSecondOfDay();
			
			if (to <= ns && ns < from)
				return true;
		}
		
		return false;
	}

	@Override
	public boolean endOfDay(Instant now, String symbol) {
		Instant first, last;
		
		try {
			first = this.candleInstants.firstCandleInstant(symbol, CandleMinute.MINUTE);
			last = this.candleInstants.lastCandleInstant(symbol, CandleMinute.MINUTE);
		} catch (KerError e) {
			this.log.warn("Fail getting instant for symbol {}. {}", symbol, e.getMessage(), e);
			return false;
		}
		
		var firSec = LocalTime.ofInstant(first, ZoneId.systemDefault()).toSecondOfDay() - 60;
		var lstSec = LocalTime.ofInstant(last, ZoneId.systemDefault()).toSecondOfDay();
		var nowSec = LocalTime.ofInstant(now, ZoneId.systemDefault()).toSecondOfDay();
		
		if (firSec > lstSec) {
			// Start trade at night and extends to next day.
			return lstSec <= nowSec && nowSec < firSec;
		} else {
			// Day trade only.
			return lstSec <= nowSec;
		}
	}
}

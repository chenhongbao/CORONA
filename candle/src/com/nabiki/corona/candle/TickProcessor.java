package com.nabiki.corona.candle;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.candle.core.CandleEngine;
import com.nabiki.corona.candle.core.CandleEngineListener;
import com.nabiki.corona.kernel.api.KerCandle;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerTick;
import com.nabiki.corona.kernel.biz.api.TickCandleForwarder;
import com.nabiki.corona.kernel.biz.api.TickLocal;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

/**
 * Process tick into candle, and re-forward ticks and candles to client.
 * <p>
 * The processor exposes it interface to upstream to be injected ticks by simply not specifying any parameters for
 * annotation Component.
 * 
 * @author Hongbao Chen
 *
 */
@Component
public class TickProcessor implements TickLocal {
	// Use OSGi logging service
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// Runtime info.
	private volatile RuntimeInfo runtime;
	
	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindRuntimeInfo(RuntimeInfo info) {
		this.runtime = info;
		this.log.info("Bind runtime info: {}.", info.name());
	}
	
	public void unbindRuntimeInfo(RuntimeInfo info) {
		if (this.runtime == info) {
			this.runtime = null;
			this.log.info("Unbind runtime info: {}.", info.name());
		}
	}

	// Forwarder takes a tick or candle and forward it to clients.
	// The function is a service and can have multiple services with this type.
	@Reference(bind = "bindForwarder", updated = "updatedForwarder", unbind = "unbindForwarder",
			cardinality = ReferenceCardinality.AT_LEAST_ONE)
	volatile Collection<TickCandleForwarder> forwarders = new ConcurrentSkipListSet<>();

	public void bindForwarder(TickCandleForwarder forwarder) {
		if (forwarder == null)
			return;

		try {
			this.forwarders.add(forwarder);
			this.log.info("Bind forwarder: {}.", forwarder.name());
		} catch (ClassCastException e) {
			this.log.warn("Fail adding forwarder: {}.", forwarder.name());
		}
	}

	public void updatedForwarder(TickCandleForwarder forwarder) {
		if (forwarder == null)
			return;

		this.log.info("Update forwarder: {}.", forwarder.name());
		bindForwarder(forwarder);
	}

	public void unbindForwarder(TickCandleForwarder forwarder) {
		if (forwarder == null)
			return;

		try {
			this.forwarders.remove(forwarder);
			this.log.info("Unbind forwarder: {}.", forwarder.name());
		} catch (ClassCastException e) {
			this.log.warn("Fail removing forwarder: {}.", forwarder.name());
		}
	}

	// Candle engine.
	private ScheduledThreadPoolExecutor executor;
	private CandleEngine engine;

	// Last tick preserve.
	private Map<String, KerTick> lastTicks = new ConcurrentHashMap<>();
	
	private LocalDate tradingDay;
	
	public TickProcessor() {
	}

	@Activate
	public void start(ComponentContext ctx) {
		try {
			this.engine = new CandleEngine(new CandlePostListener(), this.runtime);
		} catch (KerError e) {
			this.log.error("Fail creating candle engine. {}", e.getMessage(), e);
			return;
		}
		
		this.executor = new ScheduledThreadPoolExecutor(4);

		// Delayed until next minute
		long remainMsInCurMin = 0;
		var elapseMsInCurMin = System.currentTimeMillis() % CandleEngine.DEFAULT_PERIOD_MILLIS;
		remainMsInCurMin = CandleEngine.DEFAULT_PERIOD_MILLIS - elapseMsInCurMin;

		try {
			this.executor.scheduleAtFixedRate(this.engine, remainMsInCurMin, CandleEngine.DEFAULT_PERIOD_MILLIS,
					TimeUnit.MILLISECONDS);
			this.log.info("Schedule candle engine.");
		} catch (RejectedExecutionException e) {
			this.log.warn("Fail scheduling candle engine. {}", e.getMessage());
		}
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		this.executor.remove(this.engine);

		try {
			this.executor.shutdown();
			if (!this.executor.awaitTermination(60, TimeUnit.SECONDS))
				this.log.warn("Timeout candle engine threadpool shutdown.");
		} catch (InterruptedException | SecurityException e) {
			this.log.warn("Fail shuting down candle engine threadpool.");
		}

		this.log.info("Stop candle engine.");
	}

	@Override
	public void tick(KerTick tick) {
		// Forward ticks.
		var iter = this.forwarders.iterator();
		while (iter.hasNext()) {
			iter.next().tick(tick);
		}

		// Process tick into candle.
		try {
			this.engine.tick(tick);
		} catch (KerError e) {
			log.warn("Fail processing tick. {}", e.getMessage());
		}
		
		// Keep the tick as last tick.
		this.lastTicks.put(tick.symbol(), tick);
		
		if (tradingDay() == null)
			this.tradingDay = tick.tradingDay();
	}

	private class CandlePostListener implements CandleEngineListener {

		CandlePostListener() {
		}

		@Override
		public void candle(KerCandle candle) {
			// Forward candles
			var iter = forwarders.iterator();
			while (iter.hasNext()) {
				iter.next().candle(candle);
			}
		}

		@Override
		public void error(KerError e) {
			if (e.code() == ErrorCode.NONE)
				log.info(e.getMessage());
			else
				log.warn("Fail processing candle. {}", e.getMessage());
		}

	}

	@Override
	public void isWorking(boolean working) {
		if (this.engine != null)
			this.engine.state(working);
	}

	@Override
	public String name() {
		return this.getClass().getName();
	}

	@Override
	public KerTick last(String symbol) {
		if (symbol == null)
			return null;
		
		return this.lastTicks.get(symbol);
	}

	@Override
	public LocalDate tradingDay() {
		return this.tradingDay;
	}
}

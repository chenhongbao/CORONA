package com.nabiki.corona.candle;

import java.util.Collection;
import java.util.Timer;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RejectedExecutionException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.candle.core.CandleEngine;
import com.nabiki.corona.candle.core.CandleEngineListener;
import com.nabiki.corona.candle.core.CandleServiceContext;
import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerTick;
import com.nabiki.corona.system.biz.api.TickCandleForwarder;
import com.nabiki.corona.system.biz.api.TickLocal;
import com.nabiki.corona.system.info.api.RuntimeInfo;

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
public class TickLocalService implements TickLocal {
	// Use OSGi logging service
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// Runtime info.
	private CandleServiceContext context = new CandleServiceContext();
	
	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindRuntimeInfo(RuntimeInfo info) {
		this.context.info(info);
		this.log.info("Bind runtime info: {}.", info.name());
	}
	
	public void unbindRuntimeInfo(RuntimeInfo info) {
		try {
			if (this.context.info() == info) {
				this.context.info(null);
				this.log.info("Unbind runtime info: {}.", info.name());
			}
		} catch (KerError e) {
			this.log.error("Fail unbinding runtime info. {}", e.message(), e);
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
	// Use timer because the scheduleAtFixedRate will concurrently execute tasks if previous task takes longer than
	// period to finish.
	private Timer timer;
	private CandleEngine engine;
	
	public TickLocalService() {
	}

	@Activate
	public void start(ComponentContext ctx) {
		// The runtime info ref is set via service context. It can be null pointer here but will be set in future.	
		try {
			this.engine = new CandleEngine(new CandlePostListener(), this.context);
		} catch (KerError e) {
			this.log.error("Fail creating candle engine. {}", e.getMessage(), e);
			return;
		}
		
		this.timer = new Timer(true);

		// Delayed until next minute
		long remainMsInCurMin = 0;
		var elapseMsInCurMin = System.currentTimeMillis() % CandleEngine.DEFAULT_PERIOD_MILLIS;
		remainMsInCurMin = CandleEngine.DEFAULT_PERIOD_MILLIS - elapseMsInCurMin;

		try {
			this.timer.scheduleAtFixedRate(this.engine, remainMsInCurMin, CandleEngine.DEFAULT_PERIOD_MILLIS);
			this.log.info("Schedule candle engine.");
		} catch (RejectedExecutionException e) {
			this.log.warn("Fail scheduling candle engine. {}", e.getMessage());
		}
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		this.timer.cancel();
		this.timer.purge();
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
		try {
			this.context.info().lastTick(tick);
		} catch (KerError e) {
			this.log.error("Fail setting last tick in runtime info. {}", e.message(), e);
		}
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
	public void marketWorking(boolean working) {
		if (this.engine != null)
			this.engine.state(working);
	}

	@Override
	public String name() {
		return this.getClass().getName();
	}
}

package com.nabiki.corona.candle;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.api.Candle;
import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.biz.api.CandleInstantQuery;
import com.nabiki.corona.kernel.biz.api.TickCandleForwarder;
import com.nabiki.corona.kernel.biz.api.TickLocal;

/**
 * Process tick into candle, and re-forward ticks and candles to client.
 * <p>
 * The processor exposes it interface to upstream to be injected ticks by simply
 * not specifying any parameters for annotation Component.
 * 
 * @author Hongbao Chen
 *
 */
@Component
public class TickProcessor implements TickLocal {
	// Use OSGi logging service
	private Logger log;
	
	// We don't need factory, but just logger. So we take factory and get a logger
	// from it.
	@Reference
	public void bindLogger(LoggerFactory factory) {
		this.log = factory.getLogger(this.getClass().getName());
	}
	
	// Previous reference annotation will generated unbinder by replacing binder's
	// prefix with `unbind`.
	public void unbindLogger(LoggerFactory factory) {
		this.log = null;
	}

	// Instant query has a set of time points when a candle of specific mins
	// is generated. This function is built into bundle and injected.
	@Reference(bind = "bindQuery", updated = "updatedQuery", unbind = "unbindQuery", 
			cardinality = ReferenceCardinality.AT_LEAST_ONE)
	volatile Collection<CandleInstantQuery> queries = new HashSet<>();

	public void bindQuery(CandleInstantQuery query) {
		if (query == null)
			return;
		
		try {
			this.queries.add(query);
			this.log.info("Bind candle instant query: {}.", query.name());
		} catch (ClassCastException e) {
			this.log.warn("Fail adding query: {}.", query.name());
		}
	}
	
	public void updatedQuery(CandleInstantQuery query) {
		if (query == null)
			return;
		
		this.log.info("Update candle instant query: {}.", query.name());
		bindQuery(query);
	}

	public void unbindQuery(CandleInstantQuery query) {
		if (query == null)
			return;

		try {
			this.queries.remove(query);
			this.log.info("Unbind candle instant query: {}.", query.name());
		} catch (ClassCastException e) {
			this.log.warn("Fail removing query: {}.", query.name());
		}
	}
	
	// Forwarder takes a tick or candle and forward it to clients.
	// The function is a service and can have multiple services with this type.
	@Reference(bind = "bindForwarder", updated = "updatedForwarder", 
			unbind = "unbindForwarder", cardinality = ReferenceCardinality.AT_LEAST_ONE)
	volatile Collection<TickCandleForwarder> forwarders = new HashSet<>();
	
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
	
	private ScheduledThreadPoolExecutor executor;
	private CandleEngine engine;

	public TickProcessor() {
	}

	@Activate
	public void start(ComponentContext ctx) {
		this.engine = new CandleEngine(new CandlePostListener(), this.queries);
		this.executor = new ScheduledThreadPoolExecutor(4);
		
		// Delayed until next minute
		long remainMsInCurMin = 0;
		var elapseMsInCurMin = System.currentTimeMillis() % CandleEngine.DEFAULT_PERIOD_MILLIS;
		remainMsInCurMin = CandleEngine.DEFAULT_PERIOD_MILLIS - elapseMsInCurMin;
		
		try {
			this.executor.scheduleAtFixedRate(this.engine, remainMsInCurMin, 
					CandleEngine.DEFAULT_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			this.log.info("Schedule candle engine.");
		} catch (RejectedExecutionException e) {
			this.log.warn("Fail scheduling candle engine. " + e.getMessage());
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
	public void onTick(Tick tick) {
		// Forward ticks
		var iter = this.forwarders.iterator();
		while (iter.hasNext()) {
			iter.next().forTick(tick);
		}

		// Process tick into candle
		try {
			this.engine.tick(tick);
		} catch (KerError e) {
			log.warn("Fail processing tick. " + e.getMessage());
		}
	}

	private class CandlePostListener implements CandleEngineListener {
		
		CandlePostListener() {
		}

		@Override
		public void onCandle(Candle candle) {
			// Forward candles
			var iter = forwarders.iterator();
			while (iter.hasNext()) {
				iter.next().forCandle(candle);
			}
		}

		@Override
		public void onError(KerError e) {
			log.warn("Fail processing candle. " + e.getMessage());
		}
		
	}
}

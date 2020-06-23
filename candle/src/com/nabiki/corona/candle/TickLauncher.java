package com.nabiki.corona.candle;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.candle.api.EngineAction;
import com.nabiki.corona.candle.api.EngineState;
import com.nabiki.corona.candle.api.TickEngine;
import com.nabiki.corona.candle.api.TickEngineListener;
import com.nabiki.corona.candle.core.*;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerTick;
import com.nabiki.corona.system.biz.api.TickLocal;
import com.nabiki.corona.system.info.api.RuntimeInfo;

@Component(service = {})
public class TickLauncher implements Runnable {
	// Use OSGi logging service.
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// User's note for different accounts.
	public final static String MD_USER_NOTE = "marketdata";

	// Local tick processor service.
	@Reference(bind = "bindTickLocal", updated = "updatedTickLocal", unbind = "unbindTickLocal",
			cardinality = ReferenceCardinality.AT_LEAST_ONE)
	volatile Collection<TickLocal> tickLocals = new ConcurrentSkipListSet<>();

	public void bindTickLocal(TickLocal local) {
		this.tickLocals.add(local);
		this.log.info("Bind tick local processor: {}.", local.name());
	}

	public void updatedTickLocal(TickLocal local) {
		this.tickLocals.add(local);
		this.log.info("Update tick local processor: {}.", local.name());
	}

	public void unbindTickLocal(TickLocal local) {
		if (this.tickLocals.remove(local))
			this.log.info("Unbind tick local processor: {}.", local.name());
	}

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
				this.log.info("Remove runtime info: {}.", info.name());
			}
		} catch (KerError e) {
			this.log.error("Fail unbinding runtime info. {}", e.message(), e);
		}
	}

	private TickEngine engine;
	private ScheduledThreadPoolExecutor executor;
	public final static int CHECK_PERIOD_MILLIS = 60 * 1000;

	public TickLauncher() {
	}

	@Activate
	public void start(ComponentContext ctx) {
		// Delayed until next minute
		long delayMillis = 0;
		var pastMillis = System.currentTimeMillis() % TimerTaskCandleEngine.DEFAULT_PERIOD_MILLIS;
		delayMillis = TimerTaskCandleEngine.DEFAULT_PERIOD_MILLIS - pastMillis;

		try {
			this.executor = new ScheduledThreadPoolExecutor(4);
			this.executor.scheduleAtFixedRate(this, delayMillis, TickLauncher.CHECK_PERIOD_MILLIS,
					TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			this.log.error("Fail scheduling tick launcher. {}", e.getMessage());
		}

		// Create tick engine.
		this.engine = new CtpTickEngine(new TickPostListener(), this.context);
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		this.executor.remove(this);

		try {
			this.executor.shutdown();
			if (!this.executor.awaitTermination(60, TimeUnit.SECONDS))
				this.log.warn("Timeout tick launcher threadpool shutdown.");
		} catch (InterruptedException | SecurityException e) {
			this.log.warn("Fail shuting down tick launcher threadpool.");
		}
	}

	@Override
	public void run() {
		EngineAction action = EngineAction.NONE;
		try {
			action = nextAction();
		} catch (KerError e) {
			this.log.warn("Fail deciding tick launcher action. {}", e.message(), e);
			return;
		}
		
		switch (action) {
		case START:
			try {
				this.engine.start();
			} catch (KerError e) {
				this.log.warn("Fail start tick engine. {}", e.getMessage());
			}

			break;
		case STOP:
			this.engine.stop();
			break;
		default:
			this.log.warn("Unhandled launching action: {}.", action);
			break;
		}
		
		// Check time and send remote with symbols to subscribe in next trading day. Must wait all symbols ready.
		if (this.engine.state() == EngineState.STARTED && minutesAfterMarketOpen(5)) {
			try {
				this.engine.sendSymbols();
			} catch (KerError e) {
				this.log.warn("Fail sending subscribed symbols to remote. {}", e.getMessage(), e);
			}
		}
	}

	// Check now is the N minutes after market opens.
	private boolean minutesAfterMarketOpen(int minutes) {
		minutes = Math.max(0, minutes);
		var before = Instant.now().minusSeconds(minutes * 60);
		try {
			return this.context.info().isMarketOpen(before);
		} catch (KerError e) {
			this.log.error("Fail check market open. {}", e.message(), e);
			return false;
		}
	}

	private EngineAction nextAction() throws KerError {
		EngineAction next = EngineAction.NONE;
		switch (this.engine.state()) {
		case STARTED:
			if (!this.context.info().isMarketOpen(Instant.now()))
				next = EngineAction.STOP;
			break;
		case STOPPED:
			if (this.context.info().isMarketOpen(Instant.now()))
				next = EngineAction.START;
			break;
		case STARTING:
		case STOPPING:
			break;
		default:
			this.log.warn("Unhandled launcher state: {}.", this.engine.state());
			break;
		}

		return next;
	}

	private class TickPostListener implements TickEngineListener {
		private boolean working = false;

		public TickPostListener() {
		}

		private boolean isWorkingTick(Instant update) {
			var diff = Instant.now().getEpochSecond() - update.getEpochSecond();
			return Math.abs(diff) < 60;
		}

		@Override
		public void tick(KerTick tick) {
			// Check and set working state in tick local.
			if (!this.working && isWorkingTick(tick.updateTime())) {
				this.working = true;
				for (var local : tickLocals)
					local.marketWorking(true);

				log.info("Tick remote starts working.");
			}

			for (var local : tickLocals) {
				local.tick(tick);
			}
		}

		@Override
		public void error(KerError e) {
			log.warn("Tick engine encounters error. {}", e.getMessage());
		}

		@Override
		public void state(EngineState s) {
			log.info("Tick engine state: {}.", s);

			// Set working state in tick local.
			if (s == EngineState.STOPPED) {
				this.working = false;
				for (var local : tickLocals)
					local.marketWorking(false);

				log.info("Tick remote stops working.");
			}
		}
	}
}

package com.nabiki.corona.candle;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;
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

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.candle.core.CandleEngine;
import com.nabiki.corona.candle.core.TickEngine;
import com.nabiki.corona.candle.core.TickEngineListener;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.biz.api.TickLocal;
import com.nabiki.corona.kernel.settings.api.BrokerAccount;
import com.nabiki.corona.kernel.settings.api.MarketTimeQuery;
import com.nabiki.corona.kernel.settings.api.NativeExecutableInfo;

@Component(service = {})
public class TickLauncher implements Runnable {
	// Internal states.
	private enum LauncherState {
		STARTING, STARTED, STOPPING, STOPPED
	}

	private enum LauncherAction {
		START, STOP, NONE
	}

	// Use OSGi logging service.
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// User's note for different accounts.
	public final static String TRADING_USER_NOTE = "trading";
	public final static String MD_USER_NOTE = "md";

	// Broker-end user accounts.
	private BrokerAccount tradeUser;
	private BrokerAccount mdUser;

	@Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
	public void addUser(BrokerAccount user) {
		switch (user.note().toLowerCase()) {
		case TickLauncher.MD_USER_NOTE:
			this.mdUser = user;
			break;
		case TickLauncher.TRADING_USER_NOTE:
			this.tradeUser = user;
			break;
		default:
			return;
		}

		this.log.info("Add broker({}) {} account: {}", user.broker(), user.note(), user.user());
	}

	public void removeUser(BrokerAccount user) {
		if (this.tradeUser != user && this.mdUser != user)
			return;

		if (this.tradeUser == user)
			this.tradeUser = null;
		else if (this.mdUser == user)
			this.mdUser = null;

		this.log.info("Remove broker({}) {} account: {}", user.broker(), user.note(), user.user());
	}

	// Market time query.
	private MarketTimeQuery mktQuery;

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindMktQuery(MarketTimeQuery query) {
		this.mktQuery = query;
		this.log.info("Bind market time query: " + query.name());
	}

	public void unbindMktQuery(MarketTimeQuery query) {
		if (this.mktQuery == query) {
			this.mktQuery = null;
			this.log.info("Unbind market time query: " + query.name());
		}
	}

	// Native executable launch info.
	private NativeExecutableInfo execInfo;

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindExecInfo(NativeExecutableInfo info) {
		this.execInfo = info;
		this.log.info("Bind native executable info: {}.", info.title());
	}

	public void unbindExecInfo(NativeExecutableInfo info) {
		if (this.execInfo == info) {
			this.execInfo = null;
			this.log.info("Unbind native executable info: {}.", info.title());
		}
	}

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

	// Scheduled executor as timer.
	private Future<?> tickFuture;
	private TickEngine engine;
	private ScheduledThreadPoolExecutor executor;
	public final static int CHECK_PERIOD_MILLIS = 60 * 1000;

	// Current state.
	private LauncherState state = LauncherState.STOPPED;

	public TickLauncher() {
	}

	@Activate
	public void start(ComponentContext ctx) {
		// Delayed until next minute
		long delayMillis = 0;
		var pastMillis = System.currentTimeMillis() % CandleEngine.DEFAULT_PERIOD_MILLIS;
		delayMillis = CandleEngine.DEFAULT_PERIOD_MILLIS - pastMillis;

		try {
			this.executor = new ScheduledThreadPoolExecutor(4);
			this.executor.scheduleAtFixedRate(this, delayMillis, TickLauncher.CHECK_PERIOD_MILLIS,
					TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			this.log.error("Fail scheduling tick launcher. {}", e.getMessage());
		}
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
		var action = nextAction();
		switch (action) {
		case START:
			if (!userReady()) {
				this.log.warn("Presiquites not ready before new launch.");
				break;
			}

			try {
				this.engine = new TickEngine(new TickPostListener(), this.tradeUser, this.mdUser, this.execInfo);
				this.tickFuture = this.executor.submit(this.engine);
				this.log.info("Launche tick engine.");
			} catch (RejectedExecutionException e) {
				this.log.warn("Fail submitting tick engine to scheduler. {}", e.getMessage());
			}

			break;
		case STOP:
			if (this.tickFuture.isDone()) {
				this.log.warn("Fail stopping tick engine for it is done.");
				break;
			}

			// Notify engine to stop and turn its state to stopping.
			this.engine.stopping();
			if (!this.tickFuture.cancel(true))
				this.log.warn("Fail canceling tick engine.");

			// Clear resources.
			this.executor.remove(this.engine);
			this.engine = null;
			break;
		default:
			this.log.warn("Unhandled launching action: {}.", action);
			break;
		}
	}

	private boolean userReady() {
		return this.mdUser != null && this.tradeUser != null && this.execInfo != null;
	}

	private LauncherAction nextAction() {
		LauncherAction next = LauncherAction.NONE;

		if (this.mktQuery != null) {
			switch (this.state) {
			case STARTED:
				if (this.mktQuery.closed(Instant.now()))
					next = LauncherAction.STOP;
				break;
			case STOPPED:
				if (this.mktQuery.open(Instant.now()))
					next = LauncherAction.START;
				break;
			case STARTING:
			case STOPPING:
			default:
				this.log.warn("Unhandled launcher state: {}.", this.state);
				break;
			}
		} else {
			this.log.warn("Missing market time query before checking next action.");
		}

		return next;
	}

	private class TickPostListener implements TickEngineListener {
		public TickPostListener() {
		}

		@Override
		public void tick(Tick tick) {
			for (var local : tickLocals) {
				local.tick(tick);
			}
		}

		@Override
		public void error(KerError e) {
			log.warn("Tick engine encounters error. {}", e.getMessage());
		}

	}
}

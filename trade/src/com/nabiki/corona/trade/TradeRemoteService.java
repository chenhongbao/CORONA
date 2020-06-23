package com.nabiki.corona.trade;

import java.time.LocalTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.OrderStatus;
import com.nabiki.corona.OrderSubmitStatus;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.biz.api.TradeLocal;
import com.nabiki.corona.system.biz.api.TradeRemote;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.object.*;
import com.nabiki.corona.trade.api.TradeEngineListener;
import com.nabiki.corona.trade.core.*;

@Component
public class TradeRemoteService implements TradeRemote {

	private class TradeMessageHandler implements TradeEngineListener {
		private boolean instLast = true;
		private Queue<KerInstrument> insts = new ConcurrentLinkedQueue<>();

		@Override
		public void orderStatus(KerOrderStatus status) {
			try {
				context.local().orderStatus(status);
			} catch (KerError e) {
				log.error("Fail updating order status. {}", e.message(), e);
			}
		}

		@Override
		public void tradeReport(KerTradeReport rep) {
			try {
				context.local().tradeReport(rep);
			} catch (KerError e) {
				log.error("Fail updating trade report. {}", e.message(), e);
			}
		}

		@Override
		public void account(KerAccount account) {
			try {
				context.local().account(account);
			} catch (KerError e) {
				log.error("Fail updating remote account. {}", e.message(), e);
			}
		}

		@Override
		public void position(KerPositionDetail pos, boolean last) {
			try {
				context.local().positionDetail(pos, last);
			} catch (KerError e) {
				log.error("Fail updating remote position details. {}", e.message(), e);
			}
		}

		@Override
		public void error(KerError error) {
			log.error("Operation error({}): {}", error.code(), error.message(), error);
		}

		@Override
		public void error(KerOrder order, KerError error) {
			log.error("Order request error({}): {}", error.code(), error.message(), error);

			// Cancel order.
			try {
				var status = factory.create(KerOrderStatus.class);

				// Build order status for the failed order.
				status.orderId(order.orderId());
				status.originalVolume(order.volume());
				status.tradedVolume(0);
				status.price(0.0D);
				status.updateTime(LocalTime.now());
				status.orderStatus((char) OrderStatus.CANCELED);
				status.orderSubmitStatus((char) OrderSubmitStatus.INSERT_REJECTED);

				// Call method.
				context.local().orderStatus(status);
			} catch (KerError e) {
				log.error("Fail canceling order: {}. {}", order.orderId(), e.message(), e);
			}
		}

		@Override
		public void remoteLogin(KerRemoteLoginReport rep) {
			// Repeatedly login on the same trading day, don't update.
			if (Utils.same(rep.tradingDay(), login.tradingDay()))
				return;

			login = rep;
			try {
				context.local().remoteLogin(rep);
			} catch (KerError e) {
				log.error("Fail notifying remote login. {}", e.message(), e);
			}
		}

		@Override
		public void remoteLogout() {
			try {
				context.local().remoteLogout();
			} catch (KerError e) {
				log.error("Fail notifying remote logout. {}", e.message(), e);
			}
		}

		@Override
		public void error(KerAction action, KerError error) {
			log.error("Action failed for order: {}. [{}]{}", action.orderId(), error.code(), error.message(), error);
		}

		@Override
		public void instrument(KerInstrument in, boolean last) {
			// Clear old data.
			if (this.instLast)
				this.insts.clear();

			this.instLast = last;
			this.insts.add(in);

			// If data all ready, last bit is true, call the setter.
			try {
				if (this.instLast && this.insts.size() > 0) {
					while (this.insts.size() > 1) {
						context.info().instrument(this.insts.poll(), false);
					}

					// Set the last piece of data with last bit set true.
					context.info().instrument(this.insts.poll(), true);
				}
			} catch (KerError e) {
				log.error("Fail setting instruments in runtime info. {}", e.message(), e);
			}
		}

		@Override
		public void margin(KerMargin m) {
			try {
				context.info().margin(m);
			} catch (KerError e) {
				log.error("Fail setting margins in runtime info. {}", e.message(), e);
			}
		}

		@Override
		public void commission(KerCommission c) {
			try {
				context.info().commission(c);
			} catch (KerError e) {
				log.error("Fail setting commissions in runtime info. {}", e.message(), e);
			}
		}

	}

	// Use OSGi logging service
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// Runtime info.
	private TradeServiceContext context = new TradeServiceContext();

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setInfo(RuntimeInfo info) {
		this.context.info(info);
		this.log.info("Set runtime info: {}.", info.name());
	}

	public void unsetInfo(RuntimeInfo info) {
		try {
			if (this.context.info() == info) {
				this.context.info(null);
				this.log.info("Unset runtime info: {}.", info.name());
			}
		} catch (KerError e) {
			this.log.error("Fail checking runtime info. {}", e.message(), e);
		}
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setTradeLocal(TradeLocal local) {
		this.context.local(local);
		this.log.info("Set trade local: {}.", local.name());
	}

	public void unsetTradeLocal(TradeLocal local) {
		try {
			if (local == this.context.local()) {
				this.context.local(null);
				this.log.info("Unset trade local: {}.", local.name());
			}
		} catch (KerError e) {
			this.log.error("Fail checking trader local. {}", e.message(), e);
		}
	}

	private TradeEngineListener engineListener = new TradeMessageHandler();
	private TradeLauncher launcher;
	private RequestQueue requestQueue;

	// Scheduled thread.
	private ScheduledThreadPoolExecutor executor;
	private final static int MINUTE_MILLIS = 60 * 1000;

	// Data factory.
	private final DataFactory factory = DefaultDataFactory.create();

	// Remote login rsp.
	private KerRemoteLoginReport login;

	public TradeRemoteService() {
	}

	@Override
	public String name() {
		return "trade_remote_service";
	}

	@Override
	public int order(KerOrder order) {
		try {
			return this.requestQueue.enqueue(new Request<KerOrder>(order));
		} catch (KerError e) {
			this.log.error("fail sending order {} under session {} for account. {}", order.orderId(), order.sessionId(),
					order.accountId(), e.message(), e);
			return -1;
		}
	}

	@Override
	public int instrument(String symbol) {
		try {
			// Set query.
			var val = this.factory.create(KerQueryInstrument.class);
			val.symbol(symbol);
			return this.requestQueue.enqueue(new Request<KerQueryInstrument>(val));
		} catch (KerError e) {
			this.log.error("Fail sending query instrument: {}. {}", symbol, e.message(), e);
			return -1;
		}
	}

	@Override
	public int margin(String symbol) {
		try {
			// Set query.
			var val = this.factory.create(KerQueryMargin.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			val.symbol(symbol);
			return this.requestQueue.enqueue(new Request<KerQueryMargin>(val));
		} catch (KerError e) {
			this.log.error("Fail sending query margin: {}. {}", symbol, e.message(), e);
			return -1;
		}
	}

	@Override
	public int commission(String symbol) {
		try {
			// Set query.
			var val = this.factory.create(KerQueryCommission.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			val.symbol(symbol);
			return this.requestQueue.enqueue(new Request<KerQueryCommission>(val));
		} catch (KerError e) {
			this.log.error("Fail sending query commission: {}. {}", symbol, e.message(), e);
			return -1;
		}
	}

	@Override
	public void account() {
		try {
			// Set query.
			var val = this.factory.create(KerQueryAccount.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			// The trade engine sets the currency id.
			this.requestQueue.enqueue(new Request<KerQueryAccount>(val));
		} catch (KerError e) {
			this.log.error("Fail sending query account. {}", e.message(), e);
		}
	}

	@Override
	public void position() {
		try {
			// Set query, query all position so the symbol is empty.
			var val = this.factory.create(KerQueryPositionDetail.class);
			this.requestQueue.enqueue(new Request<KerQueryPositionDetail>(val));
		} catch (KerError e) {
			this.log.error("Fail sending query position detail. {}", e.message(), e);
		}
	}

	@Override
	public int action(KerAction action) {
		try {
			return this.requestQueue.enqueue(new Request<KerAction>(action));
		} catch (KerError e) {
			this.log.error("Fails creating action request: {}. {}", action.orderId(), e.message(), e);
			return -1;
		}
	}

	@Activate
	public void start(ComponentContext ctx) {
		// Create launcher with given runtime info.
		this.launcher = new TradeLauncher(this.engineListener, this.context);

		// Create and run packet queue that schedules the packet to remote server.
		this.requestQueue = new RequestQueue(this.launcher);
		this.executor.execute(this.requestQueue);

		// Delayed until next minute
		var msToWait = TradeRemoteService.MINUTE_MILLIS
				- System.currentTimeMillis() % (TradeRemoteService.MINUTE_MILLIS);

		try {
			this.executor.scheduleAtFixedRate(this.launcher, msToWait, (TradeRemoteService.MINUTE_MILLIS),
					TimeUnit.MILLISECONDS);
			this.log.info("Schedule trade launcher.");
		} catch (RejectedExecutionException e) {
			this.log.warn("Fail scheduling trade launcher. {}", e.getMessage());
		}
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		this.executor.remove(this.launcher);

		// Stop packet queue.
		this.requestQueue.tellStop();
		this.executor.remove(this.requestQueue);

		try {
			this.executor.shutdown();
			if (!this.executor.awaitTermination(60, TimeUnit.SECONDS))
				this.log.warn("Timeout trade launcher threadpool shutdown.");
		} catch (InterruptedException | SecurityException e) {
			this.log.warn("Fail shuting down trade launcher threadpool.");
		}

		this.log.info("Stop trade launcher.");
	}
}

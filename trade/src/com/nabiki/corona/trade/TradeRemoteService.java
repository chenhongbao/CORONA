package com.nabiki.corona.trade;

import java.time.Instant;
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
import com.nabiki.corona.PacketType;
import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.DefaultDataCodec;
import com.nabiki.corona.kernel.DefaultDataFactory;
import com.nabiki.corona.kernel.api.DataCodec;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerAction;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerQueryAccount;
import com.nabiki.corona.kernel.api.KerQueryCommission;
import com.nabiki.corona.kernel.api.KerQueryInstrument;
import com.nabiki.corona.kernel.api.KerQueryMargin;
import com.nabiki.corona.kernel.api.KerQueryPositionDetail;
import com.nabiki.corona.kernel.api.KerRemoteLoginReport;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.biz.api.TradeLocal;
import com.nabiki.corona.kernel.biz.api.TradeRemote;
import com.nabiki.corona.kernel.packet.api.TxQueryAccountMessage;
import com.nabiki.corona.kernel.packet.api.TxRequestActionMessage;
import com.nabiki.corona.kernel.packet.api.TxQueryCommissionMessage;
import com.nabiki.corona.kernel.packet.api.TxQueryInstrumentMessage;
import com.nabiki.corona.kernel.packet.api.TxQueryMarginMessage;
import com.nabiki.corona.kernel.packet.api.TxRequestOrderMessage;
import com.nabiki.corona.kernel.packet.api.TxQueryPositionDetailMessage;
import com.nabiki.corona.kernel.tools.Packet;
import com.nabiki.corona.trade.core.PacketQueue;
import com.nabiki.corona.trade.core.TradeEngineListener;

@Component
public class TradeRemoteService implements TradeRemote {

	private class TradeMessageProcessor implements TradeEngineListener {

		@Override
		public void orderStatus(KerOrderStatus status) {
			// TODO order status

		}

		@Override
		public void tradeReport(KerTradeReport rep) {
			// TODO trade report

		}

		@Override
		public void account(KerAccount account) {
			// TODO account

		}

		@Override
		public void position(KerPositionDetail pos, boolean last) {
			local.positionDetail(pos, last);
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
				status.updateTime(Instant.now());
				status.orderStatus((char)OrderStatus.CANCELED);
				status.orderSubmitStatus((char)OrderSubmitStatus.INSERT_REJECTED);
				
				// Call method.
				local.orderStatus(status);
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
			local.login(rep);
		}

		@Override
		public void remoteLogout() {
			// TODO logout rsp

		}

		@Override
		public void error(KerAction action, KerError error) {
			log.error("Action failed for order: {}. [{}]{}", action.orderId(), error.code(), error.message(), error);		
		}

	}

	// Use OSGi logging service
	@Reference(service = LoggerFactory.class)
	private Logger log;
	
	// Trade local service.
	private volatile TradeLocal local;
	
	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setTradeLocal(TradeLocal local) {
		this.local = local;
		this.log.info("Set trade local: {}.", local.name());
	}
	
	public void unsetTradeLocal(TradeLocal local) {
		if (local == this.local) {
			this.local = null;
			this.log.info("Unset trade local: {}.", local.name());
		}
	}
	
	private final TradeEngineListener engineListener;
	private final TradeLauncher launcher;
	private final PacketQueue packetQueue;

	// Scheduled thread.
	private ScheduledThreadPoolExecutor executor;
	private final static int MINUTE_MILLIS = 60 * 1000;

	// Data factory.
	private final DataFactory factory = DefaultDataFactory.create();

	// Codec.
	private final DataCodec codec = DefaultDataCodec.create();

	// Remote login rsp.
	private KerRemoteLoginReport login;

	public TradeRemoteService() {
		this.engineListener = new TradeMessageProcessor();
		this.launcher = new TradeLauncher(this.engineListener);

		// Create and run packet queue that schedules the packet to remote server.
		this.packetQueue = new PacketQueue(this.launcher);
		this.executor.execute(this.packetQueue);
	}

	@Override
	public String name() {
		return "trade_remote_service";
	}

	@Override
	public int order(KerOrder order) {
		TxRequestOrderMessage req;
		try {
			req = this.factory.create(TxRequestOrderMessage.class);
			req.value(order);
			req.last(true);
			return this.packetQueue.enqueue(new Packet(PacketType.TX_REQUEST_ORDER, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("fail sending order request: {}. {}", order.orderId(), e.message(), e);
			return -1;
		}
	}

	@Override
	public int instrument(String symbol) {
		try {
			var req = this.factory.create(TxQueryInstrumentMessage.class);
			// Set query.
			var val = this.factory.create(KerQueryInstrument.class);
			val.symbol(symbol);
			// Set message.
			req.value(val);
			req.last(true);
			return this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_INSTRUMENT, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("Fail sending query instrument: {}. {}", symbol, e.message(), e);
			return -1;
		}
	}

	@Override
	public int margin(String symbol) {
		try {
			var req = this.factory.create(TxQueryMarginMessage.class);
			// Set query.
			var val = this.factory.create(KerQueryMargin.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			val.symbol(symbol);
			// Set message.
			req.value(val);
			req.last(true);
			return this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_MARGIN, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("Fail sending query margin: {}. {}", symbol, e.message(), e);
			return -1;
		}
	}

	@Override
	public int commission(String symbol) {
		try {
			var req = this.factory.create(TxQueryCommissionMessage.class);
			// Set query.
			var val = this.factory.create(KerQueryCommission.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			val.symbol(symbol);
			// Set message.
			req.value(val);
			req.last(true);
			return this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_COMMISSION, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("Fail sending query commission: {}. {}", symbol, e.message(), e);
			return -1;
		}
	}

	@Override
	public void account() {
		try {
			var req = this.factory.create(TxQueryAccountMessage.class);
			// Set query.
			var val = this.factory.create(KerQueryAccount.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			val.investorId("CNY");
			// Set message.
			req.value(val);
			req.last(true);
			this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_ACCOUNT, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("Fail sending query account. {}", e.message(), e);
		}	
	}

	@Override
	public void position() {
		try {
			var req = this.factory.create(TxQueryPositionDetailMessage.class);
			// Set query, query all position so the symbol is empty.
			var val = this.factory.create(KerQueryPositionDetail.class);
			val.brokerId(this.login.brokerId());
			val.investorId(this.login.userId());
			// Set message.
			req.value(val);
			req.last(true);
			this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_POSITION_DETAIL, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("Fail sending query position detail. {}", e.message(), e);
		}
	}

	@Override
	public int action(KerAction action) {
		try {
			var req = this.factory.create(TxRequestActionMessage.class);
			req.value(action);
			req.last(true);
			return this.packetQueue.enqueue(new Packet(PacketType.TX_REQUEST_ACTION, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("Fails creating action request: {}. {}", action.orderId(), e.message(), e);
			return -1;
		}
	}

	@Activate
	public void start(ComponentContext ctx) {
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

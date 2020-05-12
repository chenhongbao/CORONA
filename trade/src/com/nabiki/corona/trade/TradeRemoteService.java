package com.nabiki.corona.trade;

import java.time.LocalDate;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.PacketType;
import com.nabiki.corona.kernel.DefaultDataCodec;
import com.nabiki.corona.kernel.DefaultDataFactory;
import com.nabiki.corona.kernel.api.DataCodec;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerRemoteLoginReport;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.biz.api.TradeRemote;
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
		public void position(KerPositionDetail pos) {
			// TODO position

		}

		@Override
		public void error(KerError error) {
			// TODO error

		}

		@Override
		public void error(KerOrder order, KerError error) {
			// TODO order error

		}

		@Override
		public void remoteLogin(KerRemoteLoginReport rep) {
			loginReport = rep;

			// Set order ref.
			currentOrderReference = new AtomicInteger(0);
			currentOrderReference.set(rep.maxOrderReference());
		}

		@Override
		public void remoteLogout() {
			// TODO logout rsp

		}

	}

	// Use OSGi logging service
	@Reference(service = LoggerFactory.class)
	private Logger log;

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
	private KerRemoteLoginReport loginReport;
	private AtomicInteger currentOrderReference;

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
	public String nextOrderId() {
		if (this.currentOrderReference == null)
			return null;
		else
			return Integer.toString(this.currentOrderReference.incrementAndGet());
	}

	@Override
	public LocalDate tradingDay() {
		if (this.loginReport == null)
			return null;
		else
			return this.loginReport.tradingDay();
	}

	@Override
	public int order(KerOrder order) {
		try {
			return this.packetQueue.enqueue(new Packet(PacketType.TX_REQUEST_ORDER, this.codec.encode(order)));
		} catch (KerError e) {
			this.log.error("fail encoding order request: {}. {}.", order.orderId(), e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int instrument(String symbol) {
		var req = this.factory.kerRemoteRequest();
		req.value(symbol);

		try {
			return this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_INSTRUMENT, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("fail encoding query instrument: {}. {}.", symbol, e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int margin(String symbol) {
		var req = this.factory.kerRemoteRequest();
		req.value(symbol);

		try {
			return this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_MARGIN, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("fail encoding query margin: {}. {}.", symbol, e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public int commission(String symbol) {
		var req = this.factory.kerRemoteRequest();
		req.value(symbol);

		try {
			return this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_COMMISSION, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("fail encoding query commission: {}. {}.", symbol, e.getMessage(), e);
			return -1;
		}
	}

	@Override
	public void account() {
		try {
			this.packetQueue.enqueue(
					new Packet(PacketType.TX_QUERY_ACCOUNT, this.codec.encode(this.factory.kerRemoteRequest())));
		} catch (KerError e) {
			this.log.error("fail encoding query commission. {}.", e.getMessage(), e);
		}
	}

	@Override
	public void position() {
		try {
			this.packetQueue.enqueue(new Packet(PacketType.TX_QUERY_POSITION_DETAIL,
					this.codec.encode(this.factory.kerRemoteRequest())));
		} catch (KerError e) {
			this.log.error("fail encoding query position detail. {}.", e.getMessage(), e);
		}
	}

	@Override
	public int action(String orderId) {
		var req = this.factory.kerRemoteRequest();
		req.value(orderId);

		try {
			return this.packetQueue.enqueue(new Packet(PacketType.TX_REQUEST_ACTION, this.codec.encode(req)));
		} catch (KerError e) {
			this.log.error("fail encoding action request: {}. {}.", orderId, e.getMessage(), e);
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

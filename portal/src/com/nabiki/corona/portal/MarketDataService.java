package com.nabiki.corona.portal;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.object.*;
import com.nabiki.corona.portal.core.MarketDataManager;
import com.nabiki.corona.portal.core.MarketDataSubscriberListener;
import com.nabiki.corona.portal.inet.ClientInputAdaptor;
import com.nabiki.corona.portal.inet.PacketServer;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.biz.api.TickCandleForwarder;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.system.packet.api.RxCandleMessage;

@Component
public class MarketDataService implements TickCandleForwarder {
	
	class MarketDataHandler extends ClientInputAdaptor implements MarketDataSubscriberListener {
		public MarketDataHandler() {
		}

		@Override
		public void error(KerError e) {
			log.error("Market data subscription: {}", e.message(), e);
		}

		@Override
		public void error(KerError e, PacketServer server, MarketDataManager manager) {
			// Log error.
			if (server.isClosed()) {
				log.error("Market data peer closed before unsubscription: {}", e.message(), e);
				// Empty symbol removes all subscription under the given packet server.
				manager.unSubscribe(null, server);
			}
			else
				log.error("Market data subscription: {}", e.message(), e);
		}

		@Override
		public KerError subscribeSymbol(String symbol, PacketServer server) {
			var r = manager.subscribe(symbol, server);
			if (r.code() == 0)
				historyCandle(symbol, server);
			
			return r;
		}
		
		private void historyCandle(String symbol, PacketServer server) {
			try {
				// try query history candles for the given symbol, and send back.
				// message type is MessageType.RX_HISTORY_CANDLE.
				var rsp = factory.create(RxCandleMessage.class);
				var candles = manager.historyCandle(symbol);
				if (candles != null) {
					rsp.last(true);
					rsp.responseSeq(Utils.increaseGet());
					rsp.time(LocalDateTime.now());
					rsp.values(candles);
					// Encode.
					var bytes = codec.encode(rsp);
					// Send.
					server.send(MessageType.RX_HISTORY_CANDLE, bytes, 0, bytes.length);
				}
			} catch (KerError e) {
				log.error("Can't send history candles: {}. {}", symbol, e.message(), e);
			}
		}
	}
	
	@Reference(service = LoggerFactory.class)
	private Logger log;
	
	@Reference(bind = "bindRuntimeInfo", unbind = "unbindRuntimeInfo", policy = ReferencePolicy.DYNAMIC)
	private volatile RuntimeInfo info;

	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;

		this.info = info;
		this.log.info("Bind runtime info.");
		
		// Create market data manager.
		this.manager = new MarketDataManager(this.root, this.info, this.handler);
	}

	public void unbindRuntimeInfo(RuntimeInfo info) {
		if (this.info != info)
			return;

		this.info = null;
		this.log.info("Unbind runtime info.");
	}
	
	// Candle data files' root.
	private final Path root = Path.of(".", "candle");
	
	// Data factory.
	private final DataFactory factory = DefaultDataFactory.create();
	
	// Data encoder.
	private final DataCodec codec = DefaultDataCodec.create();
	
	// Market data.
	private MarketDataManager manager;
	private MarketDataHandler handler = new MarketDataHandler();
	
	public MarketDataService() {
	}
	
	@Override
	public String name() {
		return "market_data_forwarder";
	}

	@Override
	public void tick(KerTick tick) {
		this.manager.dispatch(tick);
	}

	@Override
	public void candle(KerCandle candle) {
		this.manager.dispatch(candle);
	}

	@Activate
	public void start(ComponentContext ctx) {	
		// TODO start
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		// TODO stop
	}
}

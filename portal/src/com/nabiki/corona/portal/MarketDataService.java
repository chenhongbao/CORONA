package com.nabiki.corona.portal;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.nabiki.corona.portal.core.PortalServiceContext;
import com.nabiki.corona.portal.inet.ClientInputAdaptor;
import com.nabiki.corona.portal.inet.ClientInputExecutor;
import com.nabiki.corona.portal.inet.MarketDataServer;
import com.nabiki.corona.portal.inet.PacketServer;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.biz.api.TickCandleForwarder;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.system.packet.api.RxCandleMessage;
import com.nabiki.corona.system.packet.api.RxErrorMessage;

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
			} else
				log.error("Market data subscription: {}", e.message(), e);
		}

		@Override
		public RxErrorMessage subscribeSymbol(String symbol, PacketServer server) {
			RxErrorMessage msg;
			try {
				msg = factory.create(RxErrorMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				
				var r = manager.subscribe(symbol, server);
				if (r.code() == 0)
					historyCandle(symbol, server);
				else
					msg.value(r);
			} catch (KerError e) {
				log.error("Fail subscribing symbols. {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
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

	private PortalServiceContext context = new PortalServiceContext();

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;

		this.context.info(info);
		this.log.info("Bind runtime info {}.", info.name());
	}

	public void unbindRuntimeInfo(RuntimeInfo info) {
		try {
			if (this.context.info() != info)
				return;
		} catch (KerError e) {
			this.log.error("Fail unbinding runtime info. {}", e.message(), e);
			return;
		}

		this.context.info(null);
		this.log.info("Unbind runtime info {}.", info.name());
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
	private final ClientInputExecutor executor = new ClientInputExecutor(handler);

	// Socket thread.
	private ServerSocket ss;
	private final ExecutorService threads = Executors.newCachedThreadPool();

	// Default listening port.
	public final static int port = 10688;

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
		// Create market data manager.
		this.manager = new MarketDataManager(this.root, this.context, this.handler);

		this.threads.execute(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ss = new ServerSocket(MarketDataService.port);
					while (!ss.isClosed()) {
						try {
							threads.execute(new MarketDataServer(ss.accept(), executor));
						} catch (IOException e) {
							log.warn("Fail accepting incoming connection. {}", e.getMessage(), e);
						} catch (KerError e) {
							log.error("Fail creating daemon for incoming connection. {}", e.message(), e);
						}
					}
				} catch (IOException e) {
					log.error("Fail listening on port: " + MarketDataService.port + ". " + e.getMessage());
				}
			}

		}));
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		if (!ss.isClosed())
			try {
				ss.close();
			} catch (IOException e) {
				log.error("Fail closing server socket." + e.getMessage());
			}
	}
}

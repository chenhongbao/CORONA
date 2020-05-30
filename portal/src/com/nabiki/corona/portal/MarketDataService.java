package com.nabiki.corona.portal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.client.api.Candle;
import com.nabiki.corona.client.api.Tick;
import com.nabiki.corona.portal.core.MarketDataManager;
import com.nabiki.corona.portal.core.MarketDataSubscriberListener;
import com.nabiki.corona.portal.inet.ClientInputAdaptor;
import com.nabiki.corona.portal.inet.PacketServer;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.biz.api.TickCandleForwarder;

@Component
public class MarketDataService implements TickCandleForwarder {
	
	class MarketDataHandler extends ClientInputAdaptor implements MarketDataSubscriberListener {
		private final MarketDataManager manager;
		
		public MarketDataHandler(MarketDataManager manager) {
			this.manager = manager;
		}

		@Override
		public void error(KerError e) {
			log.error("Market data subscription: {}", e.message(), e);
		}

		@Override
		public void error(KerError e, PacketServer server) {
			// Log error.
			if (server.isClosed()) {
				log.error("Market data peer closed before unsubscription: {}", e.message(), e);
				// Empty symbol removes all subscription under the given packet server.
				this.manager.unSubscribe(null, server);
			}
			else
				log.error("Market data subscription: {}", e.message(), e);
		}

		@Override
		public KerError subscribeSymbol(String symbol, PacketServer server) {
			return this.manager.subscribe(symbol, server);
		}
	}
	
	@Reference(service = LoggerFactory.class)
	private Logger log;

	public MarketDataService() {}
	
	@Override
	public String name() {
		// TODO name
		return null;
	}

	@Override
	public void tick(Tick tick) {
		// TODO tick

	}

	@Override
	public void candle(Candle candle) {
		// TODO candle

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

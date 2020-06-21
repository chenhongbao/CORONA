package com.nabiki.corona.trade;

import java.time.Instant;

import org.osgi.service.component.annotations.Component;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.trade.core.*;
import com.nabiki.corona.trade.core.TradeEngine.*;

@Component(service = {})
public class TradeLauncher implements Runnable {
	private final TradeEngine engine;
	private final TradeServiceContext context;
	private final TradeEngineListener listener;
	
	public TradeLauncher(TradeEngineListener listener, TradeServiceContext context) {
		this.context = context;
		this.listener = listener;
		this.engine = new TradeEngine(this.listener, this.context);
	}
	
	public TradeEngine remote() {
		return this.engine;
	}

	@Override
	public void run() {
		try {
			if (this.context.info().isMarketOpen(Instant.now())) {
				if (this.engine.state() == State.STOPPED) {
					this.engine.start();
				}
			} else {
				if (this.engine.state() == State.STARTED) {
					this.engine.stop();
				}
			}
		} catch (KerError e) {
			this.listener.error(e);
		}
	}
}

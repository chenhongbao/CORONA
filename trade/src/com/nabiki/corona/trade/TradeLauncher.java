package com.nabiki.corona.trade;

import java.time.Instant;

import org.osgi.service.component.annotations.Component;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.trade.core.TradeEngine;
import com.nabiki.corona.trade.core.TradeEngine.State;
import com.nabiki.corona.trade.core.TradeEngineErrorListener;
import com.nabiki.corona.trade.core.TradeEngineListener;
import com.nabiki.corona.trade.core.TradeServiceContext;

@Component(service = {})
public class TradeLauncher implements Runnable {
	private final TradeEngine engine;
	private final TradeServiceContext context;
	private final TradeEngineListener listener;
	private final TradeEngineErrorListener errorListener;
	
	// Engine thread.
	private Thread engineThread;
	
	public TradeLauncher(TradeEngineListener listener, TradeEngineErrorListener errorListener, TradeServiceContext context) {
		this.context = context;
		this.listener = listener;
		this.errorListener = errorListener;
		this.engine = new TradeEngine(this.listener, this.errorListener, this.context);
	}
	
	public TradeEngine remote() {
		return this.engine;
	}

	@Override
	public void run() {
		try {
			if (this.context.info().isMarketOpen(Instant.now())) {
				if (this.engine.state() == State.STOPPED) {
					this.engineThread = new Thread(this.engine);
					this.engineThread.start();
					this.engineThread.setDaemon(true);
					
					// Wait engine state changed to STARTED.
					while (this.engine.state() != State.STARTED)
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
				}
			} else {
				if (this.engine.state() == State.STARTED) {
					this.engine.tellStop();
					
					// Wait engine state changed to STOPPED.
					while (this.engine.state() != State.STOPPED)
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					
					// Clear thread.
					try {
						this.engineThread.join();
					} catch (InterruptedException e) {
					}
				}
			}
		} catch (KerError e) {
			this.errorListener.error(e);
		}
	}
}

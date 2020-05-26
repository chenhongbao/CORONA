package com.nabiki.corona.portal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.nabiki.corona.client.api.Candle;
import com.nabiki.corona.client.api.Tick;
import com.nabiki.corona.system.biz.api.TickCandleForwarder;

@Component
public class MarketDataService implements TickCandleForwarder {

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

package com.nabiki.corona.candle;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.nabiki.corona.api.Candle;
import com.nabiki.corona.api.Tick;
import com.nabiki.corona.system.biz.api.TickCandleForwarder;

@Component
public class TickCandlePortal implements TickCandleForwarder {

	@Override
	public String name() {
		return "tick_candle_portal";
	}

	@Override
	public void tick(Tick tick) {
		// TODO forward tick
	}

	@Override
	public void candle(Candle candle) {
		// TODO forward candle
	}

	@Activate
	public void start(ComponentContext ctx) {
		// TODO activate
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		// TODO stop
	}
}

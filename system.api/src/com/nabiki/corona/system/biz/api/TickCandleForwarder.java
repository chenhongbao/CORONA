package com.nabiki.corona.system.biz.api;

import com.nabiki.corona.client.api.Candle;
import com.nabiki.corona.client.api.Tick;

public interface TickCandleForwarder {
	String name();

	void tick(Tick tick);

	void candle(Candle candle);
}

package com.nabiki.corona.system.biz.api;

import com.nabiki.corona.api.Candle;
import com.nabiki.corona.api.Tick;

public interface TickCandleForwarder {
	String name();

	void tick(Tick tick);

	void candle(Candle candle);
}

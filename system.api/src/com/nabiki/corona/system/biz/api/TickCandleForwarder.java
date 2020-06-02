package com.nabiki.corona.system.biz.api;

import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerTick;

public interface TickCandleForwarder {
	String name();

	void tick(KerTick tick);

	void candle(KerCandle candle);
}

package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.Candle;
import com.nabiki.corona.api.Tick;

public interface TickCandleForwarder {
	String name();
	
	void forTick(Tick tick);
	
	void forCandle(Candle candle);
}

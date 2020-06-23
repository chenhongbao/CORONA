package com.nabiki.corona.candle.api;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerTick;

public interface TickEngineListener {
	void tick(KerTick tick);

	void error(KerError e);

	void state(EngineState s);
}

package com.nabiki.corona.candle.core;

import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerTick;

public interface TickEngineListener {
	void tick(KerTick tick);

	void error(KerError e);

	void state(EngineState s);
}

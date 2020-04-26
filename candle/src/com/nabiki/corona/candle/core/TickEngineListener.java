package com.nabiki.corona.candle.core;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerError;

public interface TickEngineListener {
	void tick(Tick tick);
	
	void error(KerError e);
}

package com.nabiki.corona.candle.api;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerTick;

public interface CandleEngine {

	void state(boolean working);

	void run();

	void tick(KerTick t) throws KerError;

}
package com.nabiki.corona.candle.core;

import com.nabiki.corona.api.Candle;
import com.nabiki.corona.kernel.api.KerError;

public interface CandleEngineListener {
	void candle(Candle candle);
	
	void error(KerError e);
}

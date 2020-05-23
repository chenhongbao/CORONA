package com.nabiki.corona.candle.core;

import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerError;

public interface CandleEngineListener {
	void candle(KerCandle candle);

	void error(KerError e);
}

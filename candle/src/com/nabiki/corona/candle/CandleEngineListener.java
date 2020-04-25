package com.nabiki.corona.candle;

import com.nabiki.corona.api.Candle;
import com.nabiki.corona.kernel.api.KerError;

public interface CandleEngineListener {
	void onCandle(Candle candle);
	
	void onError(KerError e);
}

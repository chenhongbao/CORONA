package com.nabiki.corona.trade.core;

import com.nabiki.corona.kernel.api.KerError;

public interface TradeEngineErrorListener {
	void error(KerError e);
}

package com.nabiki.corona.trade.core;

import com.nabiki.corona.system.api.KerError;

public interface TradeEngineErrorListener {
	void error(KerError e);
}

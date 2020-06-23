package com.nabiki.corona.trade.api;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.trade.core.Request;
import com.nabiki.corona.trade.core.CtpTradeEngine.State;

public interface TradeEngine {

	void stop();

	State state();

	void start() throws KerError;

	void send(Request<?> request) throws KerError;

}
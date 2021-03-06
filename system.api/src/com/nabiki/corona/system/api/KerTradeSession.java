package com.nabiki.corona.system.api;

import java.util.Collection;

import com.nabiki.corona.client.api.TradeSession;

public interface KerTradeSession extends TradeSession {
	KerTrade newKerTrade();

	KerTrade recallKerTrade(String id);

	KerAccount kerAccount();

	Collection<KerPositionDetail> kerPositions();

	Collection<KerPositionDetail> kerPositions(String symbol);
}

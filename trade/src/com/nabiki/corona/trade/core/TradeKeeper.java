package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerTradeReport;

public class TradeKeeper {
	private final Map<String, Set<KerTradeReport>> trades = new ConcurrentHashMap<>();
	
	public TradeKeeper() {}
	
	public void addTradeReport(KerTradeReport rep) throws KerError {
		if (rep.sessionId() == null || rep.sessionId().length() == 0)
			throw new KerError("Trade report has no valid session ID.");
		
		var l = this.trades.get(rep.sessionId());
		if (l == null)
			this.trades.put(rep.sessionId(), new ConcurrentSkipListSet<KerTradeReport>());
		
		this.trades.get(rep.sessionId()).add(rep);
	}
	
	public Collection<KerTradeReport> tradeReports(String sid) throws KerError {
		if (sid == null)
			throw new KerError("Invalid parameter, session ID nulll pointer.");
		
		return this.trades.get(sid);
	}
}

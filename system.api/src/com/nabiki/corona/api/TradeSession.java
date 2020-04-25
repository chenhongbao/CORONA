package com.nabiki.corona.api;

import java.util.Collection;

public interface TradeSession {
	String accountId();
	
	Trade newTrade();
	
	Trade recallTrade(String id);
	
	Account account();
	
	Collection<PositionDetail> positions();
	
	Collection<PositionDetail> positions(String symbol);
	
	Error lastError();
}

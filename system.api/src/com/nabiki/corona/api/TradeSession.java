package com.nabiki.corona.api;

import java.util.Collection;

public interface TradeSession {
	String accountId();

	Trade trade();

	Trade retrieve(String id);

	Account account();

	Collection<PositionDetail> positions();

	Collection<PositionDetail> positions(String symbol);

	Error error();
}

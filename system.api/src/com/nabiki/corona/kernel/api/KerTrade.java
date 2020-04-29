package com.nabiki.corona.kernel.api;

import java.util.Collection;

import com.nabiki.corona.api.Trade;

public interface KerTrade extends Trade {
	KerOrderStatus kerSendAndWait(KerOrder order);

	KerOrderStatus kerSend(KerOrder order);

	KerOrderStatus kerStatus();

	Collection<KerOrderStatus> kerStatuses();

	KerTradeReport kerLastTrade();

	Collection<KerTradeReport> kerTrades();

	KerOrderStatus kerCancel();
}

package com.nabiki.corona.kernel.api;

import com.nabiki.corona.kernel.settings.api.SubscribedSymbols;

public interface DataFactory {
	KerAccount kerAccount(KerAccount... args);
	
	KerCommission kerCommission(KerCommission... args);
	
	KerInstrument kerInstrument(KerInstrument... args);
	
	KerMargin kerMargin(KerMargin... args);
	
	KerOrder kerOrder(KerOrder... args);
	
	KerOrderEvalue kerOrderEvalue(KerOrderEvalue... args);
	
	KerOrderStatus kerOrderStatus(KerOrderStatus... args);
	
	KerPositionDetail kerPositionDetail(KerPositionDetail... args);
	
	KerTrade kerTrade(KerTrade... args);
	
	KerTradeReport kerTradeReport(KerTradeReport... args);
	
	KerTradeSession kerTradeSession(KerTradeSession... args);
	
	KerCandle kerCandle(KerCandle... args);
	
	SubscribedSymbols subscribedSymbols(SubscribedSymbols... args);
}

package com.nabiki.corona.kernel.api;

public interface DataFactory {
	KerAccount kerAccount(Object... args);
	
	KerCommission kerCommission(Object... args);
	
	KerInstrument kerInstrument(Object... args);
	
	KerMargin kerMargin(Object... args);
	
	KerOrder kerOrder(Object... args);
	
	KerOrderEvalue kerOrderEvalue(Object... args);
	
	KerOrderStatus kerOrderStatus(Object... args);
	
	KerPositionDetail kerPositionDetail(Object... args);
	
	KerTrade kerTrade(Object... args);
	
	KerTradeReport kerTradeReport(Object... args);
	
	KerTradeSession kerTradeSession(Object... args);
}

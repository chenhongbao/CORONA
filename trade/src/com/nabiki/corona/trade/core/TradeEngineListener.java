package com.nabiki.corona.trade.core;

import com.nabiki.corona.system.api.*;

public interface TradeEngineListener {
	void orderStatus(KerOrderStatus status);
	
	void tradeReport(KerTradeReport rep);
	
	void account(KerAccount account);
	
	void position(KerPositionDetail pos, boolean last);
	
	void instrument(KerInstrument in, boolean last);
	
	void margin(KerMargin m);
	
	void commission(KerCommission c);
	
	void error(KerError error);
	
	void error(KerOrder order, KerError error);
	
	void error(KerAction action, KerError error);
	
	void remoteLogin(KerRemoteLoginReport rep);
	
	void remoteLogout();
}

package com.nabiki.corona.trade.core;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerRemoteLoginReport;
import com.nabiki.corona.kernel.api.KerTradeReport;

public interface TradeEngineListener {
	void orderStatus(KerOrderStatus status);
	
	void tradeReport(KerTradeReport rep);
	
	void account(KerAccount account);
	
	void position(KerPositionDetail pos);
	
	void error(KerError error);
	
	void error(KerOrder order, KerError error);
	
	void remoteLogin(KerRemoteLoginReport rep);
	
	void remoteLogout();
}

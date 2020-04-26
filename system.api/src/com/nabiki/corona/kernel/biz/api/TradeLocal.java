package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;

public interface TradeLocal {
	String name();
	
	void orderStatus(KerOrderStatus o);
	
	void tradeReport(KerTradeReport r);
	
	void positionDetail(KerPositionDetail p);
	
	void account(KerAccount a);
}

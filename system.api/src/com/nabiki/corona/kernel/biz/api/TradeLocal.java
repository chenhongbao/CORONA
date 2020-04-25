package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;

public interface TradeLocal {
	void onOrderStatus(KerOrderStatus o);
	
	void onTradeReport(KerTradeReport r);
	
	void onPositionDetail(KerPositionDetail p);
	
	void onAccount(KerAccount a);
}

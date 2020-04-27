package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;

public interface TradeLocal {
	String name();

	void orderStatus(KerOrderStatus o);

	void tradeReport(KerTradeReport r);

	void positionDetail(KerPositionDetail p);

	void account(KerAccount a);
	
	void instrument(KerInstrument i);
	
	void margin(KerMargin m);
	
	void commission(KerCommission c);
}

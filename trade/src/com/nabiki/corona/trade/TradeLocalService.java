package com.nabiki.corona.trade;

import java.util.Collection;

import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTrade;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.biz.api.TradeLocal;
import com.nabiki.corona.kernel.data.DefaultDataFactory;
import com.nabiki.corona.kernel.data.KerOrderEvalueImpl;

@Component
public class TradeLocalService implements TradeLocal {
	@Reference(service = LoggerFactory.class)
	private Logger log;
	
	// TODO Service needs to wait runtime info all ready before start to work.
	
	private RuntimeInfo info = new RuntimeInfo();

	@Override
	public String name() {
		// TODO name
		return null;
	}

	@Override
	public void orderStatus(KerOrderStatus o) {
		// TODO orderStatus
		
	}

	@Override
	public void tradeReport(KerTradeReport r) {
		// TODO tradeReport
		
	}

	@Override
	public void positionDetail(KerPositionDetail p) {
		// TODO positionDetail
		
	}

	@Override
	public void account(KerAccount a) {
		// TODO account
		
	}

	@Override
	public void instrument(KerInstrument i) {
		this.info.instrument(i);
	}

	@Override
	public void margin(KerMargin m) {
		this.info.margin(m);
	}

	@Override
	public void commission(KerCommission c) {
		this.info.commission(c);
	}

	@Override
	public KerAccount account(String id) {
		// TODO account
		return null;
	}

	@Override
	public Collection<KerPositionDetail> positionDetails(String id, String symbol) {
		// TODO positionDetails
		return null;
	}

	@Override
	public KerTrade trade(String id) {
		// TODO trade
		return null;
	}

	@Override
	public KerOrderEvalue evaluateOrder(KerOrder op) {
		var factory = DefaultDataFactory.create();
		var eval = factory.kerOrderEvalue();
		
		if (op == null) {
			this.log.warn("KerOrder null pointer.");
			return eval;
		}	
		// Don't insert order if the information for the denoted instrument is not ready.
		if (!this.info.ready(op.symbol)) {
			this.log.warn("Instrument not ready for order: " + op.symbol());
			return eval;
		}
		
		// TODO evaluateOrder
		return null;
	}

}

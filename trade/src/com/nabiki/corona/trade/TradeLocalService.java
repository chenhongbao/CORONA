package com.nabiki.corona.trade;

import java.util.Collection;

import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.api.ErrorCode;
import com.nabiki.corona.api.ErrorMessage;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTrade;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.biz.api.TradeLocal;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

@Component
public class TradeLocalService implements TradeLocal {
	@Reference(service = LoggerFactory.class)
	private Logger log;
	
	// TODO Service needs to wait runtime info all ready before start to work.
	
	@Reference(bind = "bindRuntimeInfo", unbind = "unbindRuntimeInfo", policy = ReferencePolicy.DYNAMIC)
	private volatile RuntimeInfo info;
	
	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;
		
		this.info = info;
		this.log.info("Bind runtime info.");
	}
	
	public void unbindRuntimeInfo(RuntimeInfo info) {
		if (this.info != info)
			return;
		
		this.info = null;
		this.log.info("Unbind runtime info.");
	}
	
	private final DataFactory factory = null;

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
	public KerOrderEvalue allocateOrder(KerOrder op) {
		// TODO allocate res for order.
		return null;
	}
}

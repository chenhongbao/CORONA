package com.nabiki.corona.trade;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.biz.api.TradeLocal;
import com.nabiki.corona.kernel.data.DefaultDataFactory;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;
import com.nabiki.corona.trade.core.InvestorManager;
import com.nabiki.corona.trade.core.OrderStatusKeeper;
import com.nabiki.corona.trade.core.SessionManager;

@Component
public class TradeLocalService implements TradeLocal {
	@Reference(service = LoggerFactory.class)
	private Logger log;
	
	@Reference(bind = "bindRuntimeInfo", unbind = "unbindRuntimeInfo", policy = ReferencePolicy.DYNAMIC)
	private volatile RuntimeInfo info;
	
	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;
		
		this.info = info;
		this.log.info("Bind runtime info.");
		
		// Create investor manager.
		try {
			this.investors = new InvestorManager(this.info, this.factory, this.sm);
			this.log.info("Initialize investors.");
			
			// If there are reports coming in before investors are ready, execute them.
			executeTradeReportCache();
		} catch (KerError e) {
			this.log.warn("Fail initializing investors. {}", e.getMessage(), e);
		}
	}
	
	public void unbindRuntimeInfo(RuntimeInfo info) {
		if (this.info != info)
			return;
		
		this.info = null;
		this.log.info("Unbind runtime info.");
	}
	
	private final DataFactory factory = DefaultDataFactory.create();
	private final OrderStatusKeeper statusKeeper = new OrderStatusKeeper();
	private final SessionManager sm = new SessionManager();
	
	// Manage investors.
	private InvestorManager investors;
	
	// Keep remote counter info.
	private KerAccount account;
	private boolean remotePosLast = true;
	private final List<KerPositionDetail> remotePositions = new LinkedList<>();
	
	// Save trade report and wait for investors are ready.
	private final Set<KerTradeReport> cacheTradeReports = new ConcurrentSkipListSet<>();
	
	// If investors are not ready, save the trade reports for future and return true.
	private boolean trySaveTradeReportWaitReady(KerTradeReport r) {
		if (this.investors != null)
			return false;
		
		// Save trade reports.
		this.cacheTradeReports.add(this.factory.kerTradeReport(r));
		return true;
	}
	
	// Execute trade report.
	private void executeTradeReport(KerTradeReport r) {
		var accountId = this.sm.getAccountId(r.orderId());
		if (accountId == null) {
			this.log.warn("Can't get account ID for order: {}.", r.orderId());
			return;
		}
		
		var investor = this.investors.getInvestor(accountId);
		if (investor == null) {
			this.log.error("Can't get investor account for account ID: {}.", accountId);
			return;
		}
		
		try {
			investor.trade(r);
		} catch (KerError e) {
			this.log.error("Fail updating trade report. {}", e.getMessage(), e);
		}
	}
	
	// If there are trade report cached, execute them.
	private void executeTradeReportCache() {
		if (this.cacheTradeReports.size() == 0)
			return;
		
		var iter = this.cacheTradeReports.iterator();
		while (iter.hasNext()) {
			executeTradeReport(iter.next());
		}
	}

	@Override
	public String name() {
		return "trade_account";
	}
	// TODO need validate given order/trade before executing.

	@Override
	public void orderStatus(KerOrderStatus o) {
		String sid;
		try {
			sid = this.sm.querySessionId(o.orderId());
			this.statusKeeper.setStatus(sid, o);
		} catch (KerError e) {
			this.log.error("Fail updating order status. {}", e.getMessage(), e);
		}		
	}

	@Override
	public void tradeReport(KerTradeReport r) {
		// Save trade report if investors are not ready, and wait. After investors are initialized, it will check the
		// saved reports and execute them.
		if (trySaveTradeReportWaitReady(r))
			return;
		
		executeTradeReport(r);
	}

	@Override
	public void positionDetail(KerPositionDetail p, boolean last) {
		// Mark the begin of receiving position details.
		if (this.remotePosLast) {
			this.remotePositions.clear();
		}
		
		this.remotePositions.add(this.factory.kerPositionDetail(p));
		this.remotePosLast = last;
	}

	@Override
	public void account(KerAccount a) {
		this.account = this.factory.kerAccount(a);
	}

	@Override
	public KerAccount account(String accountId) {
		if (this.investors == null) {
			this.log.warn("Query account {} when investors are not ready.", accountId);
			return null;
		}
		
		var investor = this.investors.getInvestor(accountId);
		if (investor == null) {
			this.log.error("Can't get investor: {}.", accountId);
			return null;
		}
		
		try {
			return investor.account().account().current();
		} catch (KerError e) {
			this.log.error("Fail getting account: {}.", accountId, e);
			return null;
		}
	}

	@Override
	public Collection<KerPositionDetail> positionDetails(String accountId, String symbol) {
		if (this.investors == null) {
			this.log.warn("Query {} position detail under account {} when investors are not ready.", symbol, accountId);
			return null;
		}
		
		var investor = this.investors.getInvestor(accountId);
		if (investor == null) {
			this.log.error("Fail getting investor: {}.", accountId);
			return null;
		}
		
		try {
			return investor.position().getPositon(symbol).current();
		} catch (KerError e) {
			this.log.error("Fail getting current position details. {}", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public KerOrderEvalue allocateOrder(KerOrder op) {
		if (this.investors == null) {
			this.log.warn("Alocate order for account {} when investors are not ready.", op.accountId());
			return null;
		}
		
		var investor = this.investors.getInvestor(op.accountId());
		if (investor == null) {
			this.log.error("Fail getting investor: {}.", op.accountId());
			return null;
		}
		
		try {
			return investor.allocateOrder(op);
		} catch (KerError e) {
			this.log.error("Fail allocating resource for order: {} and account: {}.", op.orderId(), op.accountId());

			// Make an error report.
			var eval = this.factory.kerOrderEvalue();
			eval.error(new KerError(ErrorCode.BAD_FIELD, e.getMessage()));
			return eval;
		}
	}

	@Override
	public KerAccount remoteAccount() {
		return this.account;
	}

	@Override
	public Collection<KerPositionDetail> remotePositionDetails() {
		return this.remotePositions;
	}
}

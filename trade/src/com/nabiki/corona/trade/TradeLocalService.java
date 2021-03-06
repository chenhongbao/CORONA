package com.nabiki.corona.trade;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.service.component.annotations.*;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.OrderStatus;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.object.DefaultDataCodec;
import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.system.biz.api.TradeLocal;
import com.nabiki.corona.trade.core.*;

@Component
public class TradeLocalService implements TradeLocal {
	@Reference(service = LoggerFactory.class)
	private Logger log;

	private TradeServiceContext context = new TradeServiceContext();

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;

		this.context.info(info);
		this.log.info("Bind runtime info: {}.", info.name());
	}

	public void unbindRuntimeInfo(RuntimeInfo info) {
		try {
			if (this.context.info() != info)
				return;
		} catch (KerError e) {
			this.log.error("Fail unbinding runtime info. {}", e.message(), e);
			return;
		}

		this.context.info(null);
		this.log.info("Unbind runtime info.");
	}

	private final DataCodec codec = DefaultDataCodec.create();
	private final DataFactory factory = DefaultDataFactory.create();
	private final IdKeeper idKeeper = new IdKeeper();

	// Manage investors.
	private InvestorManager investors;

	// Remote login rsp.
	private KerRemoteLoginReport login;

	// Keep remote counter info.
	private KerAccount account;
	private boolean remotePosLast = true;
	private final List<KerPositionDetail> remotePositions = new LinkedList<>();

	// Save trade report and wait for investors are ready.
	private final Set<KerTradeReport> cacheTradeReports = new ConcurrentSkipListSet<>();

	public TradeLocalService() {
		// Create investor manager.
		try {
			this.investors = new InvestorManager(this.context, this.idKeeper, this.codec, this.factory);
			this.log.info("Initialize investors.");
		} catch (KerError e) {
			this.log.warn("Fail initializing investors. {}", e.getMessage(), e);
		}
	}

	// If runtime is not ready, save the trade reports for future and return true.
	private boolean trySaveTradeReportWaitReady(KerTradeReport r) throws KerError {
		try {
			// Context's info is ready.
			this.context.info();
			return false;
		} catch (KerError e) {
			this.log.error("Context not ready. {}", e.message(), e);
		}

		// Save trade reports.
		this.cacheTradeReports.add(this.factory.create(KerTradeReport.class, r));
		return true;
	}

	// Execute trade report.
	private void executeTradeReport(KerTradeReport r) {
		var sid = this.idKeeper.getSessionIdWithOrderId(r.orderId());
		if (sid == null) {
			this.log.warn("Trade session ID not found for order: {}.", r.orderId());
			return;
		}

		var investor = investorWithSessionId(sid);
		if (investor == null)
			return;

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

	@Override
	public void orderStatus(KerOrderStatus o) {
		String sid;
		try {
			sid = this.idKeeper.getSessionIdWithOrderId(o.orderId());
			if (sid == null || sid.length() == 0) {
				this.log.error("Fail getting valid session ID for order: {}.", o.orderId());
				return;
			}

			var investor = investorWithSessionId(sid);
			if (investor == null)
				return;

			// Set trade session ID.
			o.sessionId(sid);
			// Update order status into investor account.
			investor.orderStatus(o);

			// Cancel order.
			// Need to remove order ID mappings for a completed order.
			if (o.orderStatus() == OrderStatus.CANCELED) {
				investor.cancel(o);
				this.idKeeper.eraseOrderId(o.orderId());
			} else if (o.orderStatus() == OrderStatus.ALL_TRADED) {
				this.idKeeper.eraseOrderId(o.orderId());
			}
		} catch (KerError e) {
			this.log.error("Fail updating order status. {}", e.getMessage(), e);
		}
	}

	@Override
	public void tradeReport(KerTradeReport r) {
		var sid = this.idKeeper.getSessionIdWithOrderId(r.orderId());
		if (sid == null) {
			this.log.error("No session ID associated with order: {}.", r.orderId());
			return;
		}

		r.sessionId(sid);

		// Save trade report if investors are not ready, and wait. After investors are initialized, it will check the
		// saved reports and execute them.
		try {
			if (trySaveTradeReportWaitReady(r))
				return;
		} catch (KerError e) {
			this.log.error("Fail saving trade report for future execution. {}", e.getMessage(), e);
			return;
		}

		// Execute cached reports first, then the currently given report.
		executeTradeReportCache();
		executeTradeReport(r);
	}

	@Override
	public void positionDetail(KerPositionDetail p, boolean last) {
		// Mark the begin of receiving position details.
		if (this.remotePosLast) {
			this.remotePositions.clear();
		}

		try {
			this.remotePositions.add(this.factory.create(KerPositionDetail.class, p));
			this.remotePosLast = last;

			if (this.remotePosLast && !this.investors.checkPosition(remotePositions))
				this.log.warn("Incorrect position, check data files for details.");
		} catch (KerError e) {
			this.log.error("fail adding remote position. {}", e.getMessage(), e);
		}
	}

	@Override
	public void account(KerAccount a) {
		try {
			this.account = this.factory.create(KerAccount.class, a);
			if (!this.investors.checkAccount(a))
				this.log.warn("Inconsistent account, check data files for details.");
		} catch (KerError e) {
			this.log.error("Factory fail creating account instance. {}", e.getMessage(), e);
		}
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
			return investor.accountManager().accountEngine().current();
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
			this.log.error("Fail allocating resource for session: {} and account: {}.", op.sessionId(), op.accountId());

			// Make an error report.
			KerOrderEvalue eval;
			try {
				eval = this.factory.create(KerOrderEvalue.class);
			} catch (KerError ex) {
				this.log.error("Factory fails creating order evalue instance. {}", ex.message(), ex);
				return null;
			}

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

	@Override
	public List<KerOrderStatus> orderStatus(String sid) {
		var investor = investorWithSessionId(sid);
		if (investor == null)
			return null;

		try {
			return investor.orderStatus(sid);
		} catch (KerError e) {
			this.log.error("Fail retriving order status under session ID: {}. {}", sid, e.message(), e);
			return null;
		}
	}

	@Override
	public List<KerTradeReport> tradeReport(String sid) {
		var investor = investorWithSessionId(sid);
		if (investor == null)
			return null;

		try {
			return investor.trades(sid);
		} catch (KerError e) {
			this.log.error("Fail retriving trades' reports under session ID: {}. {}", sid, e.message(), e);
			return null;
		}
	}

	private InvestorAccount investorWithSessionId(String sid) {
		var accountId = this.idKeeper.getAccountIdWithSessionId(sid);
		if (accountId == null || accountId.length() == 0) {
			this.log.error("Can't find account ID with given session ID: {}.", sid);
			return null;
		}

		var investor = this.investors.getInvestor(accountId);
		if (investor == null) {
			this.log.error("Can't find account with given account ID: {}.", accountId);
			return null;
		}

		return investor;
	}

	@Override
	public void createAccount(String accountId) {
		try {
			this.investors.setInvestor(accountId);
		} catch (KerError e) {
			this.log.error("Fail creating new account: {}. {}", accountId, e.message(), e);
		}
	}

	@Override
	public void moveCash(CashMove cmd) {
		if (cmd == null) {
			this.log.warn("Cash move command null pointer.");
			return;
		}

		var investor = this.investors.getInvestor(cmd.accountId());
		if (investor == null) {
			this.log.warn("Investor account not found: {}.", cmd.accountId());
			return;
		}

		try {
			investor.moveCash(cmd);
		} catch (KerError e) {
			this.log.error("Fail moving cash for account: {}. {}", investor.accountId(), e.message(), e);
		}
	}

	@Override
	public void remoteLogin(KerRemoteLoginReport rep) {
		// Filter the repeated login in the same trading day.
		if (this.login != null && Utils.same(rep.tradingDay(), this.login.tradingDay()))
			return;

		this.login = rep;
		this.login.isLogin(true);
		this.idKeeper.resetId(this.login.maxOrderReference());

		// Initialize accounts.
		try {
			this.investors.init();
			this.log.info("Initialize accounts.");
		} catch (KerError e) {
			this.log.error("Fail initializing accounts. {}", e.message(), e);
		}
	}

	@Override
	public LocalDate tradingDay() {
		if (this.login == null)
			return null;
		else
			return this.login.tradingDay();
	}

	@Override
	public void remoteLogout() {
		// Logout once per trading day.
		// However, the remote may logout over once between different episodes.
		// Check the time to logout at the end of trading day.

		// Take any of the subscribed symbols.
		try {
			var iter = this.context.info().symbols().iterator();
			if (!iter.hasNext()) {
				this.log.warn("No symbols found in runtime info.");
				return;
			}

			var symbol = iter.next();

			// Check now is the end of a trading day.
			if (!this.context.info().endOfDay(Instant.now(), symbol))
				return;
		} catch (KerError e) {
			this.log.error("Fail check end-of-day. {}", e.message(), e);
			return;
		}

		// Settle.
		try {
			this.investors.settle();
			// Clear out-dated data.
			this.idKeeper.clear();
			this.log.info("Settle accounts.");
		} catch (KerError e) {
			this.log.error("Fail settling accounts. {}", e.message(), e);
		}

		// Reset login info.
		this.login.isLogin(false);
	}

	@Override
	public KerRemoteLoginReport remoteInfo() {
		try {
			return this.factory.create(KerRemoteLoginReport.class, this.login);
		} catch (KerError e) {
			this.log.warn("Fail copying login report. {}", e.message(), e);
			return this.login;
		}
	}

	@Override
	public Collection<String> queryAccounts() {
		var r = new HashSet<String>();
		for (var inv : this.investors.getInvestors())
			r.add(inv.accountId());

		return r;
	}

	@Override
	public Collection<String> querySessionsOfAccount(String accountId) {
		return this.idKeeper.getSessionIdsOfAccount(accountId);
	}

	@Override
	public String createOrder(String sessionId) {
		try {
			return this.idKeeper.createOrderId(sessionId);
		} catch (KerError e) {
			log.error("Fail creating order ID for session: {}. {}", sessionId, e.message(), e);
			return null;
		}
	}

	@Override
	public String createSession(String accountId) {
		return this.idKeeper.createSessionId(accountId);
	}
}

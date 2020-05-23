package com.nabiki.corona.trade.core;

import java.nio.file.Path;
import java.util.Collection;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.ErrorMessage;
import com.nabiki.corona.OffsetFlag;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.system.api.CashMoveCommand;

public class InvestorAccount {
	private final AccountManager accountManager;
	private final PositionManager positionManager;
	private final IdKeeper idKeeper;
	private final TradeKeeper tradeKeeper;
	private final SessionWriter sessionWriter;
	private final OrderStatusKeeper statusKeeper;

	private final String accountId;
	private final Path directory;
	private final RuntimeInfo info;
	private final DataFactory factory;

	public InvestorAccount(String accountId, Path dir, RuntimeInfo info, DataFactory factory, IdKeeper keeper) throws KerError {
		this.accountId = accountId;
		this.info = info;
		this.directory= dir;
		this.factory = factory;
		
		// Build directory to keep data files.
		var positionDir = Path.of(this.directory.toAbsolutePath().toString(), "position");
		Utils.ensureDir(positionDir);
		var accountDir = Path.of(this.directory.toAbsolutePath().toString(), "account");
		Utils.ensureDir(accountDir);
		
		// Create instances of data.
		this.idKeeper = keeper;
		this.positionManager = new PositionManager(positionDir, this.info, this.factory);
		this.accountManager = new AccountManager(accountDir, this.info, this.positionManager, this.factory);
		this.tradeKeeper = new TradeKeeper();
		this.sessionWriter = new SessionWriter(Path.of(this.directory.toAbsolutePath().toString(), "sessions"));
		this.statusKeeper = new OrderStatusKeeper();
	}
	
	public void orderStatus(KerOrderStatus status) throws KerError {
		this.orderStatus(status);
		this.sessionWriter.write(status);
	}
	
	public KerOrderStatus orderStatus(String sid) {
		return this.statusKeeper.getStatus(sid);
	}
	
	public String accountId() {
		return this.accountId;
	}
	
	public AccountManager account() {
		return this.accountManager;
	}
	
	public void moveCash(CashMoveCommand cmd) throws KerError {
		this.accountManager.account().moveCash(cmd);
	}
	
	public PositionManager position() {
		return this.positionManager;
	}
	
	public void settle() throws KerError {
		this.positionManager.settle();
		this.accountManager.settle();
	}
	
	public void init() throws KerError {
		this.positionManager.init();
		this.accountManager.init();
	}
	
	public Collection<KerTradeReport> trades(String sid) throws KerError {
		return this.tradeKeeper.tradeReports(sid);
	}

	public void trade(KerTradeReport rep) throws KerError {
		if (rep == null)
			throw new KerError("Can't process trade report of null pointer.");
		
		// Set session ID.
		var sid = this.idKeeper.getSessionIdWithOrderId(rep.orderId());
		rep.sessionId(sid);
		
		var positionEngine = this.positionManager.getPositon(rep.symbol());
		if (positionEngine == null)
			throw new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND);
		
		if (rep.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
			// Add new position, then unlocked the margin.
			// What happens in real is to move the money from account's available to used margin of position.
			positionEngine.trade(rep);
			this.accountManager.account().trade(rep);
		} else {
			// Remove locked position to closed position.
			// What happens in real is to reduce the used margin, then account's available is increased thereby.
			positionEngine.trade(rep);
		}
		
		// Save trades.
		this.tradeKeeper.addTradeReport(rep);
		this.sessionWriter.write(rep);
	}
	
	public void cancel(KerOrderStatus order) throws KerError {
		if (order == null)
			throw new KerError("Can't cancel order of null pointer.");
		
		var sid = this.idKeeper.getSessionIdWithOrderId(order.orderId());
		
		if (order.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
			this.accountManager.account().cancel(sid);
		} else {
			// Other flags are closing order.
			var positionEngine = this.positionManager.getPositon(order.symbol());
			if (positionEngine == null) {
				throw new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND);
			} else {
				positionEngine.cancel(sid);
			}
		}
	}
	
	/**
	 * Check the validity of the order.
	 * 
	 * @param order order
	 * @return evaluation result
	 * @throws KerError
	 */
	public KerOrderEvalue allocateOrder(KerOrder order) throws KerError {
		if (order == null) {
			throw new KerError("Can't allocate for order of null pointer.");
		}
		// Don't insert order if the information for the denoted instrument is not ready.
		if (!this.info.ready(order.symbol)) {
			var r = this.factory.create(KerOrderEvalue.class);
			r.error(new KerError(ErrorCode.NOT_INITED, ErrorMessage.NOT_INITED));
			return r;
		}

		KerOrderEvalue r = null;
		// Create session ID.
		var sid = this.idKeeper.getSessionIdWithOrderId(order.orderId());
		order.sessionId(sid);
		
		// Save order.
		this.sessionWriter.write(order);
		
		if (order.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
			r = validateOpen(order);
		} else {
			r = validateClose(order);
		}
		
		// Set default NONE error, says it is OK.
		if (r.error() == null)
			r.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		
		// Return trade session ID for verified order.
		if (r.error().code() == ErrorCode.NONE)
			r.tradeSessionId(sid);
		
		return r;
	}

	private KerOrderEvalue validateClose(KerOrder order) throws KerError {
		KerOrderEvalue eval = this.factory.create(KerOrderEvalue.class);
		
		var positionEngine = this.positionManager.getPositon(order.symbol());
		if (positionEngine == null) {
			eval.error( new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND));
		} else {
			try {
				eval.positionsToClose(positionEngine.lock(order));
			} catch (KerError e) {
				eval.error(e);
			}
		}
		
		return eval;
	}

	private KerOrderEvalue validateOpen(KerOrder order) throws KerError {
		var eval = this.accountManager.account().lock(order);
		if (eval.error() == null)
			eval.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		return eval;
	}
}

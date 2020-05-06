package com.nabiki.corona.trade.core;

import java.nio.file.Path;

import com.nabiki.corona.Utils;
import com.nabiki.corona.api.ErrorCode;
import com.nabiki.corona.api.ErrorMessage;
import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class InvestorAccount {
	private final AccountEngine account;
	private final AccountManager accountManager;
	private final PositionManager positionManager;
	private final SessionManager sessionManager;

	private final String accountId;
	private final Path directory;
	private final RuntimeInfo info;
	private final DataFactory factory;

	public InvestorAccount(String accountId, Path dir, RuntimeInfo info, DataFactory factory, SessionManager sm) throws KerError {
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
		this.sessionManager = sm;
		this.positionManager = new PositionManager(positionDir, this.info, this.factory);
		this.accountManager = new AccountManager(accountDir, this.info, this.positionManager, this.factory);
		
		// Get account engine from account manager.
		this.account = this.accountManager.account();
	}
	
	public String accountId() {
		return this.accountId;
	}
	
	public AccountManager account() {
		return this.accountManager;
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

	public void trade(KerTradeReport rep) throws KerError {
		if (rep == null)
			throw new KerError("Can't process trade report of null pointer.");
		
		// Set session ID.
		var sid = this.sessionManager.querySessionId(rep.orderId());
		rep.sessionId(sid);
		
		var positionEngine = this.positionManager.getPositon(rep.symbol());
		if (positionEngine == null)
			throw new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND);
		
		if (rep.offsetFlag() == State.OFFSET_OPEN) {
			// Add new position, then unlocked the margin.
			// What happens in real is to move the money from account's available to used margin of position.
			positionEngine.trade(rep);
			this.account.trade(rep);
		} else {
			// Remove locked position to closed position.
			// What happens in real is to reduce the used margin, then account's available is increased thereby.
			positionEngine.trade(rep);
		}
	}
	
	public void cancel(KerOrder order) throws KerError {
		if (order == null)
			throw new KerError("Can't cancel order of null pointer.");
		
		var sid = this.sessionManager.querySessionId(order.orderId());
		
		if (order.offsetFlag() == State.OFFSET_OPEN) {
			this.account.cancel(sid);
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
			var r = this.factory.kerOrderEvalue();
			r.error(new KerError(ErrorCode.NOT_INITED, ErrorMessage.NOT_INITED));
			return r;
		}

		KerOrderEvalue r = null;
		// Create session ID.
		var sid = this.sessionManager.createSessionId(order.orderId(), order.accountId());
		order.sessionId(sid);
		
		if (order.offsetFlag() == State.OFFSET_OPEN) {
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

	private KerOrderEvalue validateClose(KerOrder order) {
		KerOrderEvalue eval = this.factory.kerOrderEvalue();
		
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
		var eval = this.account.lock(order);
		if (eval.error() == null)
			eval.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		return eval;
	}
}

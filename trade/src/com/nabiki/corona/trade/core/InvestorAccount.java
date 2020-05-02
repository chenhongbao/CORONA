package com.nabiki.corona.trade.core;

import com.nabiki.corona.Utils;
import com.nabiki.corona.api.ErrorCode;
import com.nabiki.corona.api.ErrorMessage;
import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.DataFactory;
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
	private final RuntimeInfo info;
	private final DataFactory factory;

	public InvestorAccount(String accountId, RuntimeInfo info, DataFactory factory) {
		this.accountId = accountId;
		this.info = info;
		this.factory = factory;
		
		// Account manager initializes account.
		this.accountManager = new AccountManager(this.info, this.factory);
		this.positionManager = new PositionManager(this.info, this.factory);
		this.sessionManager = new SessionManager();
		// Get account engine from account manager.
		this.account = this.accountManager.account();
	}
	
	public String accountId() {
		return this.accountId;
	}

	public void trade(KerTradeReport rep) throws KerError {
		if (rep == null)
			throw new KerError("Can't process trade report of null pointer.");
		
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
		if (order.offsetFlag() == State.OFFSET_OPEN) {
			r = validateOpen(order);
		} else {
			r = validateClose(order);
		}
		
		// Set default NONE error, says it is OK.
		if (r.error() == null)
			r.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		
		// Create trade session ID for verified order.
		if (r.error().code() == ErrorCode.NONE)
			r.tradeSessionId(this.sessionManager.createSessionId(order.orderId()));
		
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
		double byVol = 0.0, byMny = 0.0;

		// Has checked the availability of runtime info, so query won't return null.
		var margin = this.info.margin(order.symbol());
		if (order.direction() == State.DIRECTION_BUY) {
			byVol = margin.longMarginRatioByVolume();
			byMny = margin.longMarginRatioByMoney();
		} else {
			byVol = margin.shortMarginRatioByVolume();
			byMny = margin.shortMarginRatioByMoney();
		}

		int multi = this.info.instrument(order.symbol()).volumeMultiple();
		double lockAmount = Utils.margin(order.price(), order.volume(), multi, byMny, byVol);
		
		KerOrderEvalue eval = this.factory.kerOrderEvalue();
		if (lockAmount > this.account.current().available()) {
			eval.error(new KerError(ErrorCode.INSUFFICIENT_MONEY, ErrorMessage.INSUFFICIENT_MONEY));
		} else {
			this.account.lock(lockAmount);
		}
		
		return eval;
	}
}

package com.nabiki.corona.trade.core;

import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.data.DefaultDataFactory;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;
import com.nabiki.corona.api.ErrorCode;
import com.nabiki.corona.api.ErrorMessage;
import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrder;

public class AccountEngine {
	private final PositionManager positions;
	private final SessionIdManager sessionIds;

	private final String accountId;
	private final RuntimeInfo info;
	private final DataFactory factory;
	
	private KerAccount account; // TODO init account

	public AccountEngine(String accountId, RuntimeInfo info, DataFactory factory) {
		this.accountId = accountId;
		this.info = info;
		this.factory = factory;
		this.positions = new PositionManager(this.info);
		this.sessionIds = new SessionIdManager();

		// TODO account engine: compute account upon newly arriving trade
	}

	public String accountId() {
		return this.accountId;
	}

	public void trade(KerTradeReport rep) throws KerError {
		// TODO Set the trade session ID and update position.
		// TODO trade
	}
	
	public void setOrder(KerOrder order) throws KerError {
		// TODO set order into the account
	}

	/**
	 * Check the validity of the order.
	 * 
	 * @param order order
	 * @return evaluation result
	 * @throws KerError
	 */
	public KerOrderEvalue validateOrder(KerOrder order) throws KerError {
		if (order == null) {
			throw new KerError("KerOrder null pointer.");
		}
		// Don't insert order if the information for the denoted instrument is not ready.
		if (!this.info.ready(order.symbol)) {
			throw new KerError("Instrument not ready for order: " + order.symbol());
		}

		if (order.offsetFlag() == State.OFFSET_OPEN) {
			return validateOpen(order);
		} else {
			return validateClose(order);
		}
	}

	private KerOrderEvalue validateClose(KerOrder order) {
		// TODO evaluate open order
		return null;
	}

	private KerOrderEvalue validateOpen(KerOrder order) {
		double byVol = 0.0, byMny = 0.0;
		double lockedAmount = 0.0;

		// Has checked the availability of runtime info, so query won't return null.
		var margin = this.info.margin(order.symbol());
		if (order.direction() == State.DIRECTION_BUY) {
			byVol = margin.longMarginRatioByVolume();
			byMny = margin.longMarginRatioByMoney();
		} else {
			byVol = margin.shortMarginRatioByVolume();
			byMny = margin.shortMarginRatioByMoney();
		}

		if (byVol > 0) {
			lockedAmount = order.volume() * byVol;
		} else {
			int multi = this.info.instrument(order.symbol()).volumeMultiple();
			lockedAmount = multi * order.volume() * order.price()* byMny;
		}
		
		KerOrderEvalue eval = this.factory.kerOrderEvalue();
		if (lockedAmount > available()) {
			eval.error(new KerError(ErrorCode.INSUFFICIENT_MONEY, ErrorMessage.INSUFFICIENT_MONEY));
		} else {
			// Check passed and assign trade session ID.
			eval.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		}

		return eval;
	}
	
	private double available() {
		// TODO get available money
		return 0.0;
	}

	/**
	 * Get trade session ID with an order ID returned from remote. Trade session ID is generated and used within this
	 * system only.
	 * 
	 * @param orderId order ID(order reference)
	 * @return trade session ID
	 */
	private String sessionId(String orderId) {
		return this.sessionIds.sid(orderId);
	}
}

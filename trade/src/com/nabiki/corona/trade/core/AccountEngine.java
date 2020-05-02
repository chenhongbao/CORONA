package com.nabiki.corona.trade.core;

import java.util.LinkedList;
import java.util.List;

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

public class AccountEngine {
	private class RuntimeLockMoney {
		private double amount;
		private String sessionId;
		
		RuntimeLockMoney(double amount, String sessionId) {
			this.amount = amount;
			this.sessionId = sessionId;
		}
		
		double amount() {
			return this.amount;
		}
		
		void amount(double d) {
			this.amount = d;
		}
		
		String sessionId() {
			return this.sessionId;
		}
		
		void sessionId(String s) {
			this.sessionId = s;
		}
	}
	
	private final KerAccount origin;
	private final RuntimeInfo info;
	private final PositionManager position;
	private final DataFactory factory;
	
	private final List<RuntimeLockMoney> locked = new LinkedList<>();

	public AccountEngine(KerAccount init, RuntimeInfo info, PositionManager pos, DataFactory factory) {	
		this.info = info;
		this.position = pos;
		this.factory = factory;
		
		if (init != null)
			this.origin = init;
		else
			this.origin = this.factory.kerAccount();

		// TODO account engine: compute account upon newly arriving trade
	}


	public KerOrderEvalue lock(KerOrder order) throws KerError {
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
		double lockAmount = Utils.marginOrCommission(order.price(), order.volume(), multi, byMny, byVol);
		
		KerOrderEvalue eval = this.factory.kerOrderEvalue();
		if (lockAmount > available()) {
			eval.error(new KerError(ErrorCode.INSUFFICIENT_MONEY, ErrorMessage.INSUFFICIENT_MONEY));
		} else {
			lockCash(lockAmount, order.sessionId());
		}
		
		return eval;
	}
	
	public void cancel(String sessionId) {
		// TODO cancel open order
	}

	public void trade(KerTradeReport rep) {
		// TODO complete both open and close order
	}
	
	public KerAccount current() {
		// TODO get current account
		return null;
	}
	
	private void lockCash(double amount, String sid) throws KerError {
		if (amount > available())
			throw new KerError("Not enough available money to lock.");
		
		this.locked.add(new RuntimeLockMoney(amount, sid));
	}
	
	// Following methods compute fields of account.
	private double deposit() {
		// TODO deposit
		return 0.0D;
	}
	
	private double withdraw() {
		// TODO withdraw
		return 0.0D;
	}
	
	private double frozenMargin() {
		// TODO frozen margin
		return 0.0D;
	}
	
	private double frozenCash() {
		// TODO frozen cash
		return 0.0D;
	}
	
	private double frozenCommission() {
		// TODO frozen commission
		return 0.0D;
	}
	
	private double currentMargin() {
		// TODO current margin
		return 0.0D;
	}
	
	private double commission() {
		// TODO commission
		return 0.0D;
	}
	
	private double closeProfit() {
		// TODO close profit
		return 0.0D;
	}
	
	private double positionProfit() {
		// TODO position profit
		return 0.0D;
	}
	
	private double balance() {
		// TODO balance
		return 0.0D;
	}
	
	private double available() {
		// TODO calculate available
		return 0.0;
	}
	
	private double withdrawQuota() {
		// TODO withdraw quota
		return 0.0D;
	}
}

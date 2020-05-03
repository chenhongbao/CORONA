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
		private double commission;
		private int volume;
		private String sessionId;

		RuntimeLockMoney(double amount, double commission, int volume, String sessionId) {
			this.amount = amount;
			this.commission = commission;
			this.volume = volume;
			this.sessionId = sessionId;
		}

		double amount() {
			return this.amount;
		}

		int volume() {
			return this.volume;
		}

		void amount(double d) {
			this.amount = d;
		}

		void volume(int v) {
			this.volume = v;
		}

		String sessionId() {
			return this.sessionId;
		}

		void sessionId(String s) {
			this.sessionId = s;
		}

		double commission() {
			return this.commission;
		}

		void commission(double d) {
			this.commission = d;
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
	}

	public KerOrderEvalue lock(KerOrder order) throws KerError {
		double byVol = 0.0, byMny = 0.0;

		// Has checked the availability of runtime info, so query won't return null.
		// Margin.
		var margin = this.info.margin(order.symbol());
		if (order.direction() == State.DIRECTION_BUY) {
			byVol = margin.longMarginRatioByVolume();
			byMny = margin.longMarginRatioByMoney();
		} else {
			byVol = margin.shortMarginRatioByVolume();
			byMny = margin.shortMarginRatioByMoney();
		}

		int multi = this.info.instrument(order.symbol()).volumeMultiple();
		double lockMargin = Utils.marginOrCommission(order.price(), order.volume(), multi, byMny, byVol);

		// Commission.
		var commRate = this.info.commission(order.symbol());
		byVol = commRate.openRatioByVolume();
		byMny = commRate.openRatioByMoney();

		double lockCommission = Utils.marginOrCommission(order.price, order.volume(), multi, byMny, byVol);

		KerOrderEvalue eval = this.factory.kerOrderEvalue();
		if (lockMargin + lockCommission > current().available()) {
			eval.error(new KerError(ErrorCode.INSUFFICIENT_MONEY, ErrorMessage.INSUFFICIENT_MONEY));
		} else {
			lockCash(lockMargin, lockCommission, order.volume(), order.sessionId());
		}

		return eval;
	}

	public void cancel(String sessionId) {
		var iter = this.locked.listIterator();
		while (iter.hasNext()) {
			var s = iter.next();
			if (s.sessionId().compareTo(sessionId) == 0)
				iter.remove();

		}
	}

	public void trade(KerTradeReport rep) throws KerError {
		if (rep.offsetFlag() != State.OFFSET_OPEN)
			throw new KerError("Can't unlock cash for a close order.");

		// There is noly one cash lock per trade session.
		var iter = this.locked.listIterator();
		while (iter.hasNext()) {
			var s = iter.next();
			if (s.sessionId().compareTo(rep.sessionId()) != 0)
				continue;

			if (s.volume() < rep.volume())
				throw new KerError("Lock cash less than trade.");
			
			if (s.volume() == rep.volume()) {
				iter.remove();
				break;
			}

			// Unlock part of the cash and wait for next trade.
			// Cash in this trade is all unlocked.
			int remain = s.volume() - rep.volume();
			
			// Decrease amount/commission.
			s.amount(s.amount() * remain / s.volume());
			s.commission(s.commission() * remain / s.volume());
			s.volume(remain);
			break;
		}
	}

	public KerAccount current() {
		for (var pe : this.position.positions()) {
			for (var p : pe.locked()) {

			}

			for (var p : pe.closed()) {

			}
		}

		// TODO get current account
		return null;
	}

	public void settle() {
		// TODO settle account
	}

	// Assume available >= amount.
	private void lockCash(double amount, double commission, int volume, String sid) throws KerError {
		this.locked.add(new RuntimeLockMoney(amount, commission, volume, sid));
	}
}

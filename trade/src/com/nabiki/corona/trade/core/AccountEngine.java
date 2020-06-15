package com.nabiki.corona.trade.core;

import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.DirectionFlag;
import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.ErrorMessage;
import com.nabiki.corona.OffsetFlag;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;

public class AccountEngine {
	private class RuntimeLockMoney {
		private double margin;
		private double commission;
		private int volume;
		private String sessionId;

		RuntimeLockMoney(double amount, double commission, int volume, String sessionId) {
			this.margin = amount;
			this.commission = commission;
			this.volume = volume;
			this.sessionId = sessionId;
		}

		double margin() {
			return this.margin;
		}

		int volume() {
			return this.volume;
		}

		void amount(double d) {
			this.margin = d;
		}

		void volume(int v) {
			this.volume = v;
		}

		String sessionId() {
			return this.sessionId;
		}

		double commission() {
			return this.commission;
		}

		void commission(double d) {
			this.commission = d;
		}
	}

	private final KerAccount origin;
	private final TradeServiceContext context;
	private final PositionManager position;
	private final DataFactory factory;
	
	// Locking money.
	private final List<RuntimeLockMoney> locked = new LinkedList<>();

	// Deposit/withdraw(management).
	private final List<CashMove> deposits = new LinkedList<>();
	private final List<CashMove> withdraws = new LinkedList<>();

	public AccountEngine(KerAccount init, TradeServiceContext context, PositionManager pos, DataFactory factory) throws KerError {
		this.context = context;
		this.position = pos;
		this.factory = factory;

		if (init != null)
			this.origin = init;
		else
			this.origin = this.factory.create(KerAccount.class);
	}

	/**
	 * Get a new instance copy of original account.
	 * 
	 * @return new instance of account
	 * @throws KerError factory fails creating instance and throws exception
	 */
	public KerAccount origin() throws KerError {
		return this.factory.create(KerAccount.class, this.origin);
	}
	
	public void moveCash(CashMove cmd) throws KerError {
		if (cmd == null)
			throw new KerError("Cash move command null pointer.");
		if (cmd.type() == null)
			throw new KerError("Cash move type null pointer.");
		
		if (cmd.type() == CashMoveType.DEPOSIT)
			deposit(cmd);
		else
			withdraw(cmd);
	}

	private void deposit(CashMove cmd) throws KerError {
		if (cmd == null)
			throw new KerError("Cash command null pointer.");
		if (cmd.amount() <= 0)
			throw new KerError("Cash command amount zero or negative.");
		
		this.deposits.add(cmd);
	}

	private void withdraw(CashMove cmd) throws KerError {
		if (cmd == null)
			throw new KerError("Cash command null pointer.");
		if (cmd.amount() <= 0)
			throw new KerError("Cash command amount zero or negative.");
		
		if (cmd.amount() > current().withdrawQuota())
			throw new KerError("Withdraw quota not enough.");
		
		this.withdraws.add(cmd);
	}

	public KerOrderEvalue lock(KerOrder order) throws KerError {
		double byVol = 0.0, byMny = 0.0;

		// Has checked the availability of runtime info, so query won't return null.
		// Margin.
		var margin = this.context.info().margin(order.symbol());
		if (order.direction() == DirectionFlag.DIRECTION_BUY) {
			byVol = margin.longMarginRatioByVolume();
			byMny = margin.longMarginRatioByMoney();
		} else {
			byVol = margin.shortMarginRatioByVolume();
			byMny = margin.shortMarginRatioByMoney();
		}

		int multi = this.context.info().instrument(order.symbol()).volumeMultiple();
		double lockMargin = Utils.marginOrCommission(order.price(), order.volume(), multi, byMny, byVol);

		// Commission.
		var commRate = this.context.info().commission(order.symbol());
		byVol = commRate.openRatioByVolume();
		byMny = commRate.openRatioByMoney();

		double lockCommission = Utils.marginOrCommission(order.price(), order.volume(), multi, byMny, byVol);

		KerOrderEvalue eval = this.factory.create(KerOrderEvalue.class);
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
		if (rep.offsetFlag() != OffsetFlag.OFFSET_OPEN)
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
			s.amount(s.margin() * remain / s.volume());
			s.commission(s.commission() * remain / s.volume());
			s.volume(remain);
			break;
		}
	}

	// Algorithm: mark-to-market.
	public KerAccount current() throws KerError {
		double frozenMargin = 0.0, frozenCash = 0.0, frozenCommission = 0.0, currentMargin = 0.0, commission = 0.0,
				closeProfit = 0.0, positionProfit = 0.0, balance = 0.0, available = 0.0;

		// Frozen margin is the margin of locked position.
		// Frozen commission is the commission of locked position.
		// Current margin is total margin in balance, including locked and available.
		// Commission is the total paid commission.
		// Close profit is by-date profit of closed position.
		// Position profit is by-date profit of at-hand position.
		for (var pe : this.position.positions()) {
			for (var p : pe.locked()) {
				frozenMargin += p.margin();
				currentMargin += p.margin();
				positionProfit += p.positionProfitByDate();
				frozenCommission += p.closeCommission();
				commission += p.openCommission();
			}

			for (var p : pe.closed()) {
				closeProfit = p.closeProfitByDate();
				commission += p.openCommission() + p.closeCommission();
			}

			for (var p : pe.available()) {
				positionProfit += p.positionProfitByDate();
				commission += p.openCommission();
				currentMargin += p.margin();
			}
		}

		// Frozen cash is total locked money, including margin and commission.
		for (var c : this.locked)
			frozenCash += c.margin() + c.commission();

		// Cash move.
		var deposit = deposit();
		var withdraw = withdraw();

		// Refernce: https://www.wenhua.com.cn/popwin/zhuridingshi.htm
		balance = origin.preBalance() + deposit - withdraw + closeProfit + positionProfit - commission;
		available = balance - frozenMargin - frozenCommission - frozenCash - currentMargin;

		// Exclude the unsettled money.
		var withdrawQuota = available - (positionProfit > 0 ? positionProfit : 0) - (closeProfit > 0 ? closeProfit : 0);

		var a = origin();

		a.deposit(deposit);
		a.withdraw(withdraw);
		a.frozenMargin(frozenMargin);
		a.frozenCash(frozenCash);
		a.frozenCommission(frozenCommission);
		a.currentMargin(currentMargin);
		a.commission(commission);
		a.closeProfit(closeProfit);
		a.positionProfit(positionProfit);
		a.balance(balance);
		a.available(available);
		a.withdraw(withdrawQuota);

		return a;
	}

	/**
	 * The method will set pre-like fields with non-pre fields. Make sure the non-pre fields have valid values and only
	 * call once per trading day.
	 */
	public void init() throws KerError {
		if (Utils.same(this.context.info().tradingDay(), this.origin.tradingDay()))
			throw new KerError("Can't init an account for more than once per trading day.");
		
		// Trading day.
		this.origin.tradingDay(this.context.info().tradingDay());
		
		// Set fields to preXxx.
		this.origin.preBalance(this.origin.balance());
		this.origin.preDeposit(this.origin.deposit());
		this.origin.preMargin(this.origin.currentMargin());
		
		// Reset fields to zeros for new day trading.
		this.origin.balance(0.0);
		this.origin.deposit(0.0);
		this.origin.currentMargin(0.0);
	}

	// Assume available >= amount.
	private void lockCash(double amount, double commission, int volume, String sid) throws KerError {
		this.locked.add(new RuntimeLockMoney(amount, commission, volume, sid));
	}

	private double deposit() {
		double r = 0.0;
		for (var d : this.deposits)
			r += d.amount();

		return r;
	}

	private double withdraw() {
		double r = 0.0;
		for (var w : this.withdraws)
			r += w.amount();

		return r;
	}
}

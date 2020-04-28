package com.nabiki.corona.trade.core;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.api.Order;
import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;

public class PositionEngine {

	private final List<RuntimePositionDetail> details = new LinkedList<>();

	public PositionEngine(Collection<RuntimePositionDetail> init) {
		if (init != null)
			this.details.addAll(init);

		// TODO manage positions
	}

	public synchronized void trade(KerTradeReport rep) throws KerError {
		switch (rep.offsetFlag()) {
		case State.OFFSET_OPEN:
			openPosition(rep);
			break;
		case State.OFFSET_CLOSE:
		case State.OFFSET_CLOSE_TODAY:
		case State.OFFSET_CLOSE_YESTERDAY:
			closePosition(rep);
			break;
		default:
			throw new KerError("Unhandled unknown trade offset: " + String.valueOf(rep.offsetFlag()));
		}
	}

	private void closePosition(KerTradeReport rep) throws KerError {
		KerTradeReport noClose = rep;
		var iter = this.details.iterator();
		while (iter.hasNext() && noClose.volume() > 0) {
			noClose = iter.next().close(noClose);
		}

		// If volume of the trade can't be removed all from position, there must be wrong. Probably there are not enough
		// positions to close.
		if (noClose.volume() > 0) {
			throw new KerError("Fail removing all volume from position(" + noClose.volume() + " left).");
		}
	}

	private void openPosition(KerTradeReport rep) throws KerError {
		this.details.add(new RuntimePositionDetail(rep));
	}

	private class RuntimePositionDetail {
		private KerPositionDetail origin;
		private List<KerPositionDetail> locked = new LinkedList<>();
		private List<KerPositionDetail> closed = new LinkedList<>();

		RuntimePositionDetail(KerTradeReport rep) throws KerError {
			this.origin = ensure(rep);
			if (this.origin == null) {
				throw new KerError("Trade report needs to be an open trade.");
			}
		}

		RuntimePositionDetail(KerPositionDetail origin, Collection<KerPositionDetail> locked,
				Collection<KerPositionDetail> closed) {
			this.origin = origin;
			if (locked != null)
				this.locked.addAll(locked);
			if (closed != null)
				this.closed.addAll(closed);
		}

		// If rep is null, return an empty position.
		KerPositionDetail ensure(KerTradeReport rep) {
			// TODO check the state of trade and create a new position detail, or null if error.
			return null;
		}

		// Get the locked position.
		KerPositionDetail sumLocked() {
			var a = deepCopy(origin());
			
			int volume = 0;
			for (var p : locked()) {
				volume += p.volume();
			}
			
			a.volume(volume);
			a.margin(origin().margin() * a.volume() / origin().volume());
			a.exchangeMargin(origin().exchangeMargin() * a.volume() / origin().volume());
			
			return a;
		}

		// Get the closed position.
		KerPositionDetail sumClosed() {
			var a = deepCopy(origin());
			
			// Sum up.
			int volume = 0, closeVolume = 0;
			double closeAmount = 0.0, closeProfitByDate = 0.0F, closeProfitByTrade = 0.0F;
			for (var c : closed()) {
				volume += c.volume();
				closeVolume += c.closeVolume();
				closeAmount += c.closeAmount();
				closeProfitByDate += c.closeProfitByDate();
				closeProfitByTrade += c.closeProfitByTrade();
			}
			
			//Reset some fields.
			a.volume(volume);
			a.closeVolume(closeVolume);
			a.closeAmount(closeAmount);
			a.closeProfitByDate(closeProfitByDate);
			a.closeProfitByTrade(closeProfitByTrade);
			a.margin(0.0F);
			a.exchangeMargin(0.0);
			
			return a;
		}

		KerPositionDetail origin() {
			return this.origin;
		}

		// Get available position that are neither closed nor locked.
		KerPositionDetail available() {
			var l = sumLocked();
			var c = sumClosed();
			var a = deepCopy(origin());

			// Available position = total origin - closed - locked.
			a.volume(origin().volume() - l.volume() - c.volume());

			// Available position are position at hand and not locked. So they are not closed, neither have close quota.
			a.closeProfitByDate(0.0);
			a.closeProfitByTrade(0.0);
			a.positionProfitByDate(0.0);
			a.positionProfitByTrade(0.0);
			a.closeVolume(0);
			a.closeAmount(0.0);
			
			// Get the part of margin used by available position.
			a.margin(origin().margin() * a.volume() / origin().volume());
			a.exchangeMargin(origin().exchangeMargin() * a.volume() / origin().volume());
			
			return a;
		}

		LocalDate deepCopy(LocalDate old) {
			return LocalDate.of(origin.openDate().getYear(), origin.openDate().getMonthValue(),
					origin().openDate().getDayOfMonth());
		}
		
		KerPositionDetail deepCopy(KerPositionDetail old) {
			var a = ensure(null);
			
			a.symbol(old.symbol());
			a.brokerId(old.brokerId());
			a.investorId(old.investorId());
			a.hedgeFlag(old.hedgeFlag());
			a.direction(old.direction());
			a.openDate(deepCopy(old.openDate()));
			a.tradeId(old.tradeId());
			a.volume(old.volume());
			a.openPrice(old.openPrice());
			a.tradingDay(deepCopy(old.tradingDay()));
			a.settlementId(old.settlementId());
			a.tradeType(old.tradeType());
			a.combSymbol(old.combSymbol());
			a.exchangeId(old.exchangeId());
			a.closeProfitByDate(old.closeProfitByDate());
			a.closeProfitByTrade(old.closeProfitByTrade());
			a.positionProfitByDate(old.positionProfitByDate());
			a.positionProfitByTrade(old.positionProfitByTrade());
			a.margin(old.margin());
			a.exchangeMargin(old.exchangeMargin());
			a.marginRateByMoney(old.marginRateByMoney());
			a.marginRateByVolume(old.marginRateByVolume());
			a.lastSettlementPrice(old.lastSettlementPrice());
			a.settlementPrice(old.settlementPrice());
			a.closeVolume(old.closeVolume());
			a.closeAmount(old.closeAmount());
			a.timeFirstVolume(old.timeFirstVolume());
			a.investUnitId(old.investUnitId());
			
			return a;
		}

		// Get own position that are not closed yet.
		KerPositionDetail own() {
			var c = sumClosed();
			var a = deepCopy(origin());

			// Own position = total origin - closed.
			a.volume(origin().volume() - c.volume());

			// Own position are position at hand, that are not closed yet.
			a.closeProfitByDate(0.0);
			a.closeProfitByTrade(0.0);
			a.positionProfitByDate(0.0);
			a.positionProfitByTrade(0.0);
			a.closeVolume(0);
			a.closeAmount(0.0);
			
			// Get the part of margin used by own position.
			a.margin(origin().margin() * a.volume() / origin().volume());
			a.exchangeMargin(origin().exchangeMargin() * a.volume() / origin().volume());
			
			return a;
		}
		
		KerPositionDetail current() {
			var c = sumClosed();
			var a = deepCopy(origin());
			
			// Set close info.
			a.closeProfitByDate(c.closeProfitByDate());
			a.closeProfitByTrade(c.closeProfitByTrade());
			a.closeAmount(c.closeAmount());
			a.closeVolume(c.closeVolume());
			
			return a;
		}

		Collection<KerPositionDetail> locked() {
			return this.locked;
		}

		Collection<KerPositionDetail> closed() {
			return this.closed;
		}
		
		Order lock(Order o) {
			// TODO lock the resource for the given order. If there are still resource to be locked, return the
			// remaining.
			return null;
		}
		
		// Cancel the trade with session ID.
		void cancel(String sessionId) {
			// TODO cancel the session.
		}

		KerTradeReport close(KerTradeReport origin) {
			// TODO If given trade report belongs to a trade session that closes positions in this position detail,
			// remove that positions as many as possible. Then return remaining part of trade to be remove from other
			// position details if there are. Must check the trade session ID to find the right locked position.
			return null;
		}
	}

}

package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.Utils;
import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;

public class PositionEngine {
	private boolean isSettled = false;
	
	private final RuntimeInfo runtime;
	private final String symbol;
	private final DataFactory fatory;
	private final List<RuntimePositionDetail> details = new LinkedList<>();

	public PositionEngine(String symbol, RuntimeInfo runtime, Collection<RuntimePositionDetail> init,
			DataFactory factory) throws KerError {
		this.symbol = symbol;
		this.runtime = runtime;
		this.fatory = factory;
		if (init != null) {
			for (var d : init) {
				if (d.origin().symbol().compareTo(this.symbol) != 0)
					throw new KerError("Wrong symbol. Want " + this.symbol + " but found " + d.origin().symbol());
				this.details.add(d);
			}
		}
	}
	
	public boolean isSettled() {
		return this.isSettled;
	}

	public void cancel(String sessionId) throws KerError {
		if (sessionId == null)
			throw new KerError("Cancel session ID can't be a null pointer.");

		var iter = this.details.iterator();
		while (iter.hasNext())
			iter.next().cancel(sessionId);
	}

	/**
	 * Trade report must have a trade session ID.
	 * 
	 * @param rep trade report
	 * @throws KerError Throw exception upon failure of initialization position detail for open order, or closing all
	 *                  locked position for close order, or unknown offset flag.
	 */
	public void trade(KerTradeReport rep) throws KerError {
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

	/**
	 * Mark-to-market settle positions and set the settlement price. Then return the settled position details.
	 * 
	 * @param settlementPrice settlement price
	 * @return settled position details
	 * @throws KerError throw exception if instrument information not found.
	 */
	public List<KerPositionDetail> settle(double settlementPrice) throws KerError {
		var inst = this.runtime.instrument(symbol());
		if (inst == null)
			throw new KerError("Instrument info not found: " + symbol());

		List<KerPositionDetail> ret = new LinkedList<>();

		// We need to settle the at-hand positions.
		// Update position profit and margin.
		for (var p : this.details) {
			var n = this.fatory.kerPositionDetail(p.own());
			if (n.volume() == 0)
				continue;
			
			n.settlementPrice(settlementPrice);
			
			// Calculate previous price according to position type.
			double previousPrice = 0.0;
			if (n.tradingDay().compareTo(this.runtime.tradingDay()) == 0)
				// today's position
				previousPrice = n.openPrice();
			else
				// yesterday's position
				previousPrice = n.lastSettlementPrice();

			// Calculate position profits.
			var positionProfit = Utils.profit(previousPrice, n.settlementPrice(), n.volume(), inst.volumeMultiple(),
					n.direction());
			n.positionProfitByDate(positionProfit);
			
			// Update margin.
			double m = Utils.marginOrCommission(n.settlementPrice(), n.volume(), inst.volumeMultiple(),
					n.marginRateByMoney(),n.marginRateByVolume());
			n.margin(m);
			
			// Return the settled position details.
			ret.add(n);
		}

		this.isSettled = true;
		return ret;
	}

	public String symbol() {
		return this.symbol;
	}

	public Collection<KerPositionDetail> lock(KerOrder o) throws KerError {
		if (!canLock(o))
			throw new KerError("Can't lock position for order: " + o.orderId());

		Collection<KerPositionDetail> ret = new LinkedList<>();
		KerOrder toLock = this.fatory.kerOrder(o);
		var iter = this.details.iterator();

		while (iter.hasNext() && toLock.volume() > 0) {
			var lck = iter.next().lock(toLock);
			// Update to-lock volume and ret-collection.
			if (lck.volume() > 0) {
				ret.add(lck);
				toLock.volume(toLock.volume() - lck.volume());
			}
		}

		if (toLock.volume() > 0)
			throw new KerError(
					"[FATAL]Internal state changed, but not enough position to lock for order: " + o.orderId());

		return ret;
	}

	/**
	 * Get all locked position of this position engine.
	 * 
	 * @return locked positions
	 */
	public Collection<KerPositionDetail> locked() {
		var ret = new LinkedList<KerPositionDetail>();
		for (var rt : this.details) {
			ret.addAll(rt.locked());
		}
		return ret;
	}

	/**
	 * Get all closed position of this position engine.
	 * 
	 * @return closed positions
	 */
	public Collection<KerPositionDetail> closed() {
		var ret = new LinkedList<KerPositionDetail>();
		for (var rt : this.details) {
			ret.addAll(rt.closed());
		}
		return ret;
	}

	/**
	 * Get own position of this position engine.
	 * 
	 * @return own position
	 */
	public Collection<KerPositionDetail> own() throws KerError {
		var ret = new LinkedList<KerPositionDetail>();
		for (var rt : this.details) {
			ret.add(rt.own());
		}
		return ret;
	}

	/**
	 * Get available position of this position engine.
	 * 
	 * @return available position
	 */
	public Collection<KerPositionDetail> available() throws KerError {
		var ret = new LinkedList<KerPositionDetail>();
		for (var rt : this.details) {
			ret.add(rt.available());
		}
		return ret;
	}

	private boolean canLock(KerOrder o) throws KerError {
		if (o == null || o.volume() == 0)
			return false;

		int volumeToLock = o.volume();
		var iter = this.details.iterator();

		while (iter.hasNext() && volumeToLock > 0) {
			volumeToLock -= iter.next().available().volume();
		}

		if (volumeToLock > 0)
			return false;
		else
			return true;
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
		this.details.add(new RuntimePositionDetail(rep, this.runtime, this.fatory));
	}
}

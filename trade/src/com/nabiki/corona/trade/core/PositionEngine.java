package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.system.api.*;
import com.nabiki.corona.OffsetFlag;

public class PositionEngine {
	private boolean isSettled;

	private final TradeServiceContext context;
	private final String symbol;
	private final DataFactory fatory;
	private final List<RuntimePositionDetail> details = new LinkedList<>();

	/**
	 * If init is null, create an empty position engine and settlement market is set to false, otherwise the position
	 * engine is settled, and the mark is set to true.
	 * 
	 * @param symbol
	 * @param runtime
	 * @param init
	 * @param factory
	 * @throws KerError
	 */
	public PositionEngine(String symbol, TradeServiceContext context, Collection<RuntimePositionDetail> init,
			DataFactory factory) throws KerError {
		this.symbol = symbol;
		this.context = context;
		this.fatory = factory;
		if (init != null) {
			for (var d : init) {
				var o = d.origin();
				if (o.symbol().compareTo(this.symbol) != 0)
					throw new KerError("Wrong symbol. Want " + this.symbol + " but found " + o.symbol());
				
				this.details.add(d);
			}
			
			this.isSettled = true;
		} else
			this.isSettled = false;
	}

	public boolean isSettled() {
		return this.isSettled;
	}
	
	public void init() throws KerError {
		for (var p : this.details) {
			if (!p.isSettled())
				throw new KerError("Can't initializa unsettled runtime position.");
			
			p.init();
		}
		
		this.isSettled = false;
	}

	/**
	 * Read position detail from given position file and reset internal data to newly read-in data.
	 * 
	 * @param f position file to read from
	 * @throws KerError throw exception when symbols of current position engine and given position file not matched,
	 *         or can't read from the file
	 */
	public void read(PositionFile f) throws KerError {
		if (f.symbol().compareTo(symbol()) != 0)
			throw new KerError(
					"Symbols of position engine(" + symbol() + ") and file(" + f.symbol() + ") not matched.");

		var ps = f.read();
		// Make sure read successfully, then clear the old data and set new.
		this.details.clear();
		this.details.addAll(ps);
	}

	public void write(PositionFile f) throws KerError {
		if (f.symbol().compareTo(symbol()) != 0)
			throw new KerError(
					"Symbols of position engine(" + symbol() + ") and file(" + f.symbol() + ") not matched.");

		f.write(this.details);
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
		case OffsetFlag.OFFSET_OPEN:
			openPosition(rep);
			break;
		case OffsetFlag.OFFSET_CLOSE:
		case OffsetFlag.OFFSET_CLOSE_TODAY:
		case OffsetFlag.OFFSET_CLOSE_YESTERDAY:
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
	public void settle(double settlementPrice) throws KerError {
		for (var r : this.details)
			r.settle(settlementPrice);

		this.isSettled = true;
	}

	public String symbol() {
		return this.symbol;
	}

	public Collection<KerPositionDetail> lock(KerOrder o) throws KerError {
		if (!canLock(o))
			throw new KerError("Can't lock position for session: " + o.sessionId());

		Collection<KerPositionDetail> ret = new LinkedList<>();
		KerOrder toLock = this.fatory.create(KerOrder.class, o);
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
					"[FATAL]Internal state changed, but not enough position to lock for session: " + o.sessionId());

		return ret;
	}

	/**
	 * Get all locked position of this position engine.
	 * 
	 * @return locked positions
	 * @throws KerError throw exception on failure calculating close profit by date.
	 */
	public Collection<KerPositionDetail> locked() throws KerError {
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
	 * @throws KerError throw exception on failure calculating close profit by date.
	 */
	public Collection<KerPositionDetail> closed() throws KerError {
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
	
	public Collection<KerPositionDetail> current() throws KerError {
		var ret = new LinkedList<KerPositionDetail>();
		for (var rt : this.details) {
			ret.add(rt.current());
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
		this.details.add(new RuntimePositionDetail(rep, this.context, this.fatory));
	}
}

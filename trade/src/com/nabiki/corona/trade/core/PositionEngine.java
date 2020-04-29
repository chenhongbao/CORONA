package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerTradeReport;

public class PositionEngine {
	private final RuntimeInfo runtime;
	private final String symbol;
	private final List<RuntimePositionDetail> details = new LinkedList<>();

	public PositionEngine(String symbol, RuntimeInfo runtime, Collection<RuntimePositionDetail> init) throws KerError {
		this.symbol = symbol;
		this.runtime = runtime;
		if (init != null) {
			for (var d : init) {
				if (d.origin().symbol().compareTo(this.symbol) != 0)
					throw new KerError("Wrong symbol. Want " + this.symbol + " but found " + d.origin().symbol());
				this.details.add(d);
			}
		}
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

	public String symbol() {
		return this.symbol;
	}
	
	public synchronized void lock(KerOrder o) throws KerError {
		if (!canLock(o))
			throw new KerError("Can't lock position for order: " + o.orderId());
		
		KerOrder noLock = o;
		var iter = this.details.iterator();
		
		while(iter.hasNext() && noLock.volume() > 0) {
			noLock = iter.next().lock(noLock);
		}
		
		if (noLock.volume() > 0)
			throw new KerError(
					"[FATAL]Internal state changed, but not enough position to lock for order: " + o.orderId());
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
		this.details.add(new RuntimePositionDetail(rep, this.runtime));
	}
}

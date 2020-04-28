package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
		
		KerPositionDetail sumLocked() {
			// TODO summarize locked positions
			return null;
		}
		
		KerPositionDetail sumClosed() {
			// TODO summarize closed positions
			return null;
		}

		KerPositionDetail origin() {
			return this.origin;
		}

		KerPositionDetail available() {
			var l = sumLocked();
			var c = sumClosed();
			var a = ensure(null);
			
			// TODO Calculate some fields in position detail, leaving some for caller to fill.
			
			return null;
		}
		
		KerPositionDetail own() {
			// TODO return currently own position
			return null;
		}

		Collection<KerPositionDetail> locked() {
			return this.locked;
		}

		Collection<KerPositionDetail> closed() {
			return this.closed;
		}

		KerTradeReport close(KerTradeReport origin) {
			// TODO If given trade report belongs to a trade session that closes positions in this position detail,
			// remove that positions as many as possible. Then return remaining part of trade to be remove from other
			// position details if there are.
			return null;
		}
	}

}

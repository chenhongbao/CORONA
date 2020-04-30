package com.nabiki.corona.trade.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.data.KerOrderImpl;
import com.nabiki.corona.kernel.data.KerPositionDetailImpl;
import com.nabiki.corona.kernel.data.KerTradeReportImpl;
import com.nabiki.corona.trade.RuntimeInfo;

public class RuntimePositionDetail {
	private final String symbol;
	private final RuntimeInfo info;
	private final KerPositionDetail origin;
	private final List<KerPositionDetail> locked = new LinkedList<>();
	private final List<KerPositionDetail> closed = new LinkedList<>();

	public RuntimePositionDetail(KerTradeReport rep, RuntimeInfo info) throws KerError {
		this.symbol = rep.symbol();
		this.info = info;
		this.origin = ensure(rep);
	}

	public RuntimePositionDetail(KerPositionDetail origin, Collection<KerPositionDetail> locked,
			Collection<KerPositionDetail> closed, RuntimeInfo info) {
		this.symbol = origin.symbol();
		this.info = info;
		this.origin = origin;
		if (locked != null)
			this.locked.addAll(locked);
		if (closed != null)
			this.closed.addAll(closed);
	}

	// If rep is null, return an empty position.
	private KerPositionDetail ensure(KerTradeReport rep) throws KerError {
		if (rep.offsetFlag() != State.OFFSET_OPEN)
			throw new KerError("Position detail must be initialized by an open trade.");
		
		double byMny = getMarginRateMoney(rep.symbol(), rep.direction());
		double byVol = getMarginRateVolume(rep.symbol(), rep.direction());
		double lastSettle = lastSettle(rep.symbol());
		double margin = getMargin(rep.symbol(), rep.price(), rep.volume(), byMny, byVol);
		
		// TODO Exchange margin rate needs a new query to remote counter. Trade-off too big.
		double exMargin = margin;
		
		var p = new KerPositionDetailImpl();
		
		p.brokerId(rep.brokerId());
		p.closeAmount(0.0);
		p.closeProfitByDate(0.0);
		p.closeProfitByTrade(0.0);
		p.closeVolume(0);
		p.combSymbol("");
		p.direction(rep.direction());
		p.exchangeId(rep.exchangeId());
		p.exchangeMargin(exMargin);
		p.hedgeFlag(rep.hedgeFlag());
		p.investorId(rep.investorId());
		p.investUnitId(rep.investUnitId());
		p.lastSettlementPrice(lastSettle);
		p.margin(margin);
		p.marginRateByMoney(byMny);
		p.marginRateByVolume(byVol);
		p.openDate(rep.tradeDate());
		p.openPrice(rep.price());
		p.positionProfitByDate(0.0);
		p.positionProfitByTrade(0.0);
		p.settlementId(rep.settlementId());
		p.settlementPrice(0.0);
		p.symbol(rep.symbol());
		
		// TODO The field, time first volume, is especially for DCE, but useless.
		p.timeFirstVolume(0);
		
		p.tradeId(rep.tradeId());
		p.tradeSessionId(rep.sessionId());
		p.tradeType(rep.tradeType());
		p.tradingDay(rep.tradingDay());
		p.volume(rep.volume());
		
		return p;
	}
	
	private double getMarginRateVolume(String s, char direction) throws KerError {
		var m = this.info.margin(s);
		if (m == null)
			throw new KerError("Margin not found: " + s);
		
		if (direction == State.DIRECTION_BUY) {
			return m.longMarginRatioByVolume();
		} else if (direction == State.DIRECTION_SELL) {
			return m.shortMarginRatioByVolume();
		} else {
			throw new KerError("Unknown direction: " + String.valueOf(direction));
		}
	}

	private double getMarginRateMoney(String s, char direction) throws KerError {
		var m = this.info.margin(s);
		if (m == null)
			throw new KerError("Margin not found: " + s);
		
		if (direction == State.DIRECTION_BUY) {
			return m.longMarginRatioByMoney();
		} else if (direction == State.DIRECTION_SELL) {
			return m.shortMarginRatioByMoney();
		} else {
			throw new KerError("Unknown direction: " + String.valueOf(direction));
		}
	}

	private double getMargin(String symbol, double price, int volume, double byMny, double byVol) throws KerError {
		if (byVol != 0)
			return volume * byVol;
		else {
			var inst = this.info.instrument(symbol);
			if (inst == null)
				throw new KerError("Instrument not found: " + symbol);
			
			return inst.volumeMultiple() * volume * price * byMny;
		}
	}
	
	private double lastSettle(String symbol) throws KerError {
		var t = this.info.lastTick(symbol);
		if (t == null)
			throw new KerError("Tick not found: " + symbol);
		
		return t.lastPrice();
	}

	// Get the locked position.
	private KerPositionDetail sumLocked() {
		var a = new KerPositionDetailImpl(origin());

		int volume = 0;
		double margin = 0.0, exMargin = 0.0;
		for (var p : locked()) {
			volume += p.volume();
			margin += p.margin();
			exMargin += p.exchangeMargin();
		}

		a.volume(volume);
		a.margin(margin);
		a.exchangeMargin(exMargin);

		return a;
	}

	// Get the closed position.
	private KerPositionDetail sumClosed() {
		var a = new KerPositionDetailImpl(origin());

		// Sum up.
		int volume = 0, closeVolume = 0;
		double closeAmount = 0.0, closeProfitByDate = 0.F, closeProfitByTrade = 0.0, margin = 0.0, exMargin = 0.0;
		for (var c : closed()) {
			volume += c.volume();
			closeVolume += c.closeVolume();
			closeAmount += c.closeAmount();
			closeProfitByDate += c.closeProfitByDate();
			closeProfitByTrade += c.closeProfitByTrade();
			margin += c.margin();
			exMargin += c.exchangeMargin();
		}

		// Reset some fields.
		a.volume(volume);
		a.closeVolume(closeVolume);
		a.closeAmount(closeAmount);
		a.closeProfitByDate(closeProfitByDate);
		a.closeProfitByTrade(closeProfitByTrade);
		a.margin(margin);
		a.exchangeMargin(exMargin);

		return a;
	}

	public String symbol() {
		return this.symbol;
	}

	/**
	 * Get the original position detail that initializes this instance. The original piece of information will not be
	 * changed.
	 * 
	 * @return original position detail
	 */
	public KerPositionDetail origin() {
		return this.origin;
	}

	/**
	 * Get available position that are neither closed nor locked. The return instance is newly allocated and any change
	 * to it will not affect the original data.
	 * 
	 * @return available position detail
	 * @throws KerError throw exception on failure calculating internal data
	 */
	public KerPositionDetail available() throws KerError {
		var l = sumLocked();
		var c = sumClosed();
		var a = copyPart(origin(), origin().volume() - l.volume() - c.closeVolume());

		// Profit info.
		calculatePositionInfo(a);
		return a;
	}

	/**
	 * Get own position that are not closed yet. The return instance is newly allocated and any change
	 * to it will not affect the original data.
	 * 
	 * @return own position detail
	 * @throws KerError throw exception on failure calculating internal data
	 */
	public KerPositionDetail own() throws KerError {
		var c = sumClosed();
		var a = copyPart(origin(), origin().volume() - c.closeVolume());

		// Profit info.
		calculatePositionInfo(a);
		return a;
	}

	/**
	 * Get a summarization of current position detail. It includes original volume as well as close and position profit
	 * info. The return instance is newly created, any change to it won't affect the original data.
	 * 
	 * @return current summarization of position detail
	 * @throws KerError KerError throw exception on failure calculating internal data
	 */
	public KerPositionDetail current() throws KerError {
		var c = sumClosed();
		var a = new KerPositionDetailImpl(origin());

		// Set close info.
		a.closeProfitByDate(c.closeProfitByDate());
		a.closeProfitByTrade(c.closeProfitByTrade());
		a.closeAmount(c.closeAmount());
		a.closeVolume(c.closeVolume());

		// Profit info.
		calculatePositionInfo(a);
		return a;
	}

	/**
	 * Get the locked position details. The return reference is the internal data. Don't change it if you don't want to 
	 * modify the internal states.
	 * 
	 * @return locked position details
	 */
	protected List<KerPositionDetail> locked() {
		return this.locked;
	}

	/**
	 * Get the closed position details. Like locked(), it return the reference of internal data.
	 * 
	 * @return closed position details
	 */
	protected List<KerPositionDetail> closed() {
		return this.closed;
	}

	/**
	 * Lock the position for close order and return the unfilled part of order.
	 * 
	 * @param o order
	 * @return new created order with unfilled volume
	 * @throws KerError throw exception if the order has wrong state
	 */
	public KerOrder lock(KerOrder o) throws KerError {
		if (o.offsetFlag() == State.OFFSET_OPEN)
			throw new KerError("Can't lock position for an open order.");

		int lockVol = 0;
		var a = available();
		if (a.volume() == 0)
			return new KerOrderImpl(o);
		
		if (a.volume() < 0)
			throw new KerError("[FATAL]Negative position volume.");

		if (a.volume() <= o.volume()) {
			locked.add(a);
			lockVol = a.volume();
		} else {
			var l = copyPart(a, o.volume());
			locked.add(l);
			lockVol = l.volume();
		}

		var r = new KerOrderImpl(o);
		r.volume(r.volume() - lockVol);

		return r;
	}

	/**
	 * Copy a part of position from original position. The original position is not changed and the return position is a
	 * new instance, and any change to the return instance doesn't affect the original position.
	 * 
	 * @param origin   original position
	 * @param splitVol volume to copy out of the original position
	 * @return new create position of the given volume
	 * @throws KerError throw exception if the original position has been closed(or partly)
	 */
	private KerPositionDetail copyPart(KerPositionDetail origin, int splitVol) throws KerError {
		if (origin.closeVolume() > 0 || origin.closeAmount() > 0 || origin.closeProfitByDate() > 0
				|| origin.closeProfitByTrade() > 0)
			throw new KerError("Can't split a closed position.");

		if (splitVol < 0)
			throw new KerError("Can't split a negative volume position.");

		var r = new KerPositionDetailImpl(origin);
		
		r.margin(r.margin() * splitVol / r.volume());
		r.exchangeMargin(r.exchangeMargin() * splitVol / r.volume());
		r.volume(splitVol);
		return r;
	}

	/**
	 * Cancel the trade with session ID.
	 * 
	 * @param sessionId session ID
	 */
	public void cancel(String sessionId) {
		var iter = locked().iterator();
		while (iter.hasNext()) {
			if (iter.next().tradeSessionId().compareTo(sessionId) == 0) {
				iter.remove();
			}
		}
	}

	/**
	 * Check the trade session ID of the current position detail. If it matches the given trade report's session ID,
	 * close the position that was locked by the trade of the same session ID. Otherwise, nothing happens.
	 * 
	 * @param rep the trade report to close
	 * @return the trade volume left to close in other position details
	 * @throws KerError
	 */
	public KerTradeReport close(KerTradeReport rep) throws KerError {
		int closeVol = 0;
		var iter = locked().listIterator();

		while (iter.hasNext() && closeVol < rep.volume()) {
			var n = iter.next();

			if (n.tradeSessionId().compareTo(rep.sessionId()) != 0)
				continue;

			if (n.volume() <= rep.volume()) {
				// Close all volume of the current locked position.
				// Calculate other close info.
				calculateCloseInfo(n, rep.price());

				// Move the closed position from locked to closed.
				iter.remove();
				closed().add(n);

				closeVol += n.closeVolume();
			} else {
				var cp = copyPart(n, rep.volume());

				// Close a part of the current locked position.
				calculateCloseInfo(cp, rep.price());

				// Replace the original locked position with new one, of less volume.
				var cp2 = copyPart(n, n.volume() - rep.volume());
				iter.set(cp2);
				
				// Add closed position to closed.
				closed().add(cp);

				closeVol += cp.closeVolume();
			}
		}

		// Return trade left to close in other position details.
		var r = new KerTradeReportImpl(rep);
		r.volume(r.volume() - closeVol);

		return r;
	}

	/**
	 * Close all volume, denoted by volume(), in the given position detail. Thus it sets closeVolume to volume and
	 * calculates other close info based on that.
	 * 
	 * @param toClose    position to close
	 * @param closePrice close price
	 * @throws KerError throws if failing to get instrument info
	 */
	private void calculateCloseInfo(KerPositionDetail toClose, double closePrice) throws KerError {
		var inst = this.info.instrument(origin.symbol());
		if (inst == null)
			throw new KerError("Instrument not found: " + origin.symbol());

		toClose.closeVolume(toClose.volume());

		double ca = toClose.closeVolume() * inst.volumeMultiple() * closePrice;
		toClose.closeAmount(ca);

		double pd = profit(toClose.lastSettlementPrice(), closePrice, toClose.closeVolume(), inst.volumeMultiple(),
				origin.direction());
		toClose.closeProfitByDate(pd);

		double pt = profit(toClose.openPrice(), closePrice, toClose.closeVolume(), inst.volumeMultiple(),
				origin.direction());
		toClose.closeProfitByTrade(pt);
	}

	private void calculatePositionInfo(KerPositionDetail pos) throws KerError {
		var inst = this.info.instrument(origin.symbol());
		if (inst == null)
			throw new KerError("Instrument not found: " + origin.symbol());

		var tick = this.info.lastTick(symbol());
		if (tick == null)
			throw new KerError("Tick not found: " + origin.symbol());

		double pd = profit(pos.lastSettlementPrice(), tick.lastPrice(), pos.volume(), inst.volumeMultiple(),
				pos.direction());
		pos.positionProfitByDate(pd);

		double pt = profit(pos.openPrice(), tick.lastPrice(), pos.volume(), inst.volumeMultiple(), pos.direction());
		pos.positionProfitByTrade(pt);
	}

	private double profit(double open, double close, int volume, int multi, char direction) throws KerError {
		double ret = 0.0;
		switch (direction) {
		case State.DIRECTION_BUY:
			ret = (close - open) * volume * multi;
			break;
		case State.DIRECTION_SELL:
			ret = (open - close) * volume * multi;
			break;
		default:
			throw new KerError("Unhandled unknown direction: " + String.valueOf(direction));
		}

		return ret;
	}
}

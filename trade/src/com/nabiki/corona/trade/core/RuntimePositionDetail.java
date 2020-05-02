package com.nabiki.corona.trade.core;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.Utils;
import com.nabiki.corona.api.State;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class RuntimePositionDetail {
	private final String symbol;
	private final RuntimeInfo info;
	private final KerPositionDetail origin;
	private final List<KerPositionDetail> locked = new LinkedList<>();
	private final List<KerPositionDetail> closed = new LinkedList<>();

	// Data factory.
	private final DataFactory factory;

	public RuntimePositionDetail(KerTradeReport rep, RuntimeInfo info, DataFactory factory) throws KerError {
		this.symbol = rep.symbol();
		this.info = info;
		this.factory = factory;
		this.origin = ensure(rep);
	}

	public RuntimePositionDetail(KerPositionDetail origin, Collection<KerPositionDetail> locked,
			Collection<KerPositionDetail> closed, RuntimeInfo info, DataFactory factory) {
		this.symbol = origin.symbol();
		this.info = info;
		this.factory = factory;
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

		// Compute margin.
		double byMny = getMarginRateMoney(rep.symbol(), rep.direction());
		double byVol = getMarginRateVolume(rep.symbol(), rep.direction());
		double lastSettle = lastSettle(rep.symbol());
		double margin = getMargin(rep.symbol(), rep.price(), rep.volume(), byMny, byVol);

		// TODO Exchange margin rate needs a new query to remote counter. Trade-off too big.
		double exMargin = margin;

		// Compute open commission.
		double comm = getOpenCommission(rep.symbol(), rep.price(), rep.volume());

		var p = this.factory.kerPositionDetail();

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
		p.openCommission(comm);
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

	private double getOpenCommission(String s, double price, int volume) throws KerError {
		var inst = this.info.instrument(s);
		if (inst == null)
			throw new KerError("Instrument not found: " + s);

		var commRate = this.info.commission(s);
		if (commRate == null)
			throw new KerError("Commission rate not found: " + s);

		return Utils.marginOrCommission(price, volume, inst.volumeMultiple(), commRate.openRatioByMoney(),
				commRate.openRatioByVolume());
	}

	private double getCloseCommission(String s, double price, int volume, LocalDate positionTradingDay)
			throws KerError {
		var inst = this.info.instrument(s);
		if (inst == null)
			throw new KerError("Instrument not found: " + s);

		var commRate = this.info.commission(s);
		if (commRate == null)
			throw new KerError("Commission rate not found: " + s);

		if (compareDate(this.info.tradingDay(), positionTradingDay)) {
			// Close today position.
			return Utils.marginOrCommission(price, volume, inst.volumeMultiple(), commRate.closeTodayRatioByMoney(),
					commRate.closeTodayRatioByVolume());
		} else {
			// Close yesterday position.
			return Utils.marginOrCommission(price, volume, inst.volumeMultiple(), commRate.closeRatioByMoney(),
					commRate.closeRatioByVolume());
		}
	}

	private boolean compareDate(LocalDate d1, LocalDate d2) {
		return d1.getYear() == d2.getYear() && d1.getDayOfYear() == d2.getDayOfYear();
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
		if (byMny != 0.0 && byVol != 0.0)
			throw new KerError("Ambiguity of margin rates and both by-volume and by-money are non-zero.");

		var inst = this.info.instrument(symbol);
		if (inst == null)
			throw new KerError("Instrument not found: " + symbol);

		return Utils.marginOrCommission(price, volume, inst.volumeMultiple(), byMny, byVol);
	}

	private double lastSettle(String symbol) throws KerError {
		var t = this.info.lastTick(symbol);
		if (t == null)
			throw new KerError("Tick not found: " + symbol);

		return t.preSettlementPrice();
	}

	// Get the locked position.
	private KerPositionDetail sumLocked() {
		var a = this.factory.kerPositionDetail(origin());

		int volume = 0;
		double margin = 0.0, exMargin = 0.0, openComm = 0.0;
		for (var p : locked()) {
			volume += p.volume();
			margin += p.margin();
			exMargin += p.exchangeMargin();
			openComm += p.openCommission();
		}

		a.volume(volume);
		a.margin(margin);
		a.exchangeMargin(exMargin);
		a.openCommission(openComm);

		return a;
	}

	// Get the closed position.
	private KerPositionDetail sumClosed() {
		var a = this.factory.kerPositionDetail(origin());

		// Sum up.
		int volume = 0, closeVolume = 0;
		double closeAmount = 0.0, closeProfitByDate = 0.F, closeProfitByTrade = 0.0, margin = 0.0, exMargin = 0.0,
				openComm = 0.0, closeComm = 0.0;
		for (var c : closed()) {
			volume += c.volume();
			closeVolume += c.closeVolume();
			closeAmount += c.closeAmount();
			closeProfitByDate += c.closeProfitByDate();
			closeProfitByTrade += c.closeProfitByTrade();
			margin += c.margin();
			exMargin += c.exchangeMargin();
			openComm += c.openCommission();
			closeComm += c.closeCommission();
		}

		// Reset some fields.
		a.volume(volume);
		a.closeVolume(closeVolume);
		a.closeAmount(closeAmount);
		a.closeProfitByDate(closeProfitByDate);
		a.closeProfitByTrade(closeProfitByTrade);
		a.margin(margin);
		a.exchangeMargin(exMargin);
		a.openCommission(openComm);
		a.closeCommission(closeComm);

		return a;
	}

	public String symbol() {
		return this.symbol;
	}

	public double lockedCommission() {
		return 0.0D;
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
	 * Get own position that are not closed yet. The return instance is newly allocated and any change to it will not
	 * affect the original data.
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
		var a = this.factory.kerPositionDetail(origin());

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
	 * Lock the position for close order and return the locked part of order.
	 * 
	 * @param o order
	 * @return the locked position
	 * @throws KerError throw exception if the order has wrong state
	 */
	public KerPositionDetail lock(KerOrder o) throws KerError {
		if (o.offsetFlag() == State.OFFSET_OPEN)
			throw new KerError("Can't lock position for an open order.");

		var a = available();
		if (a.volume() == 0) {
			return a;
		}

		if (a.volume() < 0)
			throw new KerError("[FATAL]Negative position volume.");

		if (a.volume() <= o.volume()) {
			locked.add(a);
			return a;
		} else {
			var l = copyPart(a, o.volume());
			locked.add(l);
			return l;
		}
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
				|| origin.closeProfitByTrade() > 0 || origin.closeCommission() > 0)
			throw new KerError("Can't split a closed position.");

		if (splitVol < 0)
			throw new KerError("Can't split a negative volume position.");

		var r = this.factory.kerPositionDetail(origin);

		r.margin(r.margin() * splitVol / r.volume());
		r.exchangeMargin(r.exchangeMargin() * splitVol / r.volume());
		r.openCommission(r.openCommission() * splitVol / r.volume());
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
		var r = this.factory.kerTradeReport(rep);
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
		var inst = this.info.instrument(toClose.symbol());
		if (inst == null)
			throw new KerError("Instrument not found: " + toClose.symbol());

		// Close volume.
		toClose.closeVolume(toClose.volume());
		// Close amount.
		double ca = toClose.closeVolume() * inst.volumeMultiple() * closePrice;
		toClose.closeAmount(ca);
		// Close profit by date.
		double pd = profit(toClose.lastSettlementPrice(), closePrice, toClose.closeVolume(), inst.volumeMultiple(),
				origin.direction());
		toClose.closeProfitByDate(pd);
		// Close profit by trade.
		double pt = profit(toClose.openPrice(), closePrice, toClose.closeVolume(), inst.volumeMultiple(),
				origin.direction());
		toClose.closeProfitByTrade(pt);
		// Close commission.
		toClose.closeCommission(
				getCloseCommission(toClose.symbol(), closePrice, toClose.closeVolume(), toClose.tradingDay()));
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

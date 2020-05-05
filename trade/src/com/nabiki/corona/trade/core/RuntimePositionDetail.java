package com.nabiki.corona.trade.core;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.Utils;
import com.nabiki.corona.api.State;
import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class RuntimePositionDetail {
	private final String symbol;
	private final RuntimeInfo info;
	private final List<KerPositionDetail> locked = new LinkedList<>();
	private final List<KerPositionDetail> closed = new LinkedList<>();
	
	private boolean isSettled;
	private KerPositionDetail origin;

	// Data factory.
	private final DataFactory factory;

	/**
	 * Constructor for a new trade position. The settlement mark is false.
	 * 
	 * @param rep trade report
	 * @param info runtime info
	 * @param factory data factory
	 * @throws KerError throw exception when fail to create position detail for new trade.
	 */
	public RuntimePositionDetail(KerTradeReport rep, RuntimeInfo info, DataFactory factory) throws KerError {
		this.symbol = rep.symbol();
		this.info = info;
		this.factory = factory;
		this.isSettled = false;
		this.origin = ensure(rep);
	}

	/**
	 * Constructor for loading a settled position data into the object. The settlement mark is set to true.
	 * 
	 * @param origin origin position
	 * @param locked locked position
	 * @param closed closed position
	 * @param info  runtime info
	 * @param factory data factory
	 */
	public RuntimePositionDetail(KerPositionDetail origin, Collection<KerPositionDetail> locked,
			Collection<KerPositionDetail> closed, RuntimeInfo info, DataFactory factory) {
		this.symbol = origin.symbol();
		this.info = info;
		this.factory = factory;
		this.isSettled = true;
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

		var inst = this.info.instrument(symbol);
		if (inst == null)
			throw new KerError("Instrument not found: " + symbol);

		// Compute margin.
		double byMny = getMarginRateMoney(rep.symbol(), rep.direction());
		double byVol = getMarginRateVolume(rep.symbol(), rep.direction());
		double lastSettle = lastSettle(rep.symbol());
		int multi = inst.volumeMultiple();

		// For today's newly open position, position price is open price. So as margin.
		// After today's settlement, it is today's settlement price.
		double margin = getMargin(rep.symbol(), rep.price(), rep.volume(), multi, byMny, byVol);

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
		p.closeCommission(0.0);
		p.settlementId(rep.settlementId());
		p.settlementPrice(0.0);
		p.symbol(rep.symbol());
		p.volumeMultiple(multi);

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

		if (Utils.same(this.info.tradingDay(), positionTradingDay)) {
			// Close today position.
			return Utils.marginOrCommission(price, volume, inst.volumeMultiple(), commRate.closeTodayRatioByMoney(),
					commRate.closeTodayRatioByVolume());
		} else {
			// Close yesterday position.
			return Utils.marginOrCommission(price, volume, inst.volumeMultiple(), commRate.closeRatioByMoney(),
					commRate.closeRatioByVolume());
		}
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

	private double getMargin(String symbol, double price, int volume, int multi, double byMny, double byVol)
			throws KerError {
		if (byMny != 0.0 && byVol != 0.0)
			throw new KerError("Ambiguity of margin rates and both by-volume and by-money are non-zero.");

		return Utils.marginOrCommission(price, volume, multi, byMny, byVol);
	}

	private double lastSettle(String symbol) throws KerError {
		var t = this.info.lastTick(symbol);
		if (t == null)
			throw new KerError("Tick not found: " + symbol);

		return t.preSettlementPrice();
	}

	// Get the locked position.
	private KerPositionDetail sumLocked() throws KerError {
		return sumPositionDetails(this.locked);
	}
	
	private KerPositionDetail sumClosed() throws KerError {
		return sumPositionDetails(this.closed);
	}

	// Get the closed position.
	private KerPositionDetail sumPositionDetails(List<KerPositionDetail> ps) throws KerError {
		var a = origin();

		// Sum up.
		int volume = 0, closeVolume = 0;
		double closeAmount = 0.0, closeProfitByDate = 0.F, closeProfitByTrade = 0.0, margin = 0.0, exMargin = 0.0,
				openComm = 0.0, closeComm = 0.0;
		for (var p : ps) {
			volume += p.volume();
			closeVolume += p.closeVolume();
			closeAmount += p.closeAmount();
			closeProfitByDate += p.closeProfitByDate();
			closeProfitByTrade += p.closeProfitByTrade();
			margin += p.margin();
			exMargin += p.exchangeMargin();
			openComm += p.openCommission();
			closeComm += p.closeCommission();
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
	
	/**
	 * Initialize a settled position for a new day trading. It will set pre-like fields, clear old fields and records.
	 * 
	 * @throws KerError initialize an unsettled position throws exception.
	 */
	public void init() throws KerError {
		if (!isSettled())
			throw new KerError("Can't initialize a unsettled runtime position.");
		
		// Settlement price.
		origin.lastSettlementPrice(origin.settlementPrice());
		origin.settlementPrice(0.0);
		// Commission.
		origin.openCommission(0.0);
		
		// Decrease the origin position by the number of closed position and remove closed position.
		var c = sumClosed();
		this.origin = copyPart(this.origin, this.origin.volume() - c.volume());
		if (this.closed.size() > 0)
			this.closed.clear();
		
		// Check if locked has position, if it is, error.
		if (this.locked.size() > 0)
			throw new KerError("Locked position not cleared in settlement: " + symbol());
		
		// Mark.
		this.isSettled = false;
	}
	
	public boolean isSettled() {
		return this.isSettled;
	}

	public double lockedCommission() throws KerError {
		return sumLocked().closeCommission();
	}

	/**
	 * Settle this position detail.
	 * 
	 * @param settlementPrice settlement price
	 * @throws KerError throw exception when fail getting margin
	 */
	public void settle(double settlementPrice) throws KerError {
		// Clear all locked positions if there are.
		if (this.locked.size() > 0)
			this.locked.clear();

		// Update original position detail.
		origin.settlementPrice(settlementPrice);

		var m = getMargin(symbol(), origin.settlementPrice(), origin.volume(), origin.volumeMultiple(),
				origin.marginRateByMoney(), origin.marginRateByVolume());
		origin.margin(m);
		origin.exchangeMargin(m);
		
		this.isSettled = true;
	}

	/**
	 * Get the original position detail that initializes this instance. The original piece of information will not be
	 * changed.
	 * 
	 * @return original position detail
	 */
	public KerPositionDetail origin() {
		return this.factory.kerPositionDetail(this.origin);
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
		var a = copyPart(this.origin, origin.volume() - l.volume() - c.closeVolume());

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
		var a = copyPart(origin, origin.volume() - c.closeVolume());

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
		var a = origin();

		// Set close info.
		a.closeProfitByDate(c.closeProfitByDate());
		a.closeProfitByTrade(c.closeProfitByTrade());
		a.closeAmount(c.closeAmount());
		a.closeVolume(c.closeVolume());
		a.closeCommission(c.closeCommission());

		// Profit info.
		calculatePositionInfo(a);
		return a;
	}

	/**
	 * Get the locked position details. The elements are new created.
	 * 
	 * @return locked position details
	 * @throws KerError throw exception on failure calculating close profit by date.
	 */
	public List<KerPositionDetail> locked() throws KerError {
		List<KerPositionDetail> ret = new LinkedList<>();
		for (var p : this.locked) {
			var n = this.factory.kerPositionDetail(p);
			calculateCloseInfo2(n);
			ret.add(n);
		}
		return ret;
	}

	/**
	 * Get the closed position details. The elements are new created.
	 * 
	 * @return closed position details
	 * @throws KerError throw exception on failure calculating close profit by date.
	 */
	public List<KerPositionDetail> closed() throws KerError {
		List<KerPositionDetail> ret = new LinkedList<>();
		for (var p : this.closed) {
			var n = this.factory.kerPositionDetail(p);
			calculateCloseInfo2(n);
			ret.add(n);
		}
		return ret;
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
			// Calculate close info when locking to provide position in early time.
			calculateCloseInfo1(a, o.price());
			locked.add(a);
			return a;
		} else {
			var n = copyPart(a, o.volume());
			calculateCloseInfo1(n, o.price());
			locked.add(n);
			return n;
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
		if (splitVol < 0)
			throw new KerError("Can't split a negative volume position.");

		var r = this.factory.kerPositionDetail(origin);

		// Margin.
		r.margin(r.margin() * splitVol / r.volume());
		r.exchangeMargin(r.exchangeMargin() * splitVol / r.volume());
		// Commission.
		r.openCommission(r.openCommission() * splitVol / r.volume());
		r.closeCommission(r.closeCommission() * splitVol / r.volume());
		// Close profit.
		r.closeProfitByDate(r.closeProfitByDate() * splitVol / r.volume());
		r.closeProfitByTrade(r.closeProfitByTrade() * splitVol / r.volume());
		// Amount.
		r.closeAmount(r.closeAmount() * splitVol / r.volume());
		// Volume.
		r.closeVolume(splitVol);
		r.volume(splitVol);
		return r;
	}

	/**
	 * Cancel the trade with session ID.
	 * 
	 * @param sessionId session ID
	 */
	public void cancel(String sessionId) throws KerError {
		// Don't use locked() here because it create new instances and the underlying data is not changed.
		var iter = this.locked.iterator();
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
				// Move the closed position from locked to closed.
				iter.remove();
				closed().add(n);

				closeVol += n.closeVolume();
			} else {
				var cp = copyPart(n, rep.volume());
				// Add closed position to closed.
				closed().add(cp);

				// Replace the original locked position with new one, of less volume.
				var cp2 = copyPart(n, n.volume() - rep.volume());
				iter.set(cp2);

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
	private void calculateCloseInfo1(KerPositionDetail toClose, double closePrice) throws KerError {
		var inst = this.info.instrument(toClose.symbol());
		if (inst == null)
			throw new KerError("Instrument not found: " + toClose.symbol());

		// Close volume.
		toClose.closeVolume(toClose.volume());
		// Close amount.
		double ca = toClose.closeVolume() * inst.volumeMultiple() * closePrice;
		toClose.closeAmount(ca);
		// Close profit by trade.
		double pt = Utils.profit(toClose.openPrice(), closePrice, toClose.closeVolume(), toClose.volumeMultiple(),
				origin.direction());
		toClose.closeProfitByTrade(pt);
		// Close commission.
		toClose.closeCommission(
				getCloseCommission(toClose.symbol(), closePrice, toClose.closeVolume(), toClose.tradingDay()));
	}

	private void calculateCloseInfo2(KerPositionDetail toClose) throws KerError {
		// Get close price from close amount.
		var closePrice = toClose.closeAmount() / toClose.closeVolume() / toClose.volumeMultiple();
		
		// Close profit by date.
		double previousPrice = previousPrice(toClose);
		double pd = Utils.profit(previousPrice, closePrice, toClose.closeVolume(), toClose.volumeMultiple(),
				origin.direction());
		toClose.closeProfitByDate(pd);
	}

	private void calculatePositionInfo(KerPositionDetail pos) throws KerError {
		var inst = this.info.instrument(origin.symbol());
		if (inst == null)
			throw new KerError("Instrument not found: " + origin.symbol());

		var tick = this.info.lastTick(symbol());
		if (tick == null)
			throw new KerError("Tick not found: " + origin.symbol());

		// Position at hand.
		int volume = pos.volume() - pos.closeVolume();

		// Mark-to-market rules to decide the prices to compute profit by date.
		double previousPrice = previousPrice(pos);
		double currentPrice = currentPrice(pos, tick);

		double pd = Utils.profit(previousPrice, currentPrice, volume, inst.volumeMultiple(), pos.direction());
		pos.positionProfitByDate(pd);

		// Position profit by trade.
		double pt = Utils.profit(pos.openPrice(), tick.lastPrice(), volume, inst.volumeMultiple(), pos.direction());
		pos.positionProfitByTrade(pt);
	}

	private double previousPrice(KerPositionDetail pos) {
		// Position profit by date.
		// If it is today's position, today's open price is previous price, otherwise yesterday's settlement price.
		if (pos.tradingDay().compareTo(this.info.tradingDay()) == 0)
			// today's position
			return pos.openPrice();
		else
			// yesterday's position
			return pos.lastSettlementPrice();
	}

	private double currentPrice(KerPositionDetail pos, Tick tick) {
		// If we have settlement price, use it as current price.
		// Or use last price as a estimated settlement price, but will change after settling market.
		if (Utils.validPrice(pos.settlementPrice()))
			return pos.settlementPrice();
		else
			return tick.lastPrice();
	}
}

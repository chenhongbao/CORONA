package com.nabiki.corona.object;

import com.nabiki.corona.client.api.*;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.object.gson.*;
import com.nabiki.corona.system.packet.api.*;

public class DefaultDataFactory implements DataFactory {
	// Shared object.
	private final static DataFactory factory = new DefaultDataFactory();

	public static DataFactory create() {
		return factory; 
	}
	
	private DefaultDataFactory() {}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> clz) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			return (T) new RxErrorMessage();
		} else if (clz.equals(KerCandle.class) || clz.equals(Candle.class)) {
			return (T) new KerCandleGson();
		} else if (clz.equals(CashMove.class)) {
			return (T) new CashMoveGson();
		} else if (clz.equals(KerAccount.class) || clz.equals(Account.class)) {
			return (T) new KerAccountGson();
		} else if (clz.equals(KerAction.class)) {
			return (T) new KerActionGson();
		}
		else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> clz, T param) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			// RxErrorMessage.
			return (T) create((RxErrorMessage)param);
		} else if (clz.equals(KerCandle.class) || clz.equals(Candle.class)) {
			return (T) create((KerCandle)param);
		} else if (clz.equals(CashMove.class)) {
			return (T) create((CashMove)param);
		} else if (clz.equals(KerAccount.class) || clz.equals(Account.class)) {
			return (T) create((KerAccount)param);
		} else if (clz.equals(KerAction.class)) {
			return (T) create((KerAction)param);
		}
		else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}
	
	private KerActionGson create(KerAction param) {
		var r = new KerActionGson();
		r.accountId = param.accountId();
		r.ipAddress = param.ipAddress();
		r.macAddress = param.macAddress();
		r.orderId = param.orderId();
		r.sessionId = param.sessionId();
		r.symbol = param.symbol();
		return r;
	}

	private KerAccountGson create(KerAccount param) {
		var r = new KerAccountGson();
		r.accountId = param.accountId();
		r.accountState = param.accountState();
		r.available = param.available();
		r.balance = param.balance();
		r.brokerId = param.brokerId();
		r.closeProfit = param.closeProfit();
		r.commission = param.commission();
		r.currencyId = param.currencyId();
		r.currentMargin = param.currentMargin();
		r.deposit = param.deposit();
		r.exchangeMargin = param.exchangeMargin();
		r.frozenCash = param.frozenCash();
		r.frozenCommission = param.frozenCommission();
		r.frozenMargin = param.frozenMargin();
		r.interest = param.interest();
		r.interestBase = param.interestBase();
		r.positionProfit = param.positionProfit();
		r.preBalance = param.preBalance();
		r.preDeposit = param.preDeposit();
		r.preMargin = param.preMargin();
		r.reserve = param.reserve();
		r.reserveBalance = param.reserveBalance();
		r.settlementId = param.settlementId();
		r.tradingDay = Utils.deepCopy(param.tradingDay());
		r.withdraw = param.withdraw();
		r.withdrawQuota = param.withdrawQuota();
		return r;
	}

	private CashMoveGson create(CashMove param) {
		var r = new CashMoveGson();
		r.accountId = param.accountId();
		r.amount = param.amount();
		r.currencyId = param.currencyId();
		r.note = param.note();
		r.type = param.type();
		return r;
	}

	private KerCandleGson create(KerCandle param) {
		var r = new KerCandleGson();
		r.actionDay = Utils.deepCopy(param.actionDay());
		r.closePrice = param.closePrice();
		r.highPrice = param.highPrice();
		r.isDay = param.isDay();
		r.isLastOfDay = param.isLastOfDay();
		r.isRealTime = param.isRealTime();
		r.lowPrice = param.lowPrice();
		r.minutePeriod = param.minutePeriod();
		r.openInterest = param.openInterest();
		r.openPrice = param.openPrice();
		r.symbol = param.symbol();
		r.tradingDay = Utils.deepCopy(param.tradingDay());
		r.updateTime = Utils.deepCopy(param.updateTime());
		r.volume = param.volume();
		return r;
	}

	private RxErrorMessage create(RxErrorMessage param) {
		var r = new RxErrorMessage();
		var t = (RxErrorMessage)param;
		// Copy.
		r.error(t.error());
		r.last(t.last());
		r.requestSeq(t.requestSeq());
		r.responseSeq(t.responseSeq());
		r.values(t.values());
		return r;
	}
}

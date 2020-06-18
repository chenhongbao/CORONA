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
		} else if (clz.equals(KerActionError.class)) {
			return (T) new KerActionErrorGson();
		} else if (clz.equals(KerCommission.class)) {
			return (T) new KerCommissionGson();
		} else if (clz.equals(KerInstrument.class)) {
			return (T) new KerInstrumentGson();
		} else if (clz.equals(KerLogin.class)) {
			return (T) new KerLoginGson();
		} else if (clz.equals(KerMargin.class)) {
			return (T) new KerMarginGson();
		} else if (clz.equals(KerNewAccount.class)) {
			return (T) new KerNewAccountGson();
		} else if (clz.equals(KerOrder.class)) {
			return (T) new KerOrderGson();
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
		} else if (clz.equals(KerActionError.class)) {
			return (T) create((KerActionError)param);
		} else if (clz.equals(KerCommission.class)) {
			return (T) create((KerCommission)param);
		} else if (clz.equals(KerInstrument.class)) {
			return (T) create((KerInstrument)param);
		} else if (clz.equals(KerLogin.class)) {
			return (T) create((KerLogin)param);
		} else if (clz.equals(KerMargin.class)) {
			return (T) create((KerMargin)param);
		} else if (clz.equals(KerNewAccount.class)) {
			return (T) create((KerNewAccount)param);
		} else if (clz.equals(KerOrder.class)) {
			return (T) create((KerOrder)param);
		}
		else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}
	
	private KerOrderGson create(KerOrder param) {
		var r = new KerOrderGson();
		r.accountId = param.accountId();
		r.contigentConditon = param.contigentConditon();
		r.direction = param.direction();
		r.gtdDate = Utils.deepCopy(param.gtdDate());
		r.hedgeFlag = param.hedgeFlag();
		r.ipAddress = param.ipAddress();
		r.macAddress = param.macAddress();
		r.minVolume = param.minVolume();
		r.note = param.note();
		r.offsetFlag = param.offsetFlag();
		r.orderId = param.orderId();
		r.price = param.price();
		r.priceType = param.priceType();
		r.sessionId = param.sessionId();
		r.stopPrice = param.stopPrice();
		r.symbol = param.symbol();
		r.timeCondition = param.timeCondition();
		r.volume = param.volume();
		r.volumeCondition = param.volumeCondition();
		return r;
	}

	private KerNewAccountGson create(KerNewAccount param) {
		var r = new KerNewAccountGson();
		r.accountId = param.accountId();
		r.pin = param.pin();
		r.role = param.role();
		return r;
	}

	private KerMarginGson create(KerMargin param) {
		var r = new KerMarginGson();
		r.brokerId = param.brokerId();
		r.exchangeId = param.exchangeId();
		r.hedgeFlag = param.hedgeFlag();
		r.investorId = param.investorId();
		r.isRelative = param.isRelative();
		r.longMarginRatioByMoney = param.longMarginRatioByMoney();
		r.longMarginRatioByVolume = param.longMarginRatioByVolume();
		r.shortMarginRatioByMoney = param.shortMarginRatioByMoney();
		r.shortMarginRatioByVolume = param.shortMarginRatioByVolume();
		r.symbol = param.symbol();
		return r;
	}

	private KerLoginGson create(KerLogin param) {
		var r = new KerLoginGson();
		r.accountId = param.accountId();
		r.address = param.address();
		r.pin = param.pin();
		r.role = param.role();
		return r;
	}

	private KerInstrumentGson create(KerInstrument param) {
		var r = new KerInstrumentGson();
		r.createDate = Utils.deepCopy(param.createDate());
		r.deliveryMonth = param.deliveryMonth();
		r.deliveryYear = param.deliveryYear();
		r.endDelivDate = Utils.deepCopy(param.endDelivDate());
		r.exchangeId = param.exchangeId();
		r.exchangeInstId = param.exchangeInstId();
		r.expireDate = Utils.deepCopy(param.expireDate());
		r.instLifePhase = param.instLifePhase();
		r.isTrading = param.isTrading();
		r.longMarginRatio = param.longMarginRatio();
		r.maxLimitOrderVolume = param.maxLimitOrderVolume();
		r.maxMarketOrderVolume = param.maxMarketOrderVolume();
		r.minLimitOrderVolume = param.minLimitOrderVolume();
		r.minMarketOrderVolume = param.minMarketOrderVolume();
		r.openDate = Utils.deepCopy(param.openDate());
		r.positionDateType = param.positionDateType();
		r.positionType = param.positionType();
		r.priceTick = param.priceTick();
		r.productClass = param.productClass();
		r.productId = param.productId();
		r.shortMarginRatio = param.shortMarginRatio();
		r.startDelivDate = Utils.deepCopy(param.startDelivDate());
		r.symbol = param.symbol();
		r.underlyingInstrId = param.underlyingInstrId();
		r.underlyingMultiple = param.underlyingMultiple();
		r.volumeMultiple = param.volumeMultiple();
		return r;
	}

	private KerCommissionGson create(KerCommission param) {
		var r = new KerCommissionGson();
		r.brokerId = param.brokerId();
		r.closeRatioByMoney = param.closeRatioByMoney();
		r.closeRatioByVolume = param.closeRatioByVolume();
		r.closeTodayRatioByMoney = param.closeTodayRatioByMoney();
		r.closeTodayRatioByVolume = param.closeTodayRatioByVolume();
		r.exchangeId = param.exchangeId();
		r.investorId = param.investorId();
		r.openRatioByMoney = param.openRatioByMoney();
		r.openRatioByVolume = param.openRatioByVolume();
		r.symbol = param.symbol();
		return r;
	}

	private KerActionErrorGson create(KerActionError param) {
		var r = new KerActionErrorGson();
		r.action = (KerActionGson)param.action();
		r.error = param.error();
		return r;
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

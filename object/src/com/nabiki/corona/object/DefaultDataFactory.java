package com.nabiki.corona.object;

import com.nabiki.corona.client.api.Candle;
import com.nabiki.corona.object.gson.KerCandleGson;
import com.nabiki.corona.system.api.DataFactory;
import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.packet.api.RxErrorMessage;

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
		} if (clz.equals(KerCandle.class) || clz.equals(Candle.class)) {
			return (T) new KerCandleGson();
		} else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> clz, T param) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			// RxErrorMessage.
			return (T) create((RxErrorMessage)param);
		} else if(clz.equals(KerCandle.class) || clz.equals(Candle.class)) {
			return (T) create((KerCandle)param);
		}
		else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}
	
	private KerCandleGson create(KerCandle param) {
		var r = new KerCandleGson();
		r.actionDay = param.actionDay();
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
		r.tradingDay = param.tradingDay();
		r.updateTime = param.updateTime();
		r.volume = param.volume();
		return null;
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

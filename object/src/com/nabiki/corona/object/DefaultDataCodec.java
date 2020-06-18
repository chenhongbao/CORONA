package com.nabiki.corona.object;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nabiki.corona.client.api.*;
import com.nabiki.corona.object.gson.*;
import com.nabiki.corona.object.gson.info.*;
import com.nabiki.corona.object.gson.packet.*;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.info.api.*;
import com.nabiki.corona.system.packet.api.*;

public class DefaultDataCodec implements DataCodec {
	private Gson gson;
	private Charset charset = StandardCharsets.UTF_8;
	private DataFactory factory = DefaultDataFactory.create();
	
	// Shared object.
	private final static DataCodec codec = new DefaultDataCodec();
	
	public static synchronized DataCodec create() {
		return codec;
	}
	
	private DefaultDataCodec() {
		this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
	}

	@Override
	public <T> byte[] encode(T a) throws KerError {
		if (a instanceof RxErrorMessage) { 
			var proxy = serializeProxy((RxErrorMessage)a);
			return this.gson.toJson(proxy,  proxy.getClass()).getBytes(this.charset);
		} else if (a instanceof Candle || a instanceof KerCandle) {
			return this.gson.toJson(a,  KerCandleGson.class).getBytes(this.charset);
		} else if (a instanceof CashMove) {
			return this.gson.toJson(a, CashMoveGson.class).getBytes(this.charset);
		} else if (a instanceof KerAccount || a instanceof Account) {
			return this.gson.toJson(a, KerAccountGson.class).getBytes(this.charset);
		} else if (a instanceof KerAction) {
			return this.gson.toJson(a, KerActionGson.class).getBytes(this.charset);
		} else if (a instanceof KerActionError) {
			return this.gson.toJson(a, KerActionErrorGson.class).getBytes(this.charset);
		} else if (a instanceof KerCommission) {
			return this.gson.toJson(a, KerCommissionGson.class).getBytes(this.charset);
		} else if (a instanceof KerInstrument) {
			return this.gson.toJson(a, KerInstrumentGson.class).getBytes(this.charset);
		} else if (a instanceof KerLogin) {
			return this.gson.toJson(a, KerLoginGson.class).getBytes(this.charset);
		} else if (a instanceof KerMargin) {
			return this.gson.toJson(a, KerMarginGson.class).getBytes(this.charset);
		} else if (a instanceof KerNewAccountGson) {
			return this.gson.toJson(a, KerNewAccountGson.class).getBytes(this.charset);
		} else if (a instanceof KerOrder) {
			return this.gson.toJson(a, KerOrderGson.class).getBytes(this.charset);
		}
		else
			throw new KerError("Unsupported type: " + a.getClass().getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] b, Class<T> clz) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			return (T) deserializeProxy(this.gson.fromJson(new String(b, this.charset), RxErrorMessageGson.class));
		} if(clz.equals(MarketTimeSet.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), MarketTimeSetGson.class);
		} else if (clz.equals(KerCandle.class) || clz.equals(Candle.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerCandleGson.class);
		} else if (clz.equals(CashMove.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), CashMoveGson.class);
		} else if (clz.equals(KerAccount.class) || clz.equals(Account.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerAccountGson.class);
		} else if (clz.equals(KerAction.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerActionGson.class);
		} else if (clz.equals(KerActionError.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerActionErrorGson.class);
		} else if (clz.equals(KerCommission.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerCommissionGson.class);
		} else if (clz.equals(KerInstrument.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerInstrumentGson.class);
		} else if (clz.equals(KerLogin.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerLoginGson.class);
		} else if (clz.equals(KerMargin.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerMarginGson.class);
		} else if (clz.equals(KerNewAccount.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerNewAccountGson.class);
		} else if (clz.equals(KerOrder.class)) {
			return (T) this.gson.fromJson(new String(b, this.charset), KerOrderGson.class);
		}
		else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}

	private RxErrorMessageGson serializeProxy(RxErrorMessage msg) {
		var r = new RxErrorMessageGson();
		r.error = msg.error();
		r.last = msg.last();
		r.requestSeq = msg.requestSeq();
		r.responseSeq = msg.responseSeq();
		r.time = msg.time();
		r.values = msg.values();
		return r;
	}
	
	private RxErrorMessage deserializeProxy(RxErrorMessageGson proxy) throws KerError {
		var r = this.factory.create(RxErrorMessage.class);
		r.error(proxy.error);
		r.last(proxy.last);
		r.requestSeq(proxy.requestSeq);
		r.responseSeq(proxy.responseSeq);
		r.time(proxy.time);
		r.values(proxy.values);
		return r;
	}
}

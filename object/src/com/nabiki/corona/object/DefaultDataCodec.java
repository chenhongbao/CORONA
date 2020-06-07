package com.nabiki.corona.object;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nabiki.corona.client.api.Candle;
import com.nabiki.corona.object.gson.KerCandleGson;
import com.nabiki.corona.object.gson.info.MarketTimeSetGson;
import com.nabiki.corona.object.gson.packet.RxErrorMessageGson;
import com.nabiki.corona.system.api.DataCodec;
import com.nabiki.corona.system.api.DataFactory;
import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.info.api.MarketTimeSet;
import com.nabiki.corona.system.packet.api.RxErrorMessage;

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

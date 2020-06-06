package com.nabiki.corona.object;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nabiki.corona.system.api.DataCodec;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.packet.api.RxErrorMessage;

public class DefaultDataCodec implements DataCodec {
	private Gson gson;
	private Charset charset = StandardCharsets.UTF_8;
	
	// Types.
	private Type typeRxErrorMessage;
	
	public static DataCodec create() {
		return new DefaultDataCodec();
	}
	
	private DefaultDataCodec() {
		this.gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
		// Types.
		// TODO Need to replace generic type with a non-generic type.
		this.typeRxErrorMessage = new TypeToken<RxErrorMessage>(){}.getType();
	}

	@Override
	public <T> byte[] encode(T a) throws KerError {
		if (a instanceof RxErrorMessage) { 
			// TODO Need to replace generic type with a non-generic type.
			return this.gson.toJson(a,  this.typeRxErrorMessage).getBytes(this.charset);
		} else
			throw new KerError("Unsupported type: " + a.getClass().getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T decode(byte[] b, Class<T> clz) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			// TODO Need to replace generic type with a non-generic type.
			return (T) this.gson.fromJson(new String(b, this.charset), this.typeRxErrorMessage);
		} else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}
}

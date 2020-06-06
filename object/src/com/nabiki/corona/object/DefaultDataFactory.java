package com.nabiki.corona.object;

import com.nabiki.corona.system.api.DataFactory;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.packet.api.RxErrorMessage;

public class DefaultDataFactory implements DataFactory {

	public static DataFactory create() {
		return new DefaultDataFactory();
	}
	
	private DefaultDataFactory() {}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> clz) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			return (T) new RxErrorMessage();
		} else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> clz, T param) throws KerError {
		if (clz.equals(RxErrorMessage.class)) {
			// RxErrorMessage.
			var r = new RxErrorMessage();
			var t = (RxErrorMessage)param;
			// Copy.
			r.error(t.error());
			r.last(t.last());
			r.requestSeq(t.requestSeq());
			r.responseSeq(t.responseSeq());
			r.values(t.values());
			return (T) r;
		} else
			throw new KerError("Unsupported type: " + clz.getCanonicalName());
	} 
}

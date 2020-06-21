package com.nabiki.corona.trade.core;

import com.nabiki.corona.system.api.KerAction;
import com.nabiki.corona.system.api.KerOrder;
import com.nabiki.corona.system.api.KerQueryAccount;
import com.nabiki.corona.system.api.KerQueryCommission;
import com.nabiki.corona.system.api.KerQueryInstrument;
import com.nabiki.corona.system.api.KerQueryMargin;
import com.nabiki.corona.system.api.KerQueryPositionDetail;

public class Request<T> {
	
	private RequestType type = RequestType.Unknown;
	
	private T request;

	public Request(T r) {
		if (r != null) { 
			this.type = chooseType(r);
			this.request = r;
		} else
			throw new NullPointerException("Generic type object null pointer");
	}

	private RequestType chooseType(T obj) {
		if (obj instanceof KerOrder)
			return RequestType.Order;
		else if (obj instanceof KerAction)
			return RequestType.Action;
		else if (obj instanceof KerQueryInstrument)
			return RequestType.QueryInstrument;
		else if (obj instanceof KerQueryMargin)
			return RequestType.QueryMargin;
		else if (obj instanceof KerQueryCommission)
			return RequestType.QueryCommission;
		else if (obj instanceof KerQueryAccount)
			return RequestType.QueryAccount;
		else if (obj instanceof KerQueryPositionDetail)
			return RequestType.QueryPositionDetail;
		else
			return RequestType.Unknown;
	}
	
	public RequestType type() {
		return this.type;
	}
	
	public T request() {
		return this.request;
	}
}

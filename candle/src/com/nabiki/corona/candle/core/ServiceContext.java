package com.nabiki.corona.candle.core;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.info.api.RuntimeInfo;

public class ServiceContext {
	private RuntimeInfo info;
	
	public ServiceContext() {}
	
	public void info(RuntimeInfo info) {
		this.info = info;
	}
	
	public RuntimeInfo info() throws KerError {
		if (this.info == null)
			throw new KerError("Runtime info null pointer.");
		
		return this.info;
	}
}

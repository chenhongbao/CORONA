package com.nabiki.corona.portal.core;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.biz.api.TradeLocal;
import com.nabiki.corona.system.biz.api.TradeRemote;
import com.nabiki.corona.system.info.api.RuntimeInfo;

public class PortalServiceContext {
	private RuntimeInfo info;
	private TradeLocal local;
	private TradeRemote remote;
	
	public PortalServiceContext() {}
	
	public void info(RuntimeInfo info) {
		this.info = info;
	}
	
	public RuntimeInfo info() throws KerError {
		if (this.info == null)
			throw new KerError("Runtime info null pointer.");
		
		return this.info;
	}
	
	public void local(TradeLocal local) {
		this.local = local;
	}
	
	public TradeLocal local() throws KerError {
		if (this.local == null)
			throw new KerError("Trade local null pointer.");
		
		return this.local;
	}
	
	public void remote(TradeRemote remote) {
		this.remote = remote;
	}
	
	public TradeRemote remote() throws KerError {
		if (this.remote == null)
			throw new KerError("Trade remote null pointer.");
		
		return this.remote;
	}
}

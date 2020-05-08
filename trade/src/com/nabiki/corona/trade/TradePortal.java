package com.nabiki.corona.trade;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.kernel.biz.api.TradeLocal;
import com.nabiki.corona.kernel.biz.api.TradeRemote;

@Component(service = {})
public class TradePortal {
	@Reference(service = LoggerFactory.class)
	private Logger log;

	@Reference(bind = "bindTradeRemote", updated = "updatedTradeRemote", unbind = "unbindTradeRemote",
			policy = ReferencePolicy.DYNAMIC)
	private volatile TradeRemote tradeRemote;

	public void bindTradeRemote(TradeRemote remote) {
		if (remote == null)
			return;

		this.tradeRemote = remote;
		this.log.info("Bind trade remote: {}.", remote.name());
	}

	public void updatedTradeRemote(TradeRemote remote) {
		if (remote == null)
			return;

		this.tradeRemote = remote;
		this.log.info("Update trade remote: {}.", remote.name());
	}

	public void unbindTradeRemote(TradeRemote remote) {
		if (remote != this.tradeRemote)
			return;

		this.tradeRemote = null;
		this.log.info("Unbind trade remote: {}.", remote.name());
	}

	@Reference(bind = "bindTradeLocal", updated = "updatedTradeLocal", unbind = "unbindTradeLocal",
			policy = ReferencePolicy.DYNAMIC)
	private volatile TradeLocal tradeLocal;
	
	public void bindTradeLocal(TradeLocal local) {
		if (local == null)
			return;
		
		this.tradeLocal = local;
		this.log.info("Bind trade local: {}.", local.name());
	}
	
	public void updatedTradeLocal(TradeLocal local) {
		if (local == null)
			return;
		
		this.tradeLocal = local;
		this.log.info("Update trade local: {}.", local.name());
	}
	
	public void unbindTradeLocal(TradeLocal local) {
		if (local != this.tradeLocal)
			return;
		
		this.tradeLocal = null;
		this.log.info("Unbind trade local: {}.", local.name());
	}
	
	@Activate
	public void start(ComponentContext ctx) {
		// TODO activate
		// TODO schedule to perform settlement and init on trade local.
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		// TODO stop
	}
}

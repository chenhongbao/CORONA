package com.nabiki.corona.portal;

import java.util.Collection;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.biz.api.*;
import com.nabiki.corona.system.info.api.*;
import com.nabiki.corona.portal.core.LoginManager;
import com.nabiki.corona.portal.inet.ClientInputAdaptor;

@Component(service = {})
public class TradeService {
	class ServiceAdaptor extends ClientInputAdaptor {
		public ServiceAdaptor() {}
		
		private TradeLocal local() throws KerError {
			if (local == null)
				throw new KerError("Local trade service not available.");
			return local;
		}
		
		private TradeRemote remote() throws KerError {
			if (remote == null)
				throw new KerError("Remote trade service not available.");
			return remote;
		}

		@Override
		public void error(KerError e) {
			log.error("Trade service failed: {}.", e.message(), e);
		}

		@Override
		public KerAccount queryAccount(KerQueryAccount qry) {
			return local.account(qry.accountId());
		}

		@Override
		public Collection<KerPositionDetail> queryPositionDetail(KerQueryPositionDetail q) {
			// TODO position detail
			return super.queryPositionDetail(q);
		}

		@Override
		public Collection<KerOrderStatus> queryOrderStatus(KerQueryOrderStatus q) {
			// TODO order status
			return super.queryOrderStatus(q);
		}

		@Override
		public Collection<String> queryListSessionId(String accountId) {
			// TODO list session id
			return super.queryListSessionId(accountId);
		}

		@Override
		public Collection<String> queryListAccountId() {
			// TODO list account id
			return super.queryListAccountId();
		}

		@Override
		public KerOrderError requestOrder(KerOrder o) {
			// TODO order
			return super.requestOrder(o);
		}

		@Override
		public KerError requestAction(KerAction a) {
			// TODO action
			return super.requestAction(a);
		}

		@Override
		public KerError newAccount(KerNewAccount a) {
			try {
				// Check duplicated account and write account info.
				LoginManager.get().writeNewAccount(a);
				// Create investor account.
				local.createAccount(a.accountId());
				
				return new KerError(0);
			} catch (KerError e) {
				log.error("Fail creating new account: {}.", e.message(), e);
				return e;
			}
		}

		@Override
		public KerError moveCash(CashMove move) {
			// TODO move cash
			return super.moveCash(move);
		}

		@Activate
		public void start(ComponentContext ctx) {
			// TODO start
		}

		@Deactivate
		public void stop(ComponentContext ctx) {
			// TODO stop
		}
	}
	
	@Reference(service = LoggerFactory.class)
	private Logger log;

	@Reference(bind = "bindRuntimeInfo", unbind = "unbindRuntimeInfo", policy = ReferencePolicy.DYNAMIC)
	private volatile RuntimeInfo info;

	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;

		this.info = info;
		this.log.info("Bind runtime info.");
	}

	public void unbindRuntimeInfo(RuntimeInfo info) {
		if (this.info != info)
			return;

		this.info = null;
		this.log.info("Unbind runtime info.");
	}
	
	// Trade services.
	@Reference(bind = "setLocal", unbind = "unsetLocal", policy = ReferencePolicy.DYNAMIC)
	private volatile TradeLocal local;
	
	@Reference(bind = "setRemote", unbind = "unsetRemote", policy = ReferencePolicy.DYNAMIC)
	private volatile TradeRemote remote;
	
	public void setLocal(TradeLocal local) {
		this.local = local;
		this.log.info("Set trade local: {}.", local.name());
	}
	
	public void unsetLocal(TradeLocal local) {
		if (local == this.local) {
			this.local = null;
			this.log.info("Unset trade local: {}.", local.name());
		}
	}
	
	public void setRemote(TradeRemote remote) {
		this.remote = remote;
		this.log.info("Set trade remote: {}.", remote.name());
	}
	
	public void unsetRemote(TradeRemote remote) {
		if (remote == this.remote) {
			this.remote = null;
			this.log.info("Unset trade remote: {}.", remote.name());
		}
	}
}

package com.nabiki.corona.portal;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.system.api.*;
import com.nabiki.corona.portal.inet.*;
import com.nabiki.corona.system.biz.api.*;
import com.nabiki.corona.system.info.api.*;
import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.portal.core.LoginManager;

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
			log.error("Trade service failed: {}", e.message(), e);
		}

		@Override
		public KerAccount queryAccount(KerQueryAccount qry) {
			return local.account(qry.accountId());
		}

		@Override
		public Collection<KerPositionDetail> queryPositionDetail(KerQueryPositionDetail q) {
			try {
				return local().positionDetails(q.accountId(), q.symbol());
			} catch (KerError e) {
				log.error("Fail query position detail: {}", e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public Collection<KerOrderStatus> queryOrderStatus(KerQueryOrderStatus q) {
			try {
				return local().orderStatus(q.sessionId());
			} catch (KerError e) {
				log.error("Fail query order status: {}", e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public Collection<String> queryListSessionId(String accountId) {
			try {
				return local().sessionIdsOfAccount(accountId);
			} catch (KerError e) {
				log.error("Fail query session ID for account: {}. {}", accountId, e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public Collection<String> queryListAccountId() {
			try {
				return local().accountIds();
			} catch (KerError e) {
				log.error("Fail query account ID: {}", e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public KerOrderError requestOrder(KerOrder o) {
			KerOrderError e = null;
			
			try {				
				int r = remote().order(o);
				// Create response.
				e = factory.create(KerOrderError.class);
				e.order(o);
				// r < 0 is not possible currently, but it may return this value in future.
				if (r > 0)
					e.error(new KerError(0, "Order queueing."));
				else if (r == 0)
					e.error(new KerError(0, "Order sent."));
				else
					e.error(new KerError(r, "Order enqueue error."));
				
				return e;
			} catch (KerError ex) {
				log.error("Fail creating response for order request: {}", ex.message(), ex);
				return null;
			}
		}

		@Override
		public KerError requestAction(KerAction a) {
			try {
				int r = remote().action(a);
				if (r > 0)
					return new KerError(0, "Action queueing.");
				else if (r == 0)
					return new KerError(0, "Action sent.");
				else
					return new KerError(r, "Action enqueue error.");
			} catch (KerError e) {
				log.error("Fail enqueueing action. {}", e.message(), e);
				return e;
			}
		}

		@Override
		public Collection<KerTradeReport> queryTradeReport(KerQueryTradeReport q) {
			try {
				return local().tradeReport(q.sessionId());
			} catch (KerError e) {
				log.error("Fail query trade reports for session: {}. {}", q.sessionId(), e.message(), e);
				return new LinkedList<>();
			}
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
			try {
				local().moveCash(move);
				return new KerError(0);
			} catch (KerError e) {
				return e;
			}
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
	
	// Data factory.
	private final DataFactory factory = DefaultDataFactory.create();
	
	// Adaptor.
	private final ClientInputAdaptor adaptor = new ServiceAdaptor();
	private final ClientInputExecutor executor = new ClientInputExecutor(this.adaptor);

	// Socket thread.
	private ServerSocket ss;
	private final ExecutorService threads = Executors.newCachedThreadPool();
	
	// Default listening port.
	public final static int port = 10689;
	
	public TradeService() {
	}
	
	@Activate
	public void start(ComponentContext ctx) {
		this.threads.execute(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ss = new ServerSocket(TradeService.port);
					while (!ss.isClosed()) {
						try {
							threads.execute(new RoleServer(ss.accept(), executor));
						} catch (IOException e) {
							log.warn("Fail accepting incoming connection. {}", e.getMessage(), e);
						} catch (KerError e) {
							log.error("Fail creating daemon for incoming connection. {}", e.message(), e);
						}
					}
				} catch (IOException e) {
					log.error("Fail listening on port: " + MarketDataService.port + ". " + e.getMessage());
				}
			}

		}));
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		if (!ss.isClosed())
			try {
				ss.close();
			} catch (IOException e) {
				log.error("Fail closing server socket."  + e.getMessage());
			}
	}
}

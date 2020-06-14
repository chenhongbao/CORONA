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
import com.nabiki.corona.portal.core.PortalServiceContext;

@Component(service = {})
public class TradeService {
	class ServiceAdaptor extends ClientInputAdaptor {
		public ServiceAdaptor() {}

		@Override
		public void error(KerError e) {
			log.error("Trade service failed: {}", e.message(), e);
		}

		@Override
		public KerAccount queryAccount(KerQueryAccount qry) {
			try {
				return context.local().account(qry.accountId());
			} catch (KerError e) {
				log.error("Fail query account with id : {}. {}", qry.accountId(), e.message(), e);
				return null;
			}
		}

		@Override
		public Collection<KerPositionDetail> queryPositionDetail(KerQueryPositionDetail q) {
			try {
				return context.local().positionDetails(q.accountId(), q.symbol());
			} catch (KerError e) {
				log.error("Fail query position detail: {}", e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public Collection<KerOrderStatus> queryOrderStatus(KerQueryOrderStatus q) {
			try {
				return context.local().orderStatus(q.sessionId());
			} catch (KerError e) {
				log.error("Fail query order status: {}", e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public Collection<String> queryListSessionId(String accountId) {
			try {
				return context.local().sessionIdsOfAccount(accountId);
			} catch (KerError e) {
				log.error("Fail query session ID for account: {}. {}", accountId, e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public Collection<String> queryListAccountId() {
			try {
				return context.local().accountIds();
			} catch (KerError e) {
				log.error("Fail query account ID: {}", e.message(), e);
				return new LinkedList<>();
			}
		}

		@Override
		public KerOrderStatus requestOrder(KerOrder o) {
			// TODO rewrite, first allocate resources for order, then return the allocation results to client.
			//      send order to remote.
			//      create order ID and set into order, method also creates the session ID. retrieve the session ID
			//      from the allocation result.
			return null;
		}

		@Override
		public KerError requestAction(KerAction a) {
			try {
				// TODO verify the session/order status. if not completed, send action.
				int r = context.remote().action(a);
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
				return context.local().tradeReport(q.sessionId());
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
				context.local().createAccount(a.accountId());
				
				return new KerError(0);
			} catch (KerError e) {
				log.error("Fail creating new account: {}.", e.message(), e);
				return e;
			}
		}

		@Override
		public KerError moveCash(CashMove move) {
			try {
				context.local().moveCash(move);
				return new KerError(0);
			} catch (KerError e) {
				return e;
			}
		}
	}
	
	@Reference(service = LoggerFactory.class)
	private Logger log;

	private PortalServiceContext context = new PortalServiceContext();

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void bindRuntimeInfo(RuntimeInfo info) {
		if (info == null)
			return;

		this.context.info(info);
		this.log.info("Bind runtime info {}.", info.name());
	}

	public void unbindRuntimeInfo(RuntimeInfo info) {
		try {
			if (this.context.info() != info)
				return;
		} catch (KerError e) {
			this.log.error("Fail unbinding runtime. {}", e.message(), e);
			return;
		}

		this.context.info(null);
		this.log.info("Unbind runtime info {}.", info.name());
	}
	
	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setLocal(TradeLocal local) {
		this.context.local(local);;
		this.log.info("Set trade local: {}.", local.name());
	}
	
	public void unsetLocal(TradeLocal local) {
		try {
			if (this.context.local() == local) {
				this.context.local(null);
				this.log.info("Unset trade local: {}.", local.name());
			}
		} catch (KerError e) {
			this.log.error("Fail unbinding trade local. {}", e.message(), e);
		}
	}
	
	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setRemote(TradeRemote remote) {
		this.context.remote(remote);
		this.log.info("Set trade remote: {}.", remote.name());
	}
	
	public void unsetRemote(TradeRemote remote) {
		try {
			if (this.context.remote() == remote) {
				this.context.remote(remote);
				this.log.info("Unset trade remote: {}.", remote.name());
			}
		} catch (KerError e) {
			this.log.error("Fail unbinding trade remote. {}", e.message(), e);
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

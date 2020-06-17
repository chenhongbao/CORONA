package com.nabiki.corona.portal;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
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

import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.portal.inet.*;
import com.nabiki.corona.system.biz.api.*;
import com.nabiki.corona.system.info.api.*;
import com.nabiki.corona.system.packet.api.*;
import com.nabiki.corona.OffsetFlag;
import com.nabiki.corona.OrderStatus;
import com.nabiki.corona.OrderSubmitStatus;
import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.portal.core.LoginManager;
import com.nabiki.corona.portal.core.PortalServiceContext;

@Component(service = {})
public class TradeService {
	class ServiceAdaptor extends ClientInputAdaptor {
		public ServiceAdaptor() {}

		@Override
		public void error(KerError e) {
			log.error("Trade service failed. {}", e.message(), e);
		}

		@Override
		public RxAccountMessage queryAccount(KerQueryAccount qry) {
			RxAccountMessage msg = null;
			
			try {
				msg = factory.create(RxAccountMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				msg.value(context.local().account(qry.accountId()));
			} catch (KerError e) {
				log.error("Fail query account with id : {}. {}", qry.accountId(), e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public RxPositionDetailMessage queryPositionDetail(KerQueryPositionDetail q) {
			RxPositionDetailMessage msg = null;
			
			try {
				msg = factory.create(RxPositionDetailMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				msg.values(context.local().positionDetails(q.accountId(), q.symbol()));
			} catch (KerError e) {
				log.error("Fail query position detail: {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public RxOrderStatusMessage queryOrderStatus(KerQueryOrderStatus q) {
			RxOrderStatusMessage msg = null;
			
			try {
				msg = factory.create(RxOrderStatusMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				msg.values(context.local().orderStatus(q.sessionId()));
			} catch (KerError e) {
				log.error("Fail query order status: {}", e.message(), e);
				msg.error(e);
			}
			
			return null;
		}

		@Override
		public StringMessage queryListSessionId(String accountId) {
			StringMessage msg = null;
			
			try {
				msg = factory.create(StringMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				msg.values(context.local().querySessionsOfAccount(accountId));
			} catch (KerError e) {
				log.error("Fail query session ID for account: {}. {}", accountId, e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public StringMessage queryListAccountId() {
			StringMessage msg = null;
			
			try {
				msg = factory.create(StringMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				msg.values(context.local().queryAccounts());
			} catch (KerError e) {
				log.error("Fail query account IDs. {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public RxOrderStatusMessage requestOrder(KerOrder o) {
			// First allocate resources for order, then return the allocation results to client.
			// Send order to remote.
			// Once the order is validated correct, create session ID for the request, and then order IDs for each
			// sub-order.
			RxOrderStatusMessage msg = null;
			
			try {
				msg = factory.create(RxOrderStatusMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {			
				// Create order ID and associated session ID.
				// Allocate resources for order.
				var sid = context.local().createSession(o.accountId());
				o.sessionId(sid);
				
				var r = context.local().allocateOrder(o);
				if (r.error().code() != 0) {
					// Status.
					var status = factory.create(KerOrderStatus.class);
					status.orderSubmitStatus(OrderSubmitStatus.INSERT_REJECTED);
					status.statusMessage(r.error().message());
					
					// Error.
					msg.value(status);
					msg.error(r.error());
				} else {
					// Check open/close.
					if (o.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
						// Set open order ID.
						o.orderId(context.local().createOrder(sid));
						
						// Just send open request.
						int c = context.remote().order(o);
						
						// Set status params.
						var status = factory.create(KerOrderStatus.class);
						status.orderId(o.orderId());
						status.sessionId(o.sessionId());
						
						// Set message params.
						msg.value(status);
						msg.error(getProperError(c));

					} else {
						int c = 0;
						for (var p : r.positionToClose()) {
							// Duplicate params then modify some.
							var order = factory.create(KerOrder.class, o);
							
							// Create order ID for each sub-order.
							o.orderId(context.local().createOrder(order.sessionId()));
							
							if (Utils.same(p.tradingDay(), context.local().tradingDay()))
								// Close today's position.
								o.offsetFlag(OffsetFlag.OFFSET_CLOSE_TODAY);
							else
								// Close yesterday's position.
								o.offsetFlag(OffsetFlag.OFFSET_CLOSE_YESTERDAY);
							
							// Set the split volume.
							o.volume(p.volume());
							c = context.remote().order(order);

							// Set status params.
							var status = factory.create(KerOrderStatus.class);
							status.orderId(o.orderId());
							status.sessionId(o.sessionId());
							
							if (c < 0)
								status.orderSubmitStatus(OrderSubmitStatus.INSERT_REJECTED);
							else
								status.orderSubmitStatus(OrderSubmitStatus.INSERT_SUBMITTED);
							
							// Set message params.
							msg.value(status);
						}
					}
				}
			} catch (KerError e) {
				log.error("Fail requesting order. {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public RxActionErrorMessage requestAction(KerAction a) {
			RxActionErrorMessage msg = null;
			
			try {
				msg = factory.create(RxActionErrorMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				var actionError = factory.create(KerActionError.class);
				var statuses = context.local().orderStatus(a.sessionId());
				// Unknown session ID, not order info found.
				if (statuses.size() == 0) {
					var e = new KerError("No order status found for session: " + a.sessionId() + " or order: " + a.orderId());
					// Response.
					actionError.error(e);
					actionError.action(a);
					// Error.
					msg.error(e);
				} else {
					if (isTradedOrCanceled(statuses)) {
						var e = new KerError("Order outdated, it has been traded or canceled.");
						// Response.
						actionError.error(e);
						actionError.action(a);
						// Error.
						msg.error(e);
					} else {
						KerError e = null;
						int r = context.remote().action(a);
						// Response.
						actionError.error(getProperError(r));
						actionError.action(a);
						msg.value(actionError);
						// Error.
						msg.error(e);
					}
				}
			} catch (KerError e) {
				log.error("Fail enqueueing action. {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}
		
		private KerError getProperError(int c) {
			if (c > 0)
				return new KerError(0, "Request queueing.");
			else if (c == 0)
				return new KerError(0, "Request sent.");
			else
				return new KerError(c, "Request enqueue error.");
		}
		
		private boolean isTradedOrCanceled(List<KerOrderStatus> statuses) {
			for (var s : statuses)
				if (s.orderStatus() == OrderStatus.ALL_TRADED || s.orderStatus() == OrderStatus.CANCELED)
					return true;
			return false;
		}

		@Override
		public RxTradeReportMessage queryTradeReport(KerQueryTradeReport q) {
			RxTradeReportMessage msg = null;
			
			try {
				msg = factory.create(RxTradeReportMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				msg.values(context.local().tradeReport(q.sessionId()));
			} catch (KerError e) {
				log.error("Fail query trade reports for session: {}. {}", q.sessionId(), e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public RxErrorMessage newAccount(KerNewAccount a) {
			RxErrorMessage msg = null;
			
			try {
				msg = factory.create(RxErrorMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				// Check duplicated account and write account info.
				LoginManager.get().writeNewAccount(a);
				// Create investor account.
				context.local().createAccount(a.accountId());
			} catch (KerError e) {
				log.error("Fail creating new account. {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
		}

		@Override
		public RxErrorMessage moveCash(CashMove move) {
			RxErrorMessage msg = null;
			
			try {
				msg = factory.create(RxErrorMessage.class);
			} catch (KerError e) {
				log.error("Fail creating packet message. {}", e.message(), e);
				return null;
			}
			
			try {
				context.local().moveCash(move);
			} catch (KerError e) {
				log.error("Fail moving cash. {}", e.message(), e);
				msg.error(e);
			}
			
			return msg;
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

package com.nabiki.corona.portal.inet;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.AccountRole;
import com.nabiki.corona.MessageType;
import com.nabiki.corona.object.tool.Packet;
import com.nabiki.corona.object.*;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.packet.api.*;

// Execute all request input by clients in a thread.
// The thread decodes the request messages and verify the targeted account ID before process it.
public class ClientInputExecutor implements Runnable {
	// Client input information keeper.
	public class ClientInput {
		public Packet input;
		public KerLogin user;
		public PacketServer service;

		ClientInput(Packet input, PacketServer service, KerLogin user) {
			this.input = input;
			this.service = service;
			this.user = user;
		}
	}

	private boolean stopped;
	private final DataCodec codec = DefaultDataCodec.create();
	private final DataFactory factory = DefaultDataFactory.create();
	private final ClientInputAdaptor adaptor;
	private final Thread daemon;
	private final BlockingQueue<ClientInput> queue = new LinkedBlockingQueue<>();

	public ClientInputExecutor(ClientInputAdaptor adaptor) {
		this.daemon = new Thread(this);
		this.daemon.start();
		this.daemon.setDaemon(true);
		this.adaptor = adaptor;
	}

	public void input(Packet input, PacketServer service, KerLogin user) {
		this.queue.offer(new ClientInput(input, service, user));
	}

	/**
	 * Remove inputs associated with given service after a client disconnects from server and the related packet service
	 * becomes unavailable. It returns number of removed inputs.
	 * 
	 * @param service service
	 * @return number of removed inputs
	 */
	public int remove(PacketServer service) {
		int count = 0;
		for (var inp : this.queue) {
			if (inp.service != service)
				continue;
			// Set ref to null so it won't be executed and resources re-collected.
			inp.input = null;
			inp.service = null;
			++count;
		}

		return count;
	}

	private void setParams(PacketMessage<?> msg, int rSeq, boolean last) {
		if (msg == null)
			return;

		msg.requestSeq(rSeq);
		msg.responseSeq(Utils.increaseGet());
		msg.time(LocalDateTime.now());
		msg.last(last);
	}

	private void sndPacket(short type, PacketMessage<?> msg, PacketServer remote) throws KerError {
		if (msg == null)
			return;

		var bytes = this.codec.encode(msg);
		remote.send(type, bytes, 0, bytes.length);
	}
	
	// Check the operator's account ID and its counter part in request are the same.
	// Typically for trader role and two other roles can operate on any accounts.
	private boolean isRightAccountId(String reqAccountId, KerLogin user) {
		return user.role() != AccountRole.TRADER || reqAccountId.compareTo(user.accountId()) == 0;
	}

	private void procQueryAccount(TxQueryAccountMessage req, PacketServer remote, KerLogin user) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxAccountMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_ACCOUNT, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.queryAccount(n);
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_ACCOUNT, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_ACCOUNT, rsp, remote);
				}
			}
		}
	}

	private void procQueryPositionDetail(TxQueryPositionDetailMessage req, PacketServer remote, KerLogin user)
			throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxPositionDetailMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_POSITION_DETAIL, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.queryPositionDetail(iter.next());
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_POSITION_DETAIL, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_POSITION_DETAIL, rsp, remote);
				}
			}
		}
	}

	private void procQueryOrderStatus(TxQueryOrderStatusMessage req, PacketServer remote, KerLogin user)
			throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxOrderStatusMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_ORDER_STATUS, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.queryOrderStatus(iter.next());
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_ORDER_STATUS, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_ORDER_STATUS, rsp, remote);
				}
			}
		}
	}

	private void procQueryListSessionId(TxQueryListSessionIdMessage req, PacketServer remote, KerLogin user)
			throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(StringMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_LIST_SESSION_ID, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.queryListSessionId(iter.next().accountId());
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_LIST_SESSION_ID, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_LIST_SESSION_ID, rsp, remote);
				}
			}
		}
	}

	private void procQueryListAccountId(TxQueryListAccountId req, PacketServer remote, KerLogin user) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(StringMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_LIST_ACCOUNT_ID, rsp, remote);
		} else {
			while (true) {
				// Don't need any information, just skip to next.
				iter.next();
				
				// Trader won't call this method, no need to auth.
				var rsp = this.adaptor.queryListAccountId();
				// Last mark.
				if (!iter.hasNext()) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_LIST_ACCOUNT_ID, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_LIST_ACCOUNT_ID, rsp, remote);
				}
			}
		}
	}

	private void procRequestAction(TxRequestActionMessage req, PacketServer remote, KerLogin user) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxActionErrorMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_ACTION_ERROR, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.requestAction(iter.next());
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_ACTION_ERROR, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_ACTION_ERROR, rsp, remote);
				}
			}
		}
	}

	private void procRequestOrder(TxRequestOrderMessage req, PacketServer remote, KerLogin user) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxOrderStatusMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_ORDER_STATUS, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.requestOrder(iter.next());
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_ORDER_STATUS, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_ORDER_STATUS, rsp, remote);
				}
			}
		}
	}

	private void procSubscription(StringMessage req, PacketServer remote) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxErrorMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_SUBSCRIBE_SYMBOL, rsp, remote);
		} else {
			while (true) {
				// Trader won't call this method, no need to auth.
				var rsp = this.adaptor.subscribeSymbol(iter.next(), remote);
				// Last mark.
				if (!iter.hasNext()) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_SUBSCRIBE_SYMBOL, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_SUBSCRIBE_SYMBOL, rsp, remote);
				}
			}
		}
	}

	private void procNewAccount(TxRequestNewAccountMessage req, PacketServer remote) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxErrorMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_SET_NEW_ACCOUNT, rsp, remote);
		} else {
			while (true) {
				// Trader won't call this method, no need to auth.
				var rsp = this.adaptor.newAccount(iter.next());
				// Last mark.
				if (!iter.hasNext()) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_SET_NEW_ACCOUNT, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_SET_NEW_ACCOUNT, rsp, remote);
				}
			}
		}
	}

	private void procMoveCash(TxCashMoveMessage req, PacketServer remote) throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxErrorMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_CASH_MOVE, rsp, remote);
		} else {
			while (true) {
				// Trader won't call this method, no need to auth.
				var rsp = this.adaptor.moveCash(iter.next());
				// Last mark.
				if (!iter.hasNext()) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_CASH_MOVE, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_CASH_MOVE, rsp, remote);
				}
			}
		}
	}

	private void procQueryTradeReport(TxQueryTradeReportMessage req, PacketServer remote, KerLogin user)
			throws KerError {
		var iter = req.values().iterator();
		if (!iter.hasNext()) {
			var rsp = this.factory.create(RxTradeReportMessage.class);
			setParams(rsp, req.requestSeq(), true);
			sndPacket(MessageType.RX_TRADE_REPORT, rsp, remote);
		} else {
			while (true) {
				var n = iter.next();
				var next = iter.hasNext();

				// Check right account.
				if (!isRightAccountId(n.accountId(), user) && !next)
					break;

				var rsp = this.adaptor.queryTradeReport(iter.next());
				// Last mark.
				if (!next) {
					setParams(rsp, req.requestSeq(), true);
					sndPacket(MessageType.RX_TRADE_REPORT, rsp, remote);
					// Must break loop.
					break;
				} else {
					setParams(rsp, req.requestSeq(), false);
					sndPacket(MessageType.RX_TRADE_REPORT, rsp, remote);
				}
			}
		}
	}

	@Override
	public void run() {
		// Set mark.
		stopped = false;

		while (!stopped) {
			try {
				var in = queue.poll(24, TimeUnit.HOURS);
				if (in.input == null || in.service == null)
					continue;

				// Decide the input type.
				switch (in.input.type()) {
				case MessageType.TX_QUERY_CLIENT_ACCOUNT:
				case MessageType.TX_QUERY_ADMIN_ACCOUNT:
					// Process input and reply.
					procQueryAccount(this.codec.decode(in.input.bytes(), TxQueryAccountMessage.class), in.service,
							in.user);
					break;
				case MessageType.TX_QUERY_CLIENT_POSITION_DETAIL:
				case MessageType.TX_QUERY_ADMIN_POSITION_DETAIL:
					procQueryPositionDetail(this.codec.decode(in.input.bytes(), TxQueryPositionDetailMessage.class),
							in.service, in.user);
					break;
				case MessageType.TX_QUERY_CLIENT_ORDER_STATUS:
				case MessageType.TX_QUERY_ADMIN_ORDER_STATUS:
					procQueryOrderStatus(this.codec.decode(in.input.bytes(), TxQueryOrderStatusMessage.class),
							in.service, in.user);
					break;
				case MessageType.TX_QUERY_CLIENT_LIST_SESSION_ID:
				case MessageType.TX_QUERY_ADMIN_LIST_SESSION_ID:
					procQueryListSessionId(this.codec.decode(in.input.bytes(), TxQueryListSessionIdMessage.class),
							in.service, in.user);
					break;
				case MessageType.TX_QUERY_ADMIN_LIST_ACCOUNT_ID:
					procQueryListAccountId(this.codec.decode(in.input.bytes(), TxQueryListAccountId.class), in.service,
							in.user);
					break;
				case MessageType.TX_REQUEST_CLIENT_ACTION:
				case MessageType.TX_REQUEST_ADMIN_ACTION:
					procRequestAction(this.codec.decode(in.input.bytes(), TxRequestActionMessage.class), in.service,
							in.user);
					break;
				case MessageType.TX_REQUEST_CLIENT_ORDER:
				case MessageType.TX_REQUEST_ADMIN_ORDER:
					procRequestOrder(this.codec.decode(in.input.bytes(), TxRequestOrderMessage.class), in.service,
							in.user);
					break;
				case MessageType.TX_SET_CLIENT_SUBSCRIBE_SYMBOLS:
					procSubscription(this.codec.decode(in.input.bytes(), StringMessage.class), in.service);
					break;
				case MessageType.TX_SET_ADMIN_NEW_ACCOUNT:
					procNewAccount(this.codec.decode(in.input.bytes(), TxRequestNewAccountMessage.class), in.service);
					break;
				case MessageType.TX_SET_ADMIN_CASH_MOVE:
					procMoveCash(this.codec.decode(in.input.bytes(), TxCashMoveMessage.class), in.service);
					break;
				case MessageType.TX_QUERY_ADMIN_TRADE_REPORT:
				case MessageType.TX_QUERY_CLIENT_TRADE_REPORT:
					procQueryTradeReport(this.codec.decode(in.input.bytes(), TxQueryTradeReportMessage.class),
							in.service, in.user);
					break;
				default:
					// Unknown message, probably hacked.
					// Close the connection immediately.
					stopped = true;
					in.service.close();
					// Remove all pending input from the disconnected clients.
					remove(in.service);
					in.service = null;
					break;
				}
			} catch (InterruptedException e) {
				this.adaptor.error(new KerError("Interrupted blocking poll. ", e));
			} catch (KerError e) {
				this.adaptor.error(e);
			}
		}
	}
}

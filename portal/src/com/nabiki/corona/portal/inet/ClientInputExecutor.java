package com.nabiki.corona.portal.inet;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.object.tool.Packet;
import com.nabiki.corona.object.*;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.packet.api.*;

public class ClientInputExecutor implements Runnable {
	// Client input information keeper.
	public class ClientInput {
		public Packet input;
		public PacketServer service;
		
		ClientInput(Packet input, PacketServer service) {
			this.input = input;
			this.service = service;
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
	
	public void input(Packet input, PacketServer service) {
		this.queue.offer(new ClientInput(input, service));
	}
	
	/**
	 * Remove inputs associated with given service after a client disconnects from server
	 * and the related packet service becomes unavailable. It returns number of removed inputs.
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

	@Override
	public void run() {
		// Set mark.
		stopped = false;
		
		while(!stopped) {
			try {
				var in = queue.poll(24, TimeUnit.HOURS);
				if (in.input == null || in.service == null)
					continue;
				
				short rspType = 0;
				byte[] rspBytes = new byte[0];
				
				// Decide the input type.
				switch(in.input.type()) {
				case MessageType.TX_QUERY_CLIENT_ACCOUNT:
				case MessageType.TX_QUERY_ADMIN_ACCOUNT:
					// Reply message type.
					rspType = MessageType.RX_ACCOUNT;
					// Decode message.
					var reqAcc = this.codec.decode(in.input.bytes(), TxQueryAccountMessage.class);
					
					// Process input and reply.
					var rspAcc = this.factory.create(RxAccountMessage.class);
					for (var a : reqAcc.values())
						rspAcc.value(this.adaptor.queryAccount(a));
					
					// Sequences.
					rspAcc.requestSeq(reqAcc.requestSeq());
					rspAcc.responseSeq(Utils.increaseGet());
					// Time stamp.
					rspAcc.time(LocalDateTime.now());
					// Last mark.
					rspAcc.last(true);
					// Encode.
					rspBytes = this.codec.encode(rspAcc);
					break;
				case MessageType.TX_QUERY_CLIENT_POSITION_DETAIL:
				case MessageType.TX_QUERY_ADMIN_POSITION_DETAIL:
					rspType = MessageType.RX_POSITION_DETAIL;
					//
					var reqPos = this.codec.decode(in.input.bytes(), TxQueryPositionDetailMessage.class);
					var rspPos = this.factory.create(RxPositionDetailMessage.class);
					for (var q : reqPos.values())
						rspPos.values(this.adaptor.queryPositionDetail(q));
					//
					rspPos.requestSeq(reqPos.requestSeq());
					rspPos.responseSeq(Utils.increaseGet());
					rspPos.time(LocalDateTime.now());
					rspPos.last(true);
					//
					rspBytes = this.codec.encode(rspPos);
					break;
				case MessageType.TX_QUERY_CLIENT_ORDER_STATUS:
				case MessageType.TX_QUERY_ADMIN_ORDER_STATUS:
					rspType = MessageType.RX_ORDER_STATUS;
					//
					var reqSta = this.codec.decode(in.input.bytes(), TxQueryOrderStatusMessage.class);
					var rspSta = this.factory.create(RxOrderStatusMessage.class);
					for (var q : reqSta.values())
						rspSta.values(this.adaptor.queryOrderStatus(q));
					//
					rspSta.requestSeq(reqSta.requestSeq());
					rspSta.responseSeq(Utils.increaseGet());
					rspSta.time(LocalDateTime.now());
					rspSta.last(true);
					//
					rspBytes = this.codec.encode(rspSta);
					break;
				case MessageType.TX_QUERY_CLIENT_LIST_SESSION_ID:
				case MessageType.TX_QUERY_ADMIN_LIST_SESSION_ID:
					rspType = MessageType.RX_LIST_SESSION_ID;
					//
					var reqSid = this.codec.decode(in.input.bytes(), StringMessage.class);
					var rspSid = this.factory.create(StringMessage.class);
					for (var q : reqSid.values())
						rspSid.values(this.adaptor.queryListSessionId(q));
					//
					rspSid.requestSeq(reqSid.requestSeq());
					rspSid.responseSeq(Utils.increaseGet());
					rspSid.time(LocalDateTime.now());
					rspSid.last(true);
					//
					rspBytes = this.codec.encode(rspSid);
					break;
				case MessageType.TX_QUERY_ADMIN_LIST_ACCOUNT_ID:
					rspType = MessageType.RX_LIST_ACCOUNT_ID;
					//
					var reqAid = this.codec.decode(in.input.bytes(), StringMessage.class);
					var rspAid = this.factory.create(StringMessage.class);
					rspAid.values(this.adaptor.queryListAccountId());
					//
					rspAid.requestSeq(reqAid.requestSeq());
					rspAid.responseSeq(Utils.increaseGet());
					rspAid.time(LocalDateTime.now());
					rspAid.last(true);
					//
					rspBytes = this.codec.encode(rspAid);
					break;
				case MessageType.TX_REQUEST_CLIENT_ACTION:
				case MessageType.TX_REQUEST_ADMIN_ACTION:
					rspType = MessageType.RX_ACTION_ERROR;
					//
					var reqAct = this.codec.decode(in.input.bytes(), TxRequestActionMessage.class);
					var rspAct = this.factory.create(RxErrorMessage.class);
					for (var a : reqAct.values())
						rspAct.value(this.adaptor.requestAction(a));
					//
					rspAct.requestSeq(reqAct.requestSeq());
					rspAct.responseSeq(Utils.increaseGet());
					rspAct.time(LocalDateTime.now());
					rspAct.last(true);
					//
					rspBytes = this.codec.encode(rspAct);
					break;
				case MessageType.TX_REQUEST_CLIENT_ORDER:
				case MessageType.TX_REQUEST_ADMIN_ORDER:
					rspType = MessageType.RX_ORDER_STATUS;
					//
					var reqOrd = this.codec.decode(in.input.bytes(), TxRequestOrderMessage.class);
					var rspOrd = this.factory.create(RxOrderStatusMessage.class);
					for (var o : reqOrd.values())
						rspOrd.value(this.adaptor.requestOrder(o));
					//
					rspOrd.requestSeq(reqOrd.requestSeq());
					rspOrd.responseSeq(Utils.increaseGet());
					rspOrd.time(LocalDateTime.now());
					rspOrd.last(true);
					//
					rspBytes = this.codec.encode(rspOrd);
					break;
				case MessageType.TX_SET_CLIENT_SUBSCRIBE_SYMBOLS:
					rspType = MessageType.RX_SUBSCRIBE_SYMBOL;
					//
					var reqSub = this.codec.decode(in.input.bytes(), StringMessage.class);
					var rspSub = this.factory.create(RxErrorMessage.class);
					for (var s : reqSub.values())
						rspSub.value(this.adaptor.subscribeSymbol(s, in.service));
					//
					rspSub.requestSeq(reqSub.requestSeq());
					rspSub.responseSeq(Utils.increaseGet());
					rspSub.time(LocalDateTime.now());
					rspSub.last(true);
					//
					rspBytes = this.codec.encode(rspSub);
					break;
				case MessageType.TX_SET_ADMIN_NEW_ACCOUNT:
					rspType = MessageType.RX_SET_NEW_ACCOUNT;
					//
					var reqNew = this.codec.decode(in.input.bytes(), TxRequestNewAccountMessage.class);
					var rspNew = this.factory.create(RxErrorMessage.class);
					for (var q : reqNew.values())
						rspNew.value(this.adaptor.newAccount(q));
					//
					rspNew.requestSeq(reqNew.requestSeq());
					rspNew.responseSeq(Utils.increaseGet());
					rspNew.time(LocalDateTime.now());
					rspNew.last(true);
					//
					rspBytes = this.codec.encode(rspNew);
					break;
				case MessageType.TX_SET_ADMIN_CASH_MOVE:
					rspType = MessageType.RX_CASH_MOVE;
					//
					var reqCas = this.codec.decode(in.input.bytes(), TxCashMoveMessage.class);
					var rspCas = this.factory.create(RxErrorMessage.class);
					for (var q : reqCas.values())
						rspCas.value(this.adaptor.moveCash(q));
					//
					rspCas.requestSeq(reqCas.requestSeq());
					rspCas.responseSeq(Utils.increaseGet());
					rspCas.time(LocalDateTime.now());
					rspCas.last(true);
					//
					rspBytes = this.codec.encode(rspCas);
					break;
				case MessageType.TX_QUERY_ADMIN_TRADE_REPORT:
				case MessageType.TX_QUERY_CLIENT_TRADE_REPORT:
					rspType = MessageType.RX_TRADE_REPORT;
					//
					var reqTra = this.codec.decode(in.input.bytes(),  TxQueryTradeReportMessage.class);
					var rspTra = this.factory.create(RxTradeReportMessage.class);
					for (var q : reqTra.values())
						rspTra.values(this.adaptor.queryTradeReport(q));
					//
					rspTra.requestSeq(reqTra.requestSeq());
					rspTra.responseSeq(Utils.increaseGet());
					rspTra.time(LocalDateTime.now());
					rspTra.last(true);
					//
					rspBytes = this.codec.encode(rspTra);
					break;
				default:
					// Unknown message, probably hacked.
					// Close the connection immediately.
					stopped = true;
					in.service.close();
					// Remove all pending input.
					remove(in.service);
					// Set current input's ref to null.
					in.service = null;
					break;
				}
				
				// Send response to peer.
				if (in.service == null)
					continue;
				
				in.service.send(rspType, rspBytes, 0, rspBytes.length);
			} catch (InterruptedException e) {
				this.adaptor.error(new KerError("Interrupted blocking poll. ", e));
			} catch (KerError e) {
				this.adaptor.error(e);
			}
		}
	}
}

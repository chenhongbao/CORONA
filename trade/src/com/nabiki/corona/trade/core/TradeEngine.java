package com.nabiki.corona.trade.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.packet.api.*;
import com.nabiki.corona.system.info.api.RemoteConfig;
import com.nabiki.corona.system.info.api.RuntimeInfo;
import com.nabiki.corona.object.DefaultDataCodec;
import com.nabiki.corona.object.tool.Packet;
import com.nabiki.corona.object.tool.PacketConnector;

public class TradeEngine implements Runnable {
	public enum State {
		STARTING, STARTED, STOPPING, STOPPED
	}
	
	private final RuntimeInfo runtime;
	private final TradeEngineListener listener;
	private final TradeEngineErrorListener errorListener;

	// Socket connection.
	private State state = State.STOPPED;
	private PacketConnector connection;

	// Data queue.
	private Thread queDaemon;
	private final BlockingQueue<Packet> dataQueue = new LinkedBlockingQueue<>();

	// Codec.
	private final DataCodec codec = DefaultDataCodec.create();

	public TradeEngine(TradeEngineListener listener, TradeEngineErrorListener errorListener, RuntimeInfo info) {
		this.runtime = info;
		this.listener = listener;
		this.errorListener = errorListener;
	}

	public synchronized void send(short type, byte[] bytes, int offset, int length) throws KerError {
		if (this.connection.closed())
			throw new KerError("Can't send through a closed or never connected socket.");
		// Send bytes.
		this.connection.send(type, bytes, offset, length);
	}

	public void tellStop() {
		this.state = State.STOPPING;
		tryClose();
	}
	
	public State state() {
		return this.state;
	}

	@Override
	public void run() {
		// Mark state.
		this.state = State.STARTING;
		
		// Queue daemon.
		this.queDaemon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (state != State.STOPPING) {
					try {
						invokePacket(dataQueue.poll(24, TimeUnit.HOURS));
					} catch (InterruptedException e) {
						// Interrupted by trade launcher to exit.
						// Do nothing here. The while loop will exit if thread is interrupted for close.
					} catch (KerError e) {
						errorListener.error(e);
					}
				}
			}
		});

		// Connect.
		try {
			this.connection = connect();
		} catch (KerError e) {
			this.errorListener.error(e);
			return;
		}
		
		// Mark state.
		this.state = State.STARTED;

		// Loop to receive packet.
		while (state == State.STARTED) {
			try {
				if (!this.dataQueue.offer(this.connection.receive()))
					this.errorListener.error(new KerError("Fail offering packet to queue."));
			} catch (KerError e) {
				this.errorListener.error(e);
			}
		}

		// Close connection.
		tryClose();

		// Interrupt data queue thread and exit.
		if (!this.queDaemon.isInterrupted())
			this.queDaemon.interrupt();

		try {
			this.queDaemon.join();
		} catch (InterruptedException e) {
		}
		
		// Mark state.
		this.state = State.STOPPED;
	}

	private PacketConnector connect() throws KerError {
		// Find connection config to remote.
		RemoteConfig conf = null;
		for (var c : this.runtime.remoteConfig().configs()) {
			if (c.name().toLowerCase().indexOf("trade") != -1) {
				conf = c;
				break;
			}
		}

		if (conf == null)
			throw new KerError("Remote connection configuration not found. Need name like \'trade\' or \trader\'");

		try {
			var address = new InetSocketAddress(InetAddress.getByName(conf.host()), conf.port());

			// Connect remote.
			var connection = new Socket();
			connection.connect(address);

			// Wrap connection into packet socket.
			return new PacketConnector(connection);
		} catch (UnknownHostException e) {
			throw new KerError("Can't find remote host " + conf.host() + ":" + conf.port());
		} catch (IOException e) {
			throw new KerError("Fail connecting remote host " + conf.host() + ":" + conf.port());
		}
	}

	private void tryClose() {
		if (this.connection.closed())
			return;

		this.connection.close();
	}

	private void invokePacket(Packet packet) throws KerError {
		switch (packet.type()) {
		case MessageType.RX_ACCOUNT:
			var account = this.codec.decode(packet.bytes(), RxAccountMessage.class);
			if (account.valueCount() != 1)
				throw new KerError("Duplicated remote accounts, expected to be unique one.");

			// Callback with the first(sole) account.
			this.listener.account(account.value(0));
			break;
		case MessageType.RX_ACTION_ERROR:
			var actError = this.codec.decode(packet.bytes(), RxActionErrorMessage.class);
			for (var error : actError.values())
				this.listener.error(error);

			break;
		case MessageType.RX_COMMISSION:
			var comm = this.codec.decode(packet.bytes(), RxCommissionMessage.class);
			for (var c : comm.values())
				this.listener.commission(c);

			break;
		case MessageType.RX_ERROR:
			var error = this.codec.decode(packet.bytes(), RxErrorMessage.class);
			for (var e : error.values())
				this.listener.error(e);

			break;
		case MessageType.RX_INSTRUMENT:
			var insts = this.codec.decode(packet.bytes(), RxInstrumentMessage.class);
			if (insts.valueCount() > 0) {
				var iter = insts.values().iterator();
				KerInstrument in = iter.next();

				// There is next element after current position, it is not the LAST element.
				while (iter.hasNext()) {
					this.listener.instrument(in, false);
					in = iter.next();
				}
				// If this packet is last of the same query, call method with last set true, false otherwise.
				this.listener.instrument(in, insts.last());
			}
			break;
		case MessageType.RX_MARGIN:
			var margin = this.codec.decode(packet.bytes(), RxMarginMessage.class);
			for (var m : margin.values())
				this.listener.margin(m);

			break;
		case MessageType.RX_ORDER_ERROR:
			var orderError = this.codec.decode(packet.bytes(), RxOrderErrorMessage.class);
			for (var e : orderError.values())
				this.listener.error(e.order(), e.error());

			break;
		case MessageType.RX_ORDER_STATUS:
			var status = this.codec.decode(packet.bytes(), RxOrderStatusMessage.class);
			for (var s : status.values())
				this.listener.orderStatus(s);

			break;
		case MessageType.RX_POSITION_DETAIL:
			var pos = this.codec.decode(packet.bytes(), RxPositionDetailMessage.class);
			if (pos.valueCount() > 0) {
				var iter = pos.values().iterator();
				KerPositionDetail p = iter.next();
				while (iter.hasNext()) {
					this.listener.position(p, false);
					p = iter.next();
				}

				this.listener.position(p, pos.last());
			}
			break;
		case MessageType.RX_TRADE_REPORT:
			var reps = this.codec.decode(packet.bytes(), RxTradeReportMessage.class);
			for (var rep : reps.values())
				this.listener.tradeReport(rep);

			break;
		default:
			this.errorListener.error(new KerError("Unknown message type: " + Short.toString(packet.type())));
			break;
		}
	}
}

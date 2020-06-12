package com.nabiki.corona.portal.core;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.nabiki.corona.object.*;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.packet.api.*;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.CandleMinute;
import com.nabiki.corona.MessageType;
import com.nabiki.corona.portal.inet.PacketServer;

public class MarketDataManager {
	private final PortalServiceContext context;
	private final MarketDataSubscriberListener listener;
	private final DataFactory factory = DefaultDataFactory.create();
	private final DataCodec codec = DefaultDataCodec.create();
	private final Map<String, Set<PacketServer>> map = new ConcurrentHashMap<>();

	// Candle reader and writer.
	private final CandleReader reader;
	private final CandleWriter writer;

	public MarketDataManager(Path root, PortalServiceContext context, MarketDataSubscriberListener listener) {
		this.context = context;
		this.listener = listener;
		this.reader = new CandleReader(root);
		this.writer = new CandleWriter(root);
	}

	public List<KerCandle> historyCandle(String symbol) {
		var r = new LinkedList<KerCandle>();
		// Read candles.
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.MINUTE));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.FIVE_MINUTE));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.QUARTER));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.HALF_HOUR));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.HALF_QUADTER_HOUR));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.HOUR));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.TWO_HOUR));
		} catch (KerError e) {
			this.listener.error(e);
		}
		try {
			r.addAll(this.reader.read(symbol, CandleMinute.DAY));
		} catch (KerError e) {
			this.listener.error(e);
		}

		// Sort candles.
		Collections.sort(r, new Comparator<KerCandle>() {

			@Override
			public int compare(KerCandle o1, KerCandle o2) {
				return o1.updateTime().isBefore(o2.updateTime()) ? -1
						: (o1.updateTime().isAfter(o2.updateTime()) ? 1 : 0);
			}

		});
		return r;
	}

	public void dispatch(KerTick tick) {
		if (tick == null || tick.symbol() == null)
			return;

		for (var server : this.map.get(tick.symbol()))
			try {
				sendTick(tick, server);
			} catch (KerError e) {
				this.listener.error(e, server, this);
			}
	}

	private void sendTick(KerTick tick, PacketServer server) throws KerError {
		var r = this.factory.create(RxTickMessage.class);
		// Set fields.
		r.time(LocalDateTime.now());
		r.last(true);
		r.responseSeq(Utils.increaseGet());
		r.value(tick);
		// Encode and send.
		var bytes = this.codec.encode(r);
		server.send(MessageType.RX_TICK, bytes, 0, bytes.length);
	}

	public void dispatch(KerCandle candle) {
		if (candle == null || candle.symbol() == null)
			return;

		for (var server : this.map.get(candle.symbol()))
			try {
				sendCandle(candle, server);
			} catch (KerError e) {
				this.listener.error(e, server, this);
			}
		// Write candle.
		try {
			this.writer.write(candle);
		} catch (KerError e) {
			this.listener.error(e);
		}
	}

	private void sendCandle(KerCandle candle, PacketServer server) throws KerError {
		var r = this.factory.create(RxCandleMessage.class);
		// Set fields.
		r.time(LocalDateTime.now());
		r.last(true);
		r.responseSeq(Utils.increaseGet());
		r.value(candle);
		// Encode and send.
		var bytes = this.codec.encode(r);
		server.send(MessageType.RX_CANDLE, bytes, 0, bytes.length);
	}

	public KerError subscribe(String symbol, PacketServer server) throws KerError {
		if (symbol == null || server == null)
			return new KerError("Paramter null pointer.");

		// Contains method invokes string's equals that compares the ref first, then byte content if they are strings.
		if (!this.context.info().symbols().contains(symbol))
			return new KerError("Invalid symbol: " + symbol);

		if (map.get(symbol) == null)
			map.put(symbol, new ConcurrentSkipListSet<PacketServer>());

		map.get(symbol).add(server);
		return new KerError(0);
	}

	public boolean unSubscribe(String symbol, PacketServer server) {	
		boolean r = false;
		if (symbol == null || symbol.trim().length() == 0) {
			// Remove all subscriptions under given server.
			for (var entry : this.map.entrySet()) {
				var iter = entry.getValue().iterator();
				while (iter.hasNext()) {
					if (iter.next() == server) {
						iter.remove();
						r = true;
					}
				}
			}
			
			return r;
		} else {
			if (this.map.get(symbol) == null)
				return false;
			else
				return this.map.get(symbol).remove(server);
		}
	}
}

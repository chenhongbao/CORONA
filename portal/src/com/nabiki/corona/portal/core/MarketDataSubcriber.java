package com.nabiki.corona.portal.core;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.nabiki.corona.object.*;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.packet.api.*;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.MessageType;
import com.nabiki.corona.portal.inet.PacketServer;
import com.nabiki.corona.system.info.api.RuntimeInfo;

public class MarketDataSubcriber {
	private final RuntimeInfo runtime;
	private final MarketDataSubscriberListener listener;
	private final DataFactory factory = DefaultDataFactory.create();
	private final DataCodec codec = DefaultDataCodec.create();
	private final Map<String, Set<PacketServer>> map = new ConcurrentHashMap<>();
	
	public MarketDataSubcriber(RuntimeInfo runtime, MarketDataSubscriberListener listener) {
		this.runtime = runtime;
		this.listener = listener;
	}
	
	public void dispatch(KerTick tick) {
		if (tick == null || tick.symbol() == null)
			return;
		
		for (var server : this.map.get(tick.symbol()))
			try {
				sendTick(tick, server);
			} catch (KerError e) {
				this.listener.error(e);
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
	
	public boolean subscribe(String symbol, PacketServer server) {
		if (symbol == null || server == null)
			return false;
		
		// Contains method invokes string's equals that compares the ref first, then byte content if they are strings.
		if (!this.runtime.symbols().contains(symbol))
			return false;
		
		if (map.get(symbol) == null)
			map.put(symbol, new ConcurrentSkipListSet<PacketServer>());
		
		map.get(symbol).add(server);
		return true;
	}
	
	public boolean unSubscribe(String symbol, PacketServer server) {
		if (symbol == null || server == null)
			return false;
		
		if (this.map.get(symbol) == null)
			return false;
		
		return this.map.get(symbol).remove(server);
	}
}

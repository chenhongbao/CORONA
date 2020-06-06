package com.nabiki.corona.system.packet.api;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.system.api.KerError;

public abstract class AbstractPacketMessage<T> implements PacketMessage<T> {
	// Facade of the packet message implementation.
	private int requestSeq, responseSeq;
	private LocalDateTime time;
	private KerError error;
	private boolean last;
	private List<T> values;

	@Override
	public int requestSeq() {
		return this.requestSeq;
	}

	@Override
	public void requestSeq(int seq) {
		this.requestSeq = seq;
	}

	@Override
	public int responseSeq() {
		return this.responseSeq;
	}

	@Override
	public void responseSeq(int seq) {
		this.responseSeq = seq;
	}

	@Override
	public LocalDateTime time() {
		return this.time;
	}

	@Override
	public void time(LocalDateTime time) {
		this.time = time;
	}

	@Override
	public KerError error() {
		return this.error;
	}

	@Override
	public void error(KerError e) {
		this.error = e;
	}

	@Override
	public boolean last() {
		return this.last;
	}

	@Override
	public void last(boolean b) {
		this.last = b;
	}

	@Override
	public Collection<T> values() {
		return this.values;
	}

	@Override
	public void values(Collection<T> values) {
		if (this.values == null)	
			this.values = new LinkedList<>();
		this.values.addAll(values);
	}

	@Override
	public T value(int index) {
		return this.values.get(index);
	}

	@Override
	public void value(T value) {
		if (this.values == null)
			this.values = new LinkedList<>();
		this.values.add(value);
	}

	@Override
	public int valueCount() {
		return this.values.size();
	}

}

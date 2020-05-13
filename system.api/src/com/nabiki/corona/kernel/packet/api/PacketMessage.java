package com.nabiki.corona.kernel.packet.api;

import java.time.LocalDateTime;
import java.util.Collection;

import com.nabiki.corona.kernel.api.KerError;

public interface PacketMessage<T> {
	LocalDateTime time();
	
	void time(LocalDateTime time);
	
	KerError error();
	
	void error(KerError e);
	
	boolean last();
	
	void last(boolean b);
	
	Collection<T> values();
	
	void values(Collection<T> values);
	
	T value(int index);
	
	void value(T value);
	
	int valueCount();
}

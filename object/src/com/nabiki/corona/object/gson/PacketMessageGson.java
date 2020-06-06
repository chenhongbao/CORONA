package com.nabiki.corona.object.gson;

import java.time.LocalDateTime;

import com.nabiki.corona.system.api.KerError;

public class PacketMessageGson {
	public int requestSeq, responseSeq;
	public boolean last;
	public KerError error;
	public LocalDateTime time;
}

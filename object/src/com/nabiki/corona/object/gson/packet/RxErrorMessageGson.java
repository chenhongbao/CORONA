package com.nabiki.corona.object.gson.packet;

import java.util.Collection;
import java.util.LinkedList;

import com.nabiki.corona.system.api.KerError;

public class RxErrorMessageGson extends PacketMessageGson {
	public Collection<KerError> values = new LinkedList<>();
	
	public RxErrorMessageGson() {}
}

package com.nabiki.corona.portal.core;

import com.nabiki.corona.portal.inet.PacketServer;
import com.nabiki.corona.system.api.KerError;

public interface MarketDataSubscriberListener {
	void error(KerError e);
	
	void error(KerError e, PacketServer server, MarketDataManager manager);
}

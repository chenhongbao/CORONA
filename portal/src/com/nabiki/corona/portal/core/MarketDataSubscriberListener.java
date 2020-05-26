package com.nabiki.corona.portal.core;

import com.nabiki.corona.system.api.KerError;

public interface MarketDataSubscriberListener {
	void error(KerError e);
}

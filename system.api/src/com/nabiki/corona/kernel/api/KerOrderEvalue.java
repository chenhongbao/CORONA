package com.nabiki.corona.kernel.api;

import java.util.Collection;

public interface KerOrderEvalue {
	String tradeSessionId();
	
	void tradeSessionId(String s);
	
	KerError error();
	
	void error(KerError e);
	
	void positionToClose(KerPositionDetail p);
	
	void positionsToClose(Collection<KerPositionDetail> p);
	
	Collection<KerPositionDetail> positionToClose();
}

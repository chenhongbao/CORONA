package com.nabiki.corona.system.api;

import java.util.Collection;

public interface KerOrderEvalue {
	KerError error();
	
	void error(KerError e);
	
	void positionToClose(KerPositionDetail p);
	
	void positionsToClose(Collection<KerPositionDetail> p);
	
	Collection<KerPositionDetail> positionToClose();
}

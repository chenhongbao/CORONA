package com.nabiki.corona.kernel.api;

import java.util.Collection;

public interface KerOrderEvalue {
	KerError error();
	
	Collection<KerPositionDetail> positionToClose();
}

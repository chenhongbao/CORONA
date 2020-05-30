package com.nabiki.corona.system.api;

public interface KerOrderError {
	KerOrder order();
	
	void order(KerOrder o);
	
	KerError error();
	
	void error(KerError e);
}

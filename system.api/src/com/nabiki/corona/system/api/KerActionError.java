package com.nabiki.corona.system.api;

public interface KerActionError {
	KerAction action();
	
	void action(KerAction o);
	
	KerError error();
	
	void error(KerError e);
}

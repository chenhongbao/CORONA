package com.nabiki.corona.kernel.conn.api;

import com.nabiki.corona.kernel.api.KerError;

public interface Connection {
	/**
	 * Connect peer with previous profile.
	 */
	void open() throws KerError;
	
	void open(ConnProfile profile) throws KerError;
	
	void close() throws KerError;
	
	void listener(ConnListener listener);
	
	Boolean isClosed();
	
	String name();

}

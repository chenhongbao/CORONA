package com.nabiki.corona.kernel.api;

import java.util.Collection;

/**
 * Simplified request to remote counter. It only provides necessary information like symbols and order ids to cancel.
 * Other fields, like broker id and user id, will be filled by remote server.
 *
 */
public interface KerRemoteRequest {
	void values(Collection<String> vals);
	
	void value(String val);
}

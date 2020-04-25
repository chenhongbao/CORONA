package com.nabiki.corona.kernel.conn.api;

import com.nabiki.corona.kernel.api.KerError;

public interface ConnListener {
	void onOpen(ConnSession w);
	
	void onInputData(byte[] b);
	
	void onError(KerError e);
	
	void onClose();
}

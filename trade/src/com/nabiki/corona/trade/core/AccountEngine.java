package com.nabiki.corona.trade.core;

import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerTradeReport;

public class AccountEngine {

	private final KerAccount origin; // TODO init account
	private final DataFactory factory;

	public AccountEngine(KerAccount init, DataFactory factory) {
		this.factory = factory;
		if (init != null)
			this.origin = init;
		else
			this.origin = this.factory.kerAccount();

		// TODO account engine: compute account upon newly arriving trade
	}


	public void lock(double d) throws KerError {
		// TODO lock amount
	}
	
	public void cancel(String sessionId) {
		// TODO cancel open order
	}

	public void trade(KerTradeReport rep) {
		// TODO complete both open and close order
	}
	
	public KerAccount current() {
		// TODO get current account
		return null;
	}
}

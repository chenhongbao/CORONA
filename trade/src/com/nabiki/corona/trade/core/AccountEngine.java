package com.nabiki.corona.trade.core;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.api.KerTradeReport;

public class AccountEngine {

	
	private KerAccount origin; // TODO init account

	public AccountEngine() {


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
	
	public double available() {
		// TODO get available money
		return 0.0;
	}
}

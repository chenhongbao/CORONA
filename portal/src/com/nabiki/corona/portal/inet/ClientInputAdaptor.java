package com.nabiki.corona.portal.inet;

import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.packet.api.*;

/**
 * Take input and return response.
 * The executor checks the message type to decide the requests, call handler, and send response back.
 */
public abstract class ClientInputAdaptor {
	public void error(KerError e) {}
	
	public RxAccountMessage queryAccount(KerQueryAccount qry) {
		return null;
	}

	public RxPositionDetailMessage queryPositionDetail(KerQueryPositionDetail q) {
		return null;
	}
	
	public RxOrderStatusMessage queryOrderStatus(KerQueryOrderStatus q) {
		return null;
	}
	
	public RxTradeReportMessage queryTradeReport(KerQueryTradeReport q) {
		return null;
	}
	
	public StringMessage queryListSessionId(String accountId) {
		return null;
	}
	
	public StringMessage queryListAccountId() {
		return null;
	}
	
	public RxOrderStatusMessage requestOrder(KerOrder o) {
		return null;
	}
	
	public RxActionErrorMessage requestAction(KerAction a) {
		return null;
	}
	
	public RxErrorMessage subscribeSymbol(String symbol, PacketServer server) {
		return null;
	}
	
	public RxErrorMessage newAccount(KerNewAccount a) {
		return null;
	}
	
	public RxErrorMessage moveCash(CashMove move) {
		return null;
	}
}

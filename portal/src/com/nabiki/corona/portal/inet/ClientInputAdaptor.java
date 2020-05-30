package com.nabiki.corona.portal.inet;

import java.util.Collection;

import com.nabiki.corona.system.api.*;

/**
 * Take input and return response.
 * The executor checks the message type to decide the requests, call handler, and send response back.
 */
public abstract class ClientInputAdaptor {
	public void error(KerError e) {}
	
	public KerAccount queryAccount(KerQueryAccount qry) {
		return null;
	}

	public Collection<KerPositionDetail> queryPositionDetail(KerQueryPositionDetail q) {
		return null;
	}
	
	public Collection<KerOrderStatus> queryOrderStatus(KerQueryOrderStatus q) {
		return null;
	}
	
	public Collection<KerTradeReport> queryTradeReport(KerQueryTradeReport q) {
		return null;
	}
	
	public Collection<String> queryListSessionId(String accountId) {
		return null;
	}
	
	public Collection<String> queryListAccountId() {
		return null;
	}
	
	public KerOrderError requestOrder(KerOrder o) {
		return null;
	}
	
	public KerError requestAction(KerAction a) {
		return null;
	}
	
	public KerError subscribeSymbol(String symbol, PacketServer server) {
		return null;
	}
	
	public KerError newAccount(KerNewAccount a) {
		return null;
	}
	
	public KerError moveCash(CashMove move) {
		return null;
	}
}

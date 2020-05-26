package com.nabiki.corona.portal.core;

import java.util.Collection;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

import com.nabiki.corona.portal.inet.ClientInputAdaptor;
import com.nabiki.corona.system.api.CashMove;
import com.nabiki.corona.system.api.KerAccount;
import com.nabiki.corona.system.api.KerAction;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerNewAccount;
import com.nabiki.corona.system.api.KerOrder;
import com.nabiki.corona.system.api.KerOrderError;
import com.nabiki.corona.system.api.KerOrderStatus;
import com.nabiki.corona.system.api.KerPositionDetail;
import com.nabiki.corona.system.api.KerQueryAccount;
import com.nabiki.corona.system.api.KerQueryOrderStatus;
import com.nabiki.corona.system.api.KerQueryPositionDetail;
import com.nabiki.corona.system.biz.api.TradeLocal;
import com.nabiki.corona.system.biz.api.TradeRemote;

public class AdminServiceAdaptor extends ClientInputAdaptor {
	
	public AdminServiceAdaptor() {}
	
	public void setLocal(TradeLocal local) {
		// TODO set local
	}
	
	public void serRemote(TradeRemote remote) {
		// TODO set remote
	}

	@Override
	public void error(KerError e) {
		// TODO error
		super.error(e);
	}

	@Override
	public KerAccount queryAccount(KerQueryAccount qry) {
		// TODO account
		return super.queryAccount(qry);
	}

	@Override
	public Collection<KerPositionDetail> queryPositionDetail(KerQueryPositionDetail q) {
		// TODO position detail
		return super.queryPositionDetail(q);
	}

	@Override
	public Collection<KerOrderStatus> queryOrderStatus(KerQueryOrderStatus q) {
		// TODO order status
		return super.queryOrderStatus(q);
	}

	@Override
	public Collection<String> queryListSessionId(String accountId) {
		// TODO list session id
		return super.queryListSessionId(accountId);
	}

	@Override
	public Collection<String> queryListAccountId() {
		// TODO list account id
		return super.queryListAccountId();
	}

	@Override
	public KerOrderError requestOrder(KerOrder o) {
		// TODO order
		return super.requestOrder(o);
	}

	@Override
	public KerError requestAction(KerAction a) {
		// TODO action
		return super.requestAction(a);
	}

	@Override
	public KerError newAccount(KerNewAccount a) {
		// TODO new account
		return super.newAccount(a);
	}

	@Override
	public KerError moveCash(CashMove move) {
		// TODO move cash
		return super.moveCash(move);
	}

	@Activate
	public void start(ComponentContext ctx) {
		// TODO start
	}

	@Deactivate
	public void stop(ComponentContext ctx) {
		// TODO stop
	}
}

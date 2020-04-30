package com.nabiki.corona.kernel.biz.api;

import java.util.Collection;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTrade;
import com.nabiki.corona.kernel.api.KerTradeReport;

public interface TradeLocal {
	String name();

	void orderStatus(KerOrderStatus o);

	void tradeReport(KerTradeReport r);

	void positionDetail(KerPositionDetail p);

	/**
	 * Set the global account info. The account is the remote broker account.
	 * @param a account
	 */
	void account(KerAccount a);

	/**
	 * Allocate margin for order.
	 * 
	 * @param op open order
	 * @return allocation report
	 */
	KerOrderEvalue allocateOrder(KerOrder op);

	/**
	 * Query account with given account id.
	 * 
	 * @param id account id
	 * @return account info
	 */
	KerAccount account(String id);

	/**
	 * Query position detail of given symbol under given account.
	 * 
	 * @param id     account id
	 * @param symbol symbol of the query detail
	 * @return collection of position detail
	 */
	Collection<KerPositionDetail> positionDetails(String id, String symbol);

	/**
	 * Query trade session with given reference. The trade returned from the method can be either ongoing or completed.
	 * 
	 * @param id trade session identifier
	 * @return trade session
	 */
	KerTrade trade(String sid);
}

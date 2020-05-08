package com.nabiki.corona.kernel.biz.api;

import java.util.Collection;

import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerOrderEvalue;
import com.nabiki.corona.kernel.api.KerOrder;
import com.nabiki.corona.kernel.api.KerOrderStatus;
import com.nabiki.corona.kernel.api.KerPositionDetail;
import com.nabiki.corona.kernel.api.KerTradeReport;

public interface TradeLocal {
	String name();
	
	// TODO add methods for trade query.

	void orderStatus(KerOrderStatus o);

	void tradeReport(KerTradeReport r);

	void positionDetail(KerPositionDetail p, boolean last);
	
	/**
	 * Perform settlement after the previous trading day.
	 */
	void settle();
	
	/**
	 * Perform initialization for new trading day.
	 */
	void init();

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
	 * @param accountId account id
	 * @return account info
	 */
	KerAccount account(String accountId);
	
	/**
	 * Get remote counter account.
	 * 
	 * @return remote counter account
	 */
	KerAccount remoteAccount();

	/**
	 * Query position detail of given symbol under given account.
	 * 
	 * @param accountId     account id
	 * @param symbol symbol of the query detail
	 * @return collection of position detail
	 */
	Collection<KerPositionDetail> positionDetails(String accountId, String symbol);
	
	/**
	 * Query all position details.
	 * 
	 * @return collection of position details.
	 */
	Collection<KerPositionDetail> remotePositionDetails();
}

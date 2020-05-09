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

	/**
	 * Query latest order status of the given trade session ID.
	 * 
	 * @param sid trade session ID
	 * @return order status
	 */
	KerOrderStatus orderStatus(String sid);

	/**
	 * Query all received trade report of the given trade session ID.
	 * 
	 * @param sid trade session ID.
	 * @return collection of trade reports.
	 */
	Collection<KerTradeReport> tradeReports(String sid);

	/**
	 * Update order status into account.
	 * 
	 * @param status order status
	 */
	void orderStatus(KerOrderStatus status);

	/**
	 * Update trade report into account.
	 * 
	 * @param rep trade report
	 */
	void tradeReport(KerTradeReport rep);

	/**
	 * Update position detail from remote counter. This is total position summarization of all sub accounts.
	 * 
	 * @param p position detail
	 * @param last true if the current input is the last of the series
	 */
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
	 * 
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
	 * Query position detail of given symbol under given account. Account ID can't be null or empty. If symbol is null
	 * or empty string, query all position details under the given account.
	 * 
	 * @param accountId account id
	 * @param symbol    symbol of the query detail
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

package com.nabiki.corona.system.biz.api;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import com.nabiki.corona.system.api.*;

public interface TradeLocal {
	String name();

	/**
	 * Create a valid order ID under given session.
	 * 
	 * @param sessionId session that own the order
	 * @return order ID
	 */
	String createOrder(String sessionId);
	
	/**
	 * Create session ID under account with given ID.
	 * 
	 * @param accountId account ID
	 * @return session ID
	 */
	String createSession(String accountId);

	/**
	 * Get trading day.
	 * 
	 * @return trading day
	 */
	LocalDate tradingDay();

	/**
	 * Get remote login report for the current trading day. Check the field or getter for login status.
	 * 
	 * @return remote login report
	 */
	KerRemoteLoginReport remoteInfo();

	/**
	 * Notifier for remote login.
	 * 
	 * @param rep remote login report
	 */
	void remoteLogin(KerRemoteLoginReport rep);

	/**
	 * Notifier for remote logout.
	 */
	void remoteLogout();

	/**
	 * Get all account IDs.
	 * 
	 * @return collection of all account ids.
	 */
	Collection<String> queryAccounts();

	/**
	 * Get sessions of the given account, both finished and unfinished.
	 * 
	 * @param accountId account ID
	 * @return session ids of the account
	 */
	Collection<String> querySessionsOfAccount(String accountId);

	/**
	 * Query latest order status of the given trade session ID.
	 * 
	 * @param sessionId trade session ID
	 * @return list of order status sorted by update time for early to late
	 */
	List<KerOrderStatus> orderStatus(String sessionId);

	/**
	 * Query all received trade report of the given trade session ID.
	 * 
	 * @param sessionId trade session ID.
	 * @return list of trade reports sorted by update time for early to late
	 */
	List<KerTradeReport> tradeReport(String sessionId);

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
	 * @param p    position detail
	 * @param last true if the current input is the last of the series
	 */
	void positionDetail(KerPositionDetail p, boolean last);

	/**
	 * Allocate margin for order.
	 * 
	 * @param op open order
	 * @return allocation report
	 */
	KerOrderEvalue allocateOrder(KerOrder op);
	
 	/**
-	 * Set the global account info. The account is the remote broker account. The info is used to check the consistency
	 * of all sub-accounts.
-	 * 
-	 * @param a account
-	 */
	void account(KerAccount a);

	/**
	 * Query account with given account id.
	 * 
	 * @param accountId account id
	 * @return account info
	 */
	KerAccount account(String accountId);

	/**
	 * Create a new account with given ID.
	 * 
	 * @param accountId account id
	 */
	void createAccount(String accountId);

	/**
	 * Move cash into/out of an account.
	 * 
	 * @param cmd cash move command
	 */
	void moveCash(CashMove cmd);

	/**
	 * Get remote counter account.
	 * 
	 * @return remote counter account
	 */
	KerAccount remoteAccount();

	/**
	 * Query position detail of given symbol under given account. Account ID can't be null or empty. If symbol is null
	 * or empty string, query all position details under the given account. The position detail is created directly from
	 * trades, so it only concerns the open position and closed position. Frozen position is taken as own position.
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

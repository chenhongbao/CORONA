package com.nabiki.corona.client.api;

import java.time.Instant;
import java.util.Date;

/**
 * Order report to client.
 * 
 * @author Hongbao Chen
 *
 */
public interface OrderStatus {
	String symbol();

	String orderId();
	
	/**
	 * Trade session ID of this order status.
	 * 
	 * @return trade session ID
	 */
	String sessionId();

	char priceType();

	char direction();

	char offsetFlag();

	char hedgeFlag();

	double price();

	int originalVolume();

	char timeCondition();

	Date gtdDate();

	char volumeCondition();

	int minVolume();

	char contigentCondition();

	double stopPrice();

	char orderSubmitStatus();

	Date tradingDay();

	char orderStatus();

	int tradedVolume();

	/**
	 * Volume in queue, not traded yet.
	 * 
	 * @return volume in queue to be traded
	 */
	int waitingVolume();

	Instant insertTime();

	Instant activeTime();

	Instant suspendTime();

	Instant updateTime();

	Instant cancelTime();

	int zceTradedVolume();

	String accountId();
}

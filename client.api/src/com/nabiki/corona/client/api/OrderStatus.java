package com.nabiki.corona.client.api;

import java.time.LocalDate;
import java.time.LocalTime;

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

	LocalDate gtdDate();

	char volumeCondition();

	int minVolume();

	char contigentCondition();

	double stopPrice();

	char orderSubmitStatus();

	LocalDate tradingDay();

	char orderStatus();

	int tradedVolume();

	/**
	 * Volume in queue, not traded yet.
	 * 
	 * @return volume in queue to be traded
	 */
	int waitingVolume();

	LocalTime insertTime();

	LocalTime activeTime();

	LocalTime suspendTime();

	LocalTime updateTime();

	LocalTime cancelTime();

	int zceTradedVolume();

	String accountId();
}

package com.nabiki.corona.api;

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

	int waitingVolume();

	Instant insertTime();

	Instant activeTime();

	Instant suspendTime();

	Instant updateTime();

	Instant cancelTime();

	int zceTradedVolume();

	String accountId();

	int sequenceNo();

	int brokerOrderSequence();
}

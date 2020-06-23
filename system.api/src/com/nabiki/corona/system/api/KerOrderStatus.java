package com.nabiki.corona.system.api;

import java.time.LocalDate;
import java.time.LocalTime;

import com.nabiki.corona.client.api.OrderStatus;

/**
 * Internal used order report with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerOrderStatus extends OrderStatus {
	void symbol(String s);
	
	void sessionId(String sid);

	void orderId(String s);

	void priceType(char t);

	void direction(char t);

	void offsetFlag(char t);

	void hedgeFlag(char t);

	void price(double d);

	void originalVolume(int i);

	void timeCondition(char t);

	void gtdDate(LocalDate d);

	void volumeCondition(char t);

	void minVolume(int i);

	void contigentCondition(char t);

	void stopPrice(double d);

	char forceCloseReason();

	void forceCloseReason(char t);

	boolean isAutoSuspend();

	void isAutoSuspend(boolean b);

	void orderSubmitStatus(char t);

	void tradingDay(LocalDate d);

	char orderSource();

	void orderSource(char t);

	void orderStatus(char t);

	void tradedVolume(int i);

	void waitingVolume(int i);

	void insertTime(LocalTime i);

	void activeTime(LocalTime i);

	void suspendTime(LocalTime i);

	void updateTime(LocalTime i);

	void cancelTime(LocalTime i);

	int remoteFrontId();

	void remoteFrontId(int i);

	int remoteSessionId();

	void remoteSessionId(int i);

	String statusMessage();

	void statusMessage(String s);

	void zceTradedVolume(int i);

	void accountId(String s);

	String currencyId();

	void currencyId(String s);

	String ipAddress();

	void ipAddress(String s);

	String macAddress();

	void macAddress(String s);
}

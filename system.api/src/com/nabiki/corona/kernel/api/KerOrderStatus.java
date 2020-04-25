package com.nabiki.corona.kernel.api;

import java.time.Instant;
import java.util.Date;

import com.nabiki.corona.api.OrderStatus;

/**
 * Internal used order report with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerOrderStatus extends OrderStatus {
	void symbol(String s);
	
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	void orderId(String s);
	
	String userId();
	
	void userId(String s);
	
	void priceType(char t);
	
	void direction(char t);
	
	void offsetFlag(char t);
	
	void hedgeFlag(char t);
	
	void price(double d);
	
	void orginalVolume(int i);
	
	void timeCondition(char t);
	
	void gtdDate(Date d);
	
	void volumeCondition(char t);
	
	void minVolume(int i);
	
	void contigentCondition(char t);
	
	void stopPrice(double d);
	
	char forceCloseReason();
	
	void forceCloseReason(char t);
	
	boolean isAutoSuspend();
	
	void isAutoSuspend(boolean b);
	
	String businessUnit();
	
	void businessUnit(String s);
	
	int requestId();
	
	void requestId(int i);
	
	String orderLocalId();
	
	void orderLocalId(String s);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	String participantId();
	
	void participantId(String s);
	
	String clientId();
	
	void clientId(String s);
	
	String exchangeSymbol();
	
	void exchangeSymbol(String s);
	
	String traderId();
	
	void traderId(String s);
	
	int installId();
	
	void installId(int i);
	
	void orderSubmitStatus(char t);
	
	int notifySequence();
	
	void notifySequence(int i);
	
	void tradingDay(Date d);
	
	int settlementId();
	
	void settlementId(int i);
	
	String orderSysId();
	
	void orderSysId(String s);
	
	char orderSource();
	
	void orderSource(char t);
	
	void orderStatus(char t);
	
	char orderType();
	
	void orderType(char t);
	
	void tradedVolume(int i);
	
	void waitingVolume(int i);
	
	void insertTime(Instant i);
	
	void activeTime(Instant i);
	
	void suspendTime(Instant i);
	
	void updateTime(Instant i);
	
	void cancelTime(Instant i);
	
	String activeTraderId();
	
	void activeTraderId(String s);
	
	String clearingPartId();
	
	void clearingPartId(String s);
	
	void sequenceNo(int i);
	
	int frontId();
	
	void frontId(int i);
	
	int sessionId();
	
	void sessionId(int i);
	
	String userProductInfo();
	
	void userProductInfo(String s);
	
	String statusMessage();
	
	void statusMessage(String s);
	
	boolean userForceClose();
	
	void userForceClose(boolean b);
	
	String activeUserId();
	
	void activeUserId(String s);
	
	void brokerOrderSequence(int i);
	
	String relativeOrderSysId();
	
	void relativeOrderSysId(String s);
	
	void zceTradedVolume(int i);
	
	boolean isSwapOrder();
	
	void isSwapOrder(boolean b);
	
	String branchId();
	
	void branchId(String s);
	
	String investUnitId();
	
	void investUnitId(String s);
	
	void accountId(String s);
	
	String currencyId();
	
	void currencyId(String s);
	
	String ipAddress();
	
	void ipAddress(String s);
	
	String macAddress();
	
	void macAddress(String s);
}

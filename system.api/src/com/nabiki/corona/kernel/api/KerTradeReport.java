package com.nabiki.corona.kernel.api;

import java.time.Instant;
import java.util.Date;

import com.nabiki.corona.api.TradeReport;

/**
 * Internally used trade report with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerTradeReport extends TradeReport {
	String brokerId();
	
	void brokerId(String s);
	
	String investorId();
	
	void investorId(String s);
	
	String symbol();
	
	void symbol(String s);
	
	void orderId(String s);
	
	String userId();
	
	void userId(String s);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	void tradeId(String s);
	
	void direction(char t);
	
	String orderSysId();
	
	void orderSysId(String s);
	
	String participantId();
	
	void participantId(String s);
	
	String clientId();
	
	void clientId(String s);
	
	char tradingRole();
	
	void tradingRole(char t);
	
	String exchangeInstId();
	
	void exchangeInstId(String s);
	
	void offsetFlag(char t);
	
	void hedgeFlag(char t);
	
	void price(double d);
	
	void volume(int i);
	
	void tradeTime(Instant t);
	
	char tradeType();
	
	void tradeType(char t);
	
	char priceSource();
	
	void priceSource(char t);
	
	String traderId();
	
	void traderId(String s);
	
	String orderLocalId();
	
	void orderLocalId(String s);
	
	String clearingPartId();
	
	void clearingPartId(String s);
	
	String businessUnit();
	
	void businessUnit(String s);
	
	void sequenceNo(int i);
	
	void tradingDay(Date d);
	
	String settlementId();
	
	void settlementId(String s);
	
	void brokerOrderSequence(int i);
	
	char tradeSource();
	
	void tradeSource(char t);
	
	String investUnitId();
	
	void investUnitId(String s);
}

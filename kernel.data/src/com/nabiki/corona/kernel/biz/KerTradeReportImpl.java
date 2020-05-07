package com.nabiki.corona.kernel.biz;

import java.time.Instant;
import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.kernel.api.KerTradeReport;

public class KerTradeReportImpl implements KerTradeReport {

	public String brokerId;
	public int brokerOrderSequence;
	public String businessUnit;
	public String clearingPartId;
	public String clientId;
	public char direction;
	public String exchangeId;
	public String exchangeInstId;
	public char hedgeFlag;
	public String investorId;
	public String investUnitId;
	public char offsetFlag;
	public String orderId;
	public String orderLocalId;
	public String orderSysId;
	public String participantId;
	public double price;
	public char priceSource;
	public int sequenceNo;
	public String sessionId;
	public String settlementId;
	public String symbol;
	public String tradeId;
	public String traderId;
	public char tradeSource;
	public Instant tradeTime;
	public char tradeType;
	public char tradingRole;
	public String userId;
	public int volume;
	
	@JsonbDateFormat("yyyyMMdd")
	public LocalDate tradingDay;
	
	@JsonbDateFormat("yyyyMMdd")
	public LocalDate tradeDate;

	public KerTradeReportImpl() {
	}
	
	public KerTradeReportImpl(KerTradeReport rep) {
		deepCopy(rep);
	}
	
	public void deepCopy(KerTradeReport t) {
		brokerId(t.brokerId());
		brokerOrderSequence(t.brokerOrderSequence());
		businessUnit(t.businessUnit());
		clearingPartId(t.clearingPartId());
		clientId(t.clientId());
		direction(t.direction());
		exchangeId(t.exchangeId());
		exchangeInstId(t.exchangeInstId());
		hedgeFlag(t.hedgeFlag());
		investorId(t.investorId());
		investUnitId(t.investUnitId());
		offsetFlag(t.offsetFlag());
		orderId(t.orderId());
		orderLocalId(t.orderLocalId());
		orderSysId(t.orderSysId());
		participantId(t.participantId());
		price(t.price());
		priceSource(t.priceSource());
		sequenceNo(t.sequenceNo());
		sessionId(t.sessionId());
		settlementId(t.settlementId());
		symbol(t.symbol());
		tradeId(t.tradeId());
		traderId(t.traderId());
		tradeSource(t.tradeSource());
		tradeTime(t.tradeTime());
		tradeType(t.tradeType());
		tradingDay(t.tradingDay());
		tradingRole(t.tradingRole());
		userId(t.userId());
		volume(t.volume());
	}

	@Override
	public String brokerId() {
		return this.brokerId;
	}

	@Override
	public void brokerId(String s) {
		this.brokerId = s;
	}

	@Override
	public int brokerOrderSequence() {
		return this.brokerOrderSequence;
	}

	@Override
	public void brokerOrderSequence(int i) {
		this.brokerOrderSequence = i;
	}

	@Override
	public String businessUnit() {
		return this.businessUnit;
	}

	@Override
	public void businessUnit(String s) {
		this.businessUnit = s;
	}

	@Override
	public String clearingPartId() {
		return this.clearingPartId;
	}

	@Override
	public void clearingPartId(String s) {
		this.clearingPartId = s;
	}

	@Override
	public String clientId() {
		return this.clientId;
	}

	@Override
	public void clientId(String s) {
		this.clientId = s;
	}

	@Override
	public char direction() {
		return this.direction;
	}

	@Override
	public void direction(char t) {
		this.direction = t;
	}

	@Override
	public String exchangeId() {
		return this.exchangeId;
	}

	@Override
	public void exchangeId(String s) {
		this.exchangeId = s;
	}

	@Override
	public String exchangeInstId() {
		return this.exchangeInstId;
	}

	@Override
	public void exchangeInstId(String s) {
		this.exchangeInstId = s;
	}

	@Override
	public char hedgeFlag() {
		return this.hedgeFlag;
	}

	@Override
	public void hedgeFlag(char t) {
		this.hedgeFlag = t;
	}

	@Override
	public String investorId() {
		return this.investorId;
	}

	@Override
	public void investorId(String s) {
		this.investorId = s;
	}

	@Override
	public String investUnitId() {
		return this.investUnitId;
	}

	@Override
	public void investUnitId(String s) {
		this.investUnitId = s;
	}

	@Override
	public char offsetFlag() {
		return this.offsetFlag;
	}

	@Override
	public void offsetFlag(char t) {
		this.offsetFlag = t;
	}

	@Override
	public String orderId() {
		return this.orderId;
	}

	@Override
	public void orderId(String s) {
		this.orderId = s;
	}

	@Override
	public String orderLocalId() {
		return this.orderLocalId;
	}

	@Override
	public void orderLocalId(String s) {
		this.orderLocalId = s;
	}

	@Override
	public String orderSysId() {
		return this.orderSysId;
	}

	@Override
	public void orderSysId(String s) {
		this.orderSysId = s;
	}

	@Override
	public String participantId() {
		return this.participantId;
	}

	@Override
	public void participantId(String s) {
		this.participantId = s;
	}

	@Override
	public double price() {
		return this.price;
	}

	@Override
	public void price(double d) {
		this.price = d;
	}

	@Override
	public char priceSource() {
		return this.priceSource;
	}

	@Override
	public void priceSource(char t) {
		this.priceSource = t;
	}

	@Override
	public int sequenceNo() {
		return this.sequenceNo;
	}

	@Override
	public void sequenceNo(int i) {
		this.sequenceNo = i;
	}

	@Override
	public String sessionId() {
		return this.sessionId;
	}

	@Override
	public void sessionId(String s) {
		this.sessionId = s;
	}

	@Override
	public String settlementId() {
		return this.settlementId;
	}

	@Override
	public void settlementId(String s) {
		this.settlementId = s;
	}

	@Override
	public String symbol() {
		return this.symbol;
	}

	@Override
	public void symbol(String s) {
		this.symbol = s;
	}

	@Override
	public String tradeId() {
		return this.tradeId;
	}

	@Override
	public void tradeId(String s) {
		this.tradeId = s;
	}

	@Override
	public String traderId() {
		return this.traderId;
	}

	@Override
	public void traderId(String s) {
		this.traderId = s;
	}

	@Override
	public char tradeSource() {
		return this.tradeSource;
	}

	@Override
	public void tradeSource(char t) {
		this.tradeSource = t;
	}

	@Override
	public Instant tradeTime() {
		return this.tradeTime;
	}

	@Override
	public void tradeTime(Instant t) {
		this.tradeTime = t;
	}

	@Override
	public char tradeType() {
		return this.tradeType;
	}

	@Override
	public void tradeType(char t) {
		this.tradeType = t;
	}

	@Override
	public LocalDate tradingDay() {
		return this.tradingDay;
	}

	@Override
	public void tradingDay(LocalDate d) {
		this.tradingDay = d;		
	}

	@Override
	public char tradingRole() {
		return this.tradingRole;
	}

	@Override
	public void tradingRole(char t) {
		this.tradingRole = t;	
	}

	@Override
	public String userId() {
		return this.userId;
	}

	@Override
	public void userId(String s) {
		this.userId = s;	
	}

	@Override
	public int volume() {
		return this.volume;
	}

	@Override
	public void volume(int i) {
		this.volume = i;
	}

	@Override
	public LocalDate tradeDate() {
		return this.tradeDate;
	}

	@Override
	public void tradeDate(LocalDate date) {
		this.tradeDate = date;
	}

}

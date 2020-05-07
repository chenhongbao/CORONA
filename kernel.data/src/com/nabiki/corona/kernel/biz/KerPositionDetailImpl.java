package com.nabiki.corona.kernel.biz;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.KerPositionDetail;

public class KerPositionDetailImpl implements KerPositionDetail {

	public String brokerId;
	public double closeAmount;
	public double closeCommission;
	public double closeProfitByDate;
	public double closeProfitByTrade;
	public int closeVolume;
	public String combSymbol;
	public char direction;
	public String exchangeId;
	public double exchangeMargin;
	public char hedgeFlag;
	public String investorId;
	public String investUnitId;
	public double lastSettlementPrice;
	public double margin;
	public double marginRateByMoney;
	public double marginRateByVolume;
	public double openCommission;
	@JsonbDateFormat("yyyyMMdd")
	public LocalDate openDate;
	public double openPrice;
	public double positionProfitByDate;
	public double positionProfitByTrade;
	public String settlementId;
	public double settlementPrice;
	public String symbol;
	public int timeFirstVolume;
	public String tradeId;
	public String tradeSessionId;
	public char tradeType;
	
	@JsonbDateFormat("yyyyMMdd")
	public LocalDate tradingDay;
	
	public int volume;
	
	public int volumeMultiple;

	public KerPositionDetailImpl() {
	}
	
	public KerPositionDetailImpl(KerPositionDetail p) {
		deepCopy(p);
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
	public double closeAmount() {
		return this.closeAmount;
	}

	@Override
	public void closeAmount(double d) {
		this.closeAmount = d;
	}

	@Override
	public double closeCommission() {
		return this.closeCommission;
	}

	@Override
	public void closeCommission(double d) {
		this.closeCommission = d;
	}

	@Override
	public double closeProfitByDate() {
		return this.closeProfitByDate;
	}

	@Override
	public void closeProfitByDate(double d) {
		this.closeProfitByDate = d;
	}

	@Override
	public double closeProfitByTrade() {
		return this.closeProfitByTrade;
	}

	@Override
	public void closeProfitByTrade(double d) {
		this.closeProfitByDate = d;
	}

	@Override
	public int closeVolume() {
		return this.closeVolume;
	}

	@Override
	public void closeVolume(int i) {
		this.closeVolume = i;
	}

	@Override
	public String combSymbol() {
		return this.combSymbol;
	}

	@Override
	public void combSymbol(String s) {
		this.combSymbol = s;
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
	public double exchangeMargin() {
		return this.exchangeMargin;
	}

	@Override
	public void exchangeMargin(double d) {
		this.exchangeMargin = d;
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
	public double lastSettlementPrice() {
		return this.lastSettlementPrice;
	}

	@Override
	public void lastSettlementPrice(double d) {
		this.lastSettlementPrice = d;
	}

	@Override
	public double margin() {
		return this.margin;
	}

	@Override
	public void margin(double d) {
		this.margin = d;
	}

	@Override
	public double marginRateByMoney() {
		return this.marginRateByMoney;
	}

	@Override
	public void marginRateByMoney(double d) {
		this.marginRateByMoney = d;
	}

	@Override
	public double marginRateByVolume() {
		return this.marginRateByVolume;
	}

	@Override
	public void marginRateByVolume(double d) {
		this.marginRateByVolume = d;
	}

	@Override
	public double openCommission() {
		return this.openCommission;
	}

	@Override
	public void openCommission(double d) {
		this.openCommission = d;
	}

	@Override
	public LocalDate openDate() {
		return this.openDate;
	}

	@Override
	public void openDate(LocalDate d) {
		this.openDate = d;
	}

	@Override
	public double openPrice() {
		return this.openPrice;
	}

	@Override
	public void openPrice(double d) {
		this.openPrice = d;
	}

	@Override
	public double positionProfitByDate() {
		return this.positionProfitByDate;
	}

	@Override
	public void positionProfitByDate(double d) {
		this.positionProfitByDate = d;
	}

	@Override
	public double positionProfitByTrade() {
		return this.positionProfitByTrade;
	}

	@Override
	public void positionProfitByTrade(double d) {
		this.positionProfitByTrade = d;
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
	public double settlementPrice() {
		return this.settlementPrice;
	}

	@Override
	public void settlementPrice(double d) {
		this.settlementPrice = d;
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
	public int timeFirstVolume() {
		return this.timeFirstVolume;
	}

	@Override
	public void timeFirstVolume(int i) {
		this.timeFirstVolume = i;
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
	public String tradeSessionId() {
		return this.tradeSessionId;
	}

	@Override
	public void tradeSessionId(String s) {
		this.tradeSessionId = s;
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
	public int volume() {
		return this.volume;
	}

	@Override
	public void volume(int i) {
		this.volume = i;
	}

	@Override
	public int volumeMultiple() {
		return this.volumeMultiple;
	}

	@Override
	public void volumeMultiple(int i) {
		this.volumeMultiple = i;
		
	}

	private void deepCopy(KerPositionDetail old) {	
		symbol(old.symbol());
		brokerId(old.brokerId());
		investorId(old.investorId());
		hedgeFlag(old.hedgeFlag());
		direction(old.direction());
		openDate(Utils.deepCopy(old.openDate()));
		tradeId(old.tradeId());
		volume(old.volume());
		openPrice(old.openPrice());
		tradingDay(Utils.deepCopy(old.tradingDay()));
		settlementId(old.settlementId());
		tradeType(old.tradeType());
		combSymbol(old.combSymbol());
		exchangeId(old.exchangeId());
		closeProfitByDate(old.closeProfitByDate());
		closeProfitByTrade(old.closeProfitByTrade());
		positionProfitByDate(old.positionProfitByDate());
		positionProfitByTrade(old.positionProfitByTrade());
		margin(old.margin());
		exchangeMargin(old.exchangeMargin());
		marginRateByMoney(old.marginRateByMoney());
		marginRateByVolume(old.marginRateByVolume());
		lastSettlementPrice(old.lastSettlementPrice());
		settlementPrice(old.settlementPrice());
		closeVolume(old.closeVolume());
		closeAmount(old.closeAmount());
		timeFirstVolume(old.timeFirstVolume());
		investUnitId(old.investUnitId());
	}

}

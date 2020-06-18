package com.nabiki.corona.object.gson;

import java.time.LocalDate;

import com.nabiki.corona.system.api.KerInstrument;

public class KerInstrumentGson implements KerInstrument {
	
	public String symbol;
	public String exchangeId;
	public String exchangeInstId;
	public String productId;
	public char productClass;
	public int deliveryYear;
	public int deliveryMonth;
	public int maxMarketOrderVolume;
	public int minMarketOrderVolume;
	public int maxLimitOrderVolume;
	public int minLimitOrderVolume;
	public int volumeMultiple;
	public double priceTick;
	public LocalDate createDate;
	public LocalDate openDate;
	public LocalDate expireDate;
	public LocalDate startDelivDate;
	public LocalDate endDelivDate;
	public char instLifePhase;
	public boolean isTrading;
	public char positionType;
	public char positionDateType;
	public double longMarginRatio;
	public double shortMarginRatio;
	public String underlyingInstrId;
	public double underlyingMultiple;

	public KerInstrumentGson() {
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
	public String productId() {
		return this.productId;
	}

	@Override
	public void productId(String s) {
		this.productId = s;
	}

	@Override
	public char productClass() {
		return this.productClass;
	}

	@Override
	public void productClass(char c) {
		this.productClass = c;
	}

	@Override
	public int deliveryYear() {
		return this.deliveryYear;
	}

	@Override
	public void deliveryYear(int i) {
		this.deliveryYear = i;
	}

	@Override
	public int deliveryMonth() {
		return this.deliveryMonth;
	}

	@Override
	public void deliveryMonth(int i) {
		this.deliveryMonth = i;
	}

	@Override
	public int maxMarketOrderVolume() {
		return this.maxMarketOrderVolume;
	}

	@Override
	public void maxMarketOrderVolume(int i) {
		this.maxMarketOrderVolume = i;
	}

	@Override
	public int minMarketOrderVolume() {
		return this.minMarketOrderVolume;
	}

	@Override
	public void minMarketOrderVolume(int i) {
		this.minMarketOrderVolume = i;
	}

	@Override
	public int maxLimitOrderVolume() {
		return this.maxLimitOrderVolume;
	}

	@Override
	public void maxLimitOrderVolume(int i) {
		this.maxLimitOrderVolume = i;
	}

	@Override
	public int minLimitOrderVolume() {
		return this.minLimitOrderVolume;
	}

	@Override
	public void minLimitOrderVolume(int i) {
		this.minLimitOrderVolume = i;
	}

	@Override
	public int volumeMultiple() {
		return this.volumeMultiple;
	}

	@Override
	public void volumeMultiple(int i) {
		this.volumeMultiple = i;
	}

	@Override
	public double priceTick() {
		return this.priceTick;
	}

	@Override
	public void priceTick(double d) {
		this.priceTick = d;
	}

	@Override
	public LocalDate createDate() {
		return this.createDate;
	}

	@Override
	public void createDate(LocalDate d) {
		this.createDate = d;
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
	public LocalDate expireDate() {
		return this.expireDate;
	}

	@Override
	public void expireDate(LocalDate d) {
		this.expireDate = d;
	}

	@Override
	public LocalDate startDelivDate() {
		return this.startDelivDate;
	}

	@Override
	public void startDelivDate(LocalDate d) {
		this.startDelivDate = d;
	}

	@Override
	public LocalDate endDelivDate() {
		return this.endDelivDate;
	}

	@Override
	public void endDelivDate(LocalDate d) {
		this.endDelivDate = d;
	}

	@Override
	public char instLifePhase() {
		return this.instLifePhase;
	}

	@Override
	public void instLifePhase(char c) {
		this.instLifePhase = c;
	}

	@Override
	public boolean isTrading() {
		return this.isTrading;
	}

	@Override
	public void isTrading(boolean b) {
		this.isTrading = b;
	}

	@Override
	public char positionType() {
		return this.positionType;
	}

	@Override
	public void positionType(char c) {
		this.positionType = c;
	}

	@Override
	public char positionDateType() {
		return this.positionDateType;
	}

	@Override
	public void positionDateType(char c) {
		this.positionDateType = c;
	}

	@Override
	public double longMarginRatio() {
		return this.longMarginRatio;
	}

	@Override
	public void longMarginRatio(double d) {
		this.longMarginRatio = d;
	}

	@Override
	public double shortMarginRatio() {
		return this.shortMarginRatio;
	}

	@Override
	public void shortMarginRatio(double d) {
		this.shortMarginRatio = d;
	}

	@Override
	public String underlyingInstrId() {
		return this.underlyingInstrId;
	}

	@Override
	public void underlyingInstrId(String s) {
		this.underlyingInstrId = s;
	}

	@Override
	public double underlyingMultiple() {
		return this.underlyingMultiple;
	}

	@Override
	public void underlyingMultiple(double d) {
		this.underlyingMultiple = d;
	}

}

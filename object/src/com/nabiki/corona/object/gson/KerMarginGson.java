package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.KerMargin;

public class KerMarginGson implements KerMargin {
	
	public String symbol;
	public String brokerId;
	public String investorId;
	public char hedgeFlag;
	public double longMarginRatioByMoney;
	public double longMarginRatioByVolume;
	public double shortMarginRatioByMoney;
	public double shortMarginRatioByVolume;
	public boolean isRelative;
	public String exchangeId;

	public KerMarginGson() {
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
	public String brokerId() {
		return this.brokerId;
	}

	@Override
	public void brokerId(String s) {
		this.brokerId = s;
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
	public char hedgeFlag() {
		return this.hedgeFlag;
	}

	@Override
	public void hedgeFlag(char c) {
		this.hedgeFlag = c;
	}

	@Override
	public double longMarginRatioByMoney() {
		return this.longMarginRatioByMoney;
	}

	@Override
	public void longMarginRatioByMoney(double d) {
		this.longMarginRatioByMoney = d;
	}

	@Override
	public double longMarginRatioByVolume() {
		return this.longMarginRatioByVolume;
	}

	@Override
	public void longMarginRatioByVolume(double d) {
		this.longMarginRatioByVolume = d;
	}

	@Override
	public double shortMarginRatioByMoney() {
		return this.shortMarginRatioByMoney;
	}

	@Override
	public void shortMarginRatioByMoney(double d) {
		this.shortMarginRatioByMoney = d;
	}

	@Override
	public double shortMarginRatioByVolume() {
		return this.shortMarginRatioByVolume;
	}

	@Override
	public void shortMarginRatioByVolume(double d) {
		this.shortMarginRatioByVolume = d;
	}

	@Override
	public boolean isRelative() {
		return this.isRelative;
	}

	@Override
	public void isRelative(boolean b) {
		this.isRelative = b;
	}

	@Override
	public String exchangeId() {
		return this.exchangeId;
	}

	@Override
	public void exchangeId(String s) {
		this.exchangeId = s;
	}

}

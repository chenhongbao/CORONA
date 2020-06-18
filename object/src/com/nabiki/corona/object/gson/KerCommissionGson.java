package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.KerCommission;

public class KerCommissionGson implements KerCommission {
	public String symbol;
	public String brokerId;
	public String investorId;
	public double openRatioByMoney;
	public double openRatioByVolume;
	public double closeRatioByMoney;
	public double closeRatioByVolume;
	public double closeTodayRatioByMoney;
	public double closeTodayRatioByVolume;
	public String exchangeId;

	public KerCommissionGson() {
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
	public double openRatioByMoney() {
		return this.openRatioByMoney;
	}

	@Override
	public void openRatioByMoney(double d) {
		this.openRatioByMoney = d;
	}

	@Override
	public double openRatioByVolume() {
		return this.openRatioByVolume;
	}

	@Override
	public void openRatioByVolume(double d) {
		this.openRatioByVolume = d;
	}

	@Override
	public double closeRatioByMoney() {
		return this.closeRatioByMoney;
	}

	@Override
	public void closeRatioByMoney(double d) {
		this.closeRatioByMoney = d;

	}

	@Override
	public double closeRatioByVolume() {
		return this.closeRatioByVolume;
	}

	@Override
	public void closeRatioByVolume(double d) {
		this.closeRatioByVolume = d;
	}

	@Override
	public double closeTodayRatioByMoney() {
		return this.closeTodayRatioByMoney;
	}

	@Override
	public void closeTodayRatioByMoney(double d) {
		this.closeTodayRatioByMoney = d;
	}

	@Override
	public double closeTodayRatioByVolume() {
		return this.closeTodayRatioByVolume;
	}

	@Override
	public void closeTodayRatioByVolume(double d) {
		this.closeTodayRatioByVolume = d;
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

package com.nabiki.corona.object.gson;

import java.time.LocalDate;

import com.nabiki.corona.system.api.KerOrder;

public class KerOrderGson implements KerOrder {
	
	public String symbol;
	public String accountId;
	public double price;
	public double stopPrice;
	public int volume;
	public int minVolume;
	public char direction;
	public char priceType;
	public char offsetFlag;
	public char hedgeFlag;
	public char timeCondition;
	public char volumeCondition;
	public char contigentConditon;
	public String note;
	public String sessionId;
	public String orderId;
	public LocalDate gtdDate;
	public String ipAddress;
	public String macAddress;

	public KerOrderGson() {
	}

	@Override
	public void symbol(String s) {
		this.symbol = s;
	}

	@Override
	public void accountId(String s) {
		this.accountId = s;
	}

	@Override
	public void price(double d) {
		this.price = d;
	}

	@Override
	public double price() {
		return this.price;
	}

	@Override
	public void stopPrice(double d) {
		this.stopPrice = d;
	}

	@Override
	public double stopPrice() {
		return this.stopPrice;
	}

	@Override
	public void volume(int i) {
		this.volume = i;
	}

	@Override
	public int volume() {
		return this.volume;
	}

	@Override
	public void minVolume(int i) {
		this.minVolume = i;
	}

	@Override
	public int minVolume() {
		return this.minVolume;
	}

	@Override
	public void direction(char t) {
		this.direction = t;
	}

	@Override
	public char direction() {
		return this.direction;
	}

	@Override
	public void priceType(char t) {
		this.priceType = t;
	}

	@Override
	public char priceType() {
		return this.priceType;
	}

	@Override
	public void offsetFlag(char t) {
		this.offsetFlag = t;
	}

	@Override
	public char offsetFlag() {
		return this.offsetFlag;
	}

	@Override
	public void hedgeFlag(char t) {
		this.hedgeFlag = t;
	}

	@Override
	public char hedgeFlag() {
		return this.hedgeFlag;
	}

	@Override
	public void timeCondition(char t) {
		this.timeCondition = t;
	}

	@Override
	public char timeCondition() {
		return this.timeCondition;
	}

	@Override
	public void volumeCondition(char t) {
		this.volumeCondition = t;
	}

	@Override
	public char volumeCondition() {
		return this.volumeCondition;
	}

	@Override
	public void contigentConditon(char t) {
		this.contigentConditon = t;
	}

	@Override
	public char contigentConditon() {
		return this.contigentConditon;
	}

	@Override
	public void note(String n) {
		this.note = n;
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
	public String orderId() {
		return this.orderId;
	}

	@Override
	public void orderId(String s) {
		this.orderId = s;
	}

	@Override
	public String accountId() {
		return this.accountId;
	}

	@Override
	public String symbol() {
		return this.symbol;
	}

	@Override
	public LocalDate gtdDate() {
		// TODO Auto-generated method stub
		return this.gtdDate;
	}

	@Override
	public void gtdDate(LocalDate d) {
		this.gtdDate = d;
	}

	@Override
	public String ipAddress() {
		return this.ipAddress;
	}

	@Override
	public void ipAddress(String s) {
		this.ipAddress = s;
	}

	@Override
	public String macAddress() {
		return this.macAddress;
	}

	@Override
	public void macAddress(String s) {
		this.macAddress = s;
	}

	@Override
	public String note() {
		return this.note;
	}

}

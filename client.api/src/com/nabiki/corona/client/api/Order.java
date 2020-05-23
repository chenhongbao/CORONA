package com.nabiki.corona.client.api;

public class Order {
	public String symbol;
	public String sessionId;
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
	public char contigentCondition;
	String note;

	public Order() {
	}
	
	public void sessionId(String s) {
		this.sessionId = s;
	}
	
	public String sessionId() {
		return this.sessionId;
	}

	public void symbol(String s) {
		this.symbol = s;
	}

	public String symbol() {
		return this.symbol;
	}

	public void accountId(String s) {
		this.accountId = s;
	}

	public String accountId() {
		return this.accountId;
	}

	public void price(double d) {
		this.price = d;
	}

	public double price() {
		return this.price;
	}

	public void stopPrice(double d) {
		this.stopPrice = d;
	}

	public double stopPrice() {
		return this.stopPrice;
	}

	public void volume(int i) {
		this.volume = i;
	}

	public int volume() {
		return this.volume;
	}

	public void minVolume(int i) {
		this.minVolume = i;
	}

	public int minVolume() {
		return this.minVolume;
	}

	public void direction(char t) {
		this.direction = t;
	}

	public char direction() {
		return this.direction;
	}

	public void priceType(char t) {
		this.priceType = t;
	}

	public char priceType() {
		return this.priceType;
	}

	public void offsetFlag(char t) {
		this.offsetFlag = t;
	}

	public char offsetFlag() {
		return this.offsetFlag;
	}

	public void hedgeFlag(char t) {
		this.hedgeFlag = t;
	}

	public char hedgeFlag() {
		return this.hedgeFlag;
	}

	public void timeCondition(char t) {
		this.timeCondition = t;
	}

	public char timeCondition() {
		return this.timeCondition;
	}

	public void volumeCondition(char t) {
		this.volumeCondition = t;
	}

	public char volumeCondition() {
		return this.volumeCondition;
	}

	public void contigentConditon(char t) {
		this.contigentCondition = t;
	}

	public char contigentConditon() {
		return this.contigentCondition;
	}

	public void note(String n) {
		this.note = n;
	}

	public String note() {
		return this.note;
	}

}

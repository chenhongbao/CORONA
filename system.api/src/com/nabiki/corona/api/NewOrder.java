package com.nabiki.corona.api;

public class NewOrder {
	String symbol;
	String accountId;
	double price;
	double stopPrice;
	int volume;
	int minVolume;
	char direction;
	char priceType;
	char offsetFlag;
	char hedgeFlag;
	char timeCondition;
	char volumeCondition;
	char contigentCondition;
	String note;

	public NewOrder() {
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

package com.nabiki.corona.object.gson;

import java.time.LocalDate;

import com.nabiki.corona.system.api.KerAccount;

public class KerAccountGson implements KerAccount {
	public String accountId;
	public char accountState;
	public double preDeposit;
	public double preBalance;
	public double preMargin;
	public double interestBase;
	public double interest;
	public double deposit;
	public double withdraw;
	public double frozenMargin;
	public double frozenCash;
	public double frozenCommission;
	public double currentMargin;
	public double commission;
	public double closeProfit;
	public double positionProfit;
	public double balance;
	public double available;
	public double withdrawQuota;
	public double reserve;
	public LocalDate tradingDay;
	public String settlementId;
	public double exchangeMargin;
	public double reserveBalance;
	public String currencyId;
	public String brokerId;
	
	public KerAccountGson() {}

	@Override
	public String accountId() {
		return this.accountId;
	}

	@Override
	public char accountState() {
		return this.accountState;
	}

	@Override
	public double preDeposit() {
		return this.preDeposit;
	}

	@Override
	public double preBalance() {
		return this.preBalance;
	}

	@Override
	public double preMargin() {
		return this.preMargin;
	}

	@Override
	public double interestBase() {
		return this.interestBase;
	}

	@Override
	public double interest() {
		return this.interest;
	}

	@Override
	public double deposit() {
		return this.deposit;
	}

	@Override
	public double withdraw() {
		return this.withdraw;
	}

	@Override
	public double frozenMargin() {
		return this.frozenMargin;
	}

	@Override
	public double frozenCash() {
		return this.frozenCash;
	}

	@Override
	public double frozenCommission() {
		return this.frozenCommission;
	}

	@Override
	public double currentMargin() {
		return this.currentMargin;
	}

	@Override
	public double commission() {
		return this.commission;
	}

	@Override
	public double closeProfit() {
		return this.closeProfit;
	}

	@Override
	public double positionProfit() {
		return this.positionProfit;
	}

	@Override
	public double balance() {
		return this.balance;
	}

	@Override
	public double available() {
		return this.available;
	}

	@Override
	public double withdrawQuota() {
		return this.withdrawQuota;
	}

	@Override
	public double reserve() {
		return this.reserve;
	}

	@Override
	public LocalDate tradingDay() {
		return this.tradingDay;
	}

	@Override
	public String settlementId() {
		return this.settlementId;
	}

	@Override
	public double exchangeMargin() {
		return this.exchangeMargin;
	}

	@Override
	public double reserveBalance() {
		return this.reserveBalance;
	}

	@Override
	public String currencyId() {
		return this.currencyId;
	}

	@Override
	public void accountState(char c) {
		this.accountState = c;
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
	public void accountId(String s) {
		this.accountId = s;
	}

	@Override
	public void preDeposit(double d) {
		this.preDeposit = d;
	}

	@Override
	public void preBalance(double d) {
		this.preBalance = d;
	}

	@Override
	public void preMargin(double d) {
		this.preMargin = d;
	}

	@Override
	public void interestBase(double d) {
		this.interestBase = d;
	}

	@Override
	public void interest(double d) {
		this.interest = d;
	}

	@Override
	public void deposit(double d) {
		this.deposit = d;
	}

	@Override
	public void withdraw(double d) {
		this.withdraw = d;
	}

	@Override
	public void frozenMargin(double d) {
		this.frozenMargin = d;
	}

	@Override
	public void frozenCash(double d) {
		this.frozenCash = d;
	}

	@Override
	public void frozenCommission(double d) {
		this.frozenCommission = d;
	}

	@Override
	public void currentMargin(double d) {
		this.currentMargin = d;
	}

	@Override
	public void commission(double d) {
		this.commission = d;
	}

	@Override
	public void closeProfit(double d) {
		this.closeProfit = d;
	}

	@Override
	public void positionProfit(double d) {
		this.positionProfit = d;
	}

	@Override
	public void balance(double d) {
		this.balance = d;
	}

	@Override
	public void available(double d) {
		this.available = d;
	}

	@Override
	public void withdrawQuota(double d) {
		this.withdrawQuota = d;
	}

	@Override
	public void reserve(double d) {
		this.reserve = d;
	}

	@Override
	public void tradingDay(LocalDate d) {
		this.tradingDay = d;
	}

	@Override
	public void settlementId(String s) {
		this.settlementId = s;
	}

	@Override
	public void exchangeMargin(double d) {
		this.exchangeMargin = d;
	}

	@Override
	public void reserveBalance(double d) {
		this.reserveBalance = d;
	}

	@Override
	public void currencyId(String s) {
		this.currencyId = s;
	}

}

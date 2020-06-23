package com.nabiki.corona.system.api;

import java.time.LocalDate;

import com.nabiki.corona.client.api.Account;

/**
 * Internally used account with full getters.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerAccount extends Account {
	void accountState(char c);

	String brokerId();

	void brokerId(String s);

	void accountId(String s);

	void preDeposit(double d);

	void preBalance(double d);

	void preMargin(double d);

	void interestBase(double d);

	void interest(double d);

	void deposit(double d);

	void withdraw(double d);

	void frozenMargin(double d);

	void frozenCash(double d);

	void frozenCommission(double d);

	void currentMargin(double d);

	void commission(double d);

	void closeProfit(double d);

	void positionProfit(double d);

	void balance(double d);

	void available(double d);

	void withdrawQuota(double d);

	void reserve(double d);

	void tradingDay(LocalDate d);

	void settlementId(int s);

	void exchangeMargin(double d);

	void reserveBalance(double d);

	void currencyId(String s);
}

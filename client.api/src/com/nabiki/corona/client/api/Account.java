package com.nabiki.corona.client.api;

import java.time.LocalDate;

/**
 * Client's account.
 * 
 * @author Hongbao Chen
 *
 */
public interface Account {
	String accountId();

	/**
	 * Account char, could be locked open, locked close, locked all, or others.
	 * 
	 * @return account char
	 */
	char accountState();

	double preDeposit();

	double preBalance();

	double preMargin();

	double interestBase();

	double interest();

	double deposit();

	double withdraw();

	double frozenMargin();

	double frozenCash();

	double frozenCommission();

	double currentMargin();

	double commission();

	double closeProfit();

	double positionProfit();

	double balance();

	double available();

	double withdrawQuota();

	double reserve();

	LocalDate tradingDay();

	int settlementId();

	double exchangeMargin();

	double reserveBalance();

	String currencyId();
}

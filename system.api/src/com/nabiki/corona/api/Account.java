package com.nabiki.corona.api;

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

	double preMortgage();

	double preCredit();

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

	double cashIn();

	double commission();

	double closeProfit();

	double positionProfit();

	double balance();

	double available();

	double withdrawQuota();

	double reserve();

	LocalDate tradingDay();

	String settlementId();

	double credit();

	double mortgage();

	double exchangeMargin();

	double deliveryMargin();

	double exchangeDeliveryMargin();

	double reserveBalance();

	String currencyId();

	double preFundMortgageIn();

	double preFundMortgageOut();

	double fundMortgageIn();

	double fundMortgageOut();

	double fundMortgageAvailable();

	double mortgageableFund();

	double specProductMagin();

	double specProductFrozenMargin();

	double specProductCommission();

	double specProductFrozenCommission();

	double specProductPositionProfit();

	double specProductCloseProfit();

	double specProductPositionProfitByAlg();

	double specProductExchangeMargin();

	char bizType();

	double frozenSwap();

	double remainSwap();
}

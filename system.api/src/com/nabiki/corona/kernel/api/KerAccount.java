package com.nabiki.corona.kernel.api;

import java.time.LocalDate;

import com.nabiki.corona.api.Account;

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

	void preMortgage(double d);

	void preCredit(double d);

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

	void cashIn(double d);

	void commission(double d);

	void closeProfit(double d);

	void positionProfit(double d);

	void balance(double d);

	void available(double d);

	void withdrawQuota(double d);

	void reserve(double d);

	void tradingDay(LocalDate d);

	void settlementId(String s);

	void credit(double d);

	void mortgage(double d);

	void exchangeMargin(double d);

	void deliveryMargin(double d);

	void exchangeDeliveryMargin(double d);

	void reserveBalanec(double d);

	void currencyId(double d);

	void preFundMortgageIn(double d);

	void preFundMortgageOut(double d);

	void fundMortgageIn(double d);

	void fundMortgageOut(double d);

	void fundMortgageAvailable(double d);

	void mortgageableFund(double d);

	void specProductMargin(double d);

	void specProductFrozenMargin(double d);

	void specProductCommission(double d);

	void specProductFrozenCommission(double d);

	void specProductPositionProfit(double d);

	void specProductCloseProfit(double d);

	void specProductPositionProfitByAlg(double d);

	void specProductExchangeMargin(double d);

	void bizType(char t);

	void frozenSwap(double d);

	void remainSwap(double d);
}

package com.nabiki.corona.system.api;

import java.util.Date;

public interface KerInstrument {
	String symbol();
	
	void symbol(String s);
	
	String exchangeId();
	
	void exchangeId(String s);
	
	String instrumentName();
	
	void instrumentName(String s);
	
	String exchangeInstId();
	
	void exchangeInstId(String s);
	
	String productId();
	
	void productId(String s);
	
	char productClass();
	
	void productClass(char c);
	
	int deliveryYear();
	
	void deliveryYear(int i);
	
	int deliveryMonth();
	
	void deliveryMonth(int i);
	
	int maxMarketOrderVolume();
	
	void maxMarketOrderVolume(int i);
	
	int minMarketOrderVolume();
	
	void minMarketOrderVolume(int i);
	
	int maxLimitOrderVolume();
	
	void maxLimitOrderVolume(int i);
	
	int minLimitOrderVolume();
	
	void minLimitOrderVolume(int i);
	
	int volumeMultiple();
	
	void volumeMultiple(int i);
	
	double priceTick();
	
	void priceTick(double d);
	
	Date createDate();
	
	void createDate(Date d);
	
	Date openDate();
	
	void openDate(Date d);
	
	Date expireDate();
	
	void expireDate(Date d);
	
	Date startDelivDate();
	
	void startDelivDate(Date d);
	
	Date endDelivDate();
	
	void endDelivDate(Date d);
	
	char instLifePhase();
	
	void instLifePhase(char c);
	
	boolean isTrading();
	
	void isTrading(boolean b);
	
	char positionType();
	
	void positionType(char c);
	
	char positionDateType();
	
	void positionDateType(char c);
	
	double longMarginRatio();
	
	void longMarginRatio(double d);
	
	double shortMarginRatio();
	
	void shortMarginRatio(double d);
	
	char maxMarginSideAlgorithm();
	
	void maxMarginSideAlgorithm(char c);
	
	String underlyingInstrId();
	
	void underlyingInstrId(String s);
	
	double strikePrice();
	
	void strikePrice(double d);
	
	char optionsType();
	
	void opetionsType(char c);
	
	double underlyingMultiple();
	
	void underlyingMultiple(double d);
	
	char combinationType();
	
	void combinationType(char c);
}

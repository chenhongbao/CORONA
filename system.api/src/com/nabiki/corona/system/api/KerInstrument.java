package com.nabiki.corona.system.api;

import java.time.LocalDate;

public interface KerInstrument {
	String symbol();
	
	void symbol(String s);
	
	String exchangeId();
	
	void exchangeId(String s);
	
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
	
	LocalDate createDate();
	
	void createDate(LocalDate d);
	
	LocalDate openDate();
	
	void openDate(LocalDate d);
	
	LocalDate expireDate();
	
	void expireDate(LocalDate d);
	
	LocalDate startDelivDate();
	
	void startDelivDate(LocalDate d);
	
	LocalDate endDelivDate();
	
	void endDelivDate(LocalDate d);
	
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
	
	String underlyingInstrId();
	
	void underlyingInstrId(String s);
	
	double underlyingMultiple();
	
	void underlyingMultiple(double d);
}

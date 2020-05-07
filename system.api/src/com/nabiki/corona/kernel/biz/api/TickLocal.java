package com.nabiki.corona.kernel.biz.api;

import java.time.LocalDate;

import com.nabiki.corona.kernel.api.KerTick;

public interface TickLocal {
	String name();
	
	LocalDate tradingDay();

	void isWorking(boolean working);

	void tick(KerTick tick);
	
	KerTick last(String symbol);
}

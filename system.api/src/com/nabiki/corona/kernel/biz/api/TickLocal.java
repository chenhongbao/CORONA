package com.nabiki.corona.kernel.biz.api;

import java.time.LocalDate;

import com.nabiki.corona.api.Tick;

public interface TickLocal {
	String name();
	
	LocalDate tradingDay();

	void state(boolean working);

	void tick(Tick tick);
	
	Tick last(String symbol);
}

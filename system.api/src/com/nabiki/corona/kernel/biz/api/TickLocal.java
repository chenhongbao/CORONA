package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.Tick;

public interface TickLocal {
	String name();

	void state(boolean working);

	void tick(Tick tick);
	
	Tick last(String symbol);
}

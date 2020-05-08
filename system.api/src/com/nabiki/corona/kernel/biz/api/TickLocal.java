package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.kernel.api.KerTick;

public interface TickLocal {
	String name();

	void isWorking(boolean working);

	void tick(KerTick tick);
}

package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.kernel.api.KerTick;

public interface TickLocal {
	/**
	 * Name of tick local service.
	 * 
	 * @return name of service
	 */
	String name();

	/**
	 * Set the working state of the remote tick server. When the market closes, it is set false.
	 * 
	 * @param working working state
	 */
	void isWorking(boolean working);

	/**
	 * Set the latest tick into the service.
	 * 
	 * @param tick latest tick
	 */
	void tick(KerTick tick);
}

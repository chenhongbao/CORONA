package com.nabiki.corona.system.biz.api;

import com.nabiki.corona.system.api.KerTick;

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
	void marketWorking(boolean working);

	/**
	 * Set the latest tick into the service.
	 * 
	 * @param tick latest tick
	 */
	void tick(KerTick tick);
}

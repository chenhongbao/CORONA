package com.nabiki.corona.kernel.settings.api;

import java.time.Instant;

/**
 * Market open time interface. Please note that the data behind the interface could change without notice. So
 * don't extract the data until you really need them and update(extract again) at next use.
 * 
 * @author Hongbao Chen
 *
 */
public interface MarketTimeQuery {
	String name();

	boolean open(Instant now);

	boolean closed(Instant now);

	/**
	 * Return true if now is after market close of the day and before market open of the next day.
	 * 
	 * @param now current instant
	 * @return true if now is the end of the current trading day.
	 */
	boolean endOfDay(Instant now);
}

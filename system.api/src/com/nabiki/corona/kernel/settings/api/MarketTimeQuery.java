package com.nabiki.corona.kernel.settings.api;

import java.time.Instant;

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

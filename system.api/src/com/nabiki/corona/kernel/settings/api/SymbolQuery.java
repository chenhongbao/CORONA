package com.nabiki.corona.kernel.settings.api;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Symbol setting obtaining interface. Please note that the data behind the interface could change without notice. So
 * don't extract the data until you really need them and update(extract again) at next use.
 * 
 * @author Hongbao Chen
 *
 */
public interface SymbolQuery {
	String name();

	boolean candleNow(String symbol, int min, Instant now, int margin, TimeUnit marginUnit);

	Collection<String> symbols();
}

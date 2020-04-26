package com.nabiki.corona.kernel.settings.api;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface SymbolQuery {
	String name();

	boolean candleNow(String symbol, int min, Instant now, int margin, TimeUnit marginUnit);

	Collection<String> symbols();
}

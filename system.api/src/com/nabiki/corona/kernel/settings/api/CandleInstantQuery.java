package com.nabiki.corona.kernel.settings.api;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public interface CandleInstantQuery {
	String name();
	
	boolean now(String symbol, int min, Instant now, int margin, TimeUnit marginUnit);
}

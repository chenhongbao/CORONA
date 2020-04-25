package com.nabiki.corona.kernel.biz.api;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public interface CandleInstantQuery {
	String name();
	
	boolean isNow(String symbol, int min, Instant now, int margin, TimeUnit marginUnit);
}

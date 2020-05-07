package com.nabiki.corona.kernel.settings.api;

import java.util.List;

public interface ProductTradingTime {
	List<String> products();
	
	List<TimeRange> times();
}

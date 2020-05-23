package com.nabiki.corona.system.info.api;

import java.util.List;

public interface ProductTradingTime {
	List<String> products();
	
	List<TimeRange> times();
}

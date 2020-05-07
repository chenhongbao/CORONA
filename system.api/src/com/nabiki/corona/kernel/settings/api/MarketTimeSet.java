package com.nabiki.corona.kernel.settings.api;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketTimeSet {
	LocalDateTime updateTime();
	
	List<TimeRange> marketTimes();
}

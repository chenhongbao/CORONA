package com.nabiki.corona.system.info.api;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketTimeSet {
	LocalDateTime updateTime();
	
	List<TimeRange> marketTimes();
}

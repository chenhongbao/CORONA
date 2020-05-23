package com.nabiki.corona.system.info.api;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductTradingTimeSet {
	LocalDateTime updateTime();
	
	List<ProductTradingTime> productTimes();
}

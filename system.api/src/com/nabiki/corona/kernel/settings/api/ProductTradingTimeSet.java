package com.nabiki.corona.kernel.settings.api;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductTradingTimeSet {
	LocalDateTime updateTime();
	
	List<ProductTradingTime> productTimes();
}

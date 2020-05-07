package com.nabiki.corona.kernel.settings;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

public class ProductTradingTimeSet {
	public class ProductTradingTime {
		public List<String> products = new LinkedList<>();
		public List<TimeRange> times = new LinkedList<>();
		
		public ProductTradingTime() {}
	}
	
	@JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updateTime;
	public List<ProductTradingTime> productTimes = new LinkedList<>();
	
	public ProductTradingTimeSet() {}
}

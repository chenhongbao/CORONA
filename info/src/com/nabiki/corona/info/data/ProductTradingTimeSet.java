package com.nabiki.corona.info.data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

public class ProductTradingTimeSet {
	public class TradingTime {
		public int rank;
		@JsonbDateFormat("HH:mm:ss")
		public LocalTime from;
		@JsonbDateFormat("HH:mm:ss")
		public LocalTime to;
		
		public TradingTime() {}
	}
	
	public class ProductTradingTime {
		public List<String> products = new LinkedList<>();
		public List<TradingTime> times = new LinkedList<>();
		
		public ProductTradingTime() {}
	}
	
	@JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updateTime;
	public List<ProductTradingTime> productTimes = new LinkedList<>();
	
	public ProductTradingTimeSet() {}
}

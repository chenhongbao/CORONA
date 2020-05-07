package com.nabiki.corona.kernel.settings;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.kernel.settings.api.ProductTradingTime;
import com.nabiki.corona.kernel.settings.api.ProductTradingTimeSet;

public class ProductTradingTimeSetImpl implements ProductTradingTimeSet{
	@JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updateTime;
	public List<ProductTradingTime> productTimes = new LinkedList<>();
	
	public ProductTradingTimeSetImpl() {}

	@Override
	public LocalDateTime updateTime() {
		return this.updateTime;
	}

	@Override
	public List<ProductTradingTime> productTimes() {
		return this.productTimes;
	}
}

package com.nabiki.corona.kernel.settings;

import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.kernel.settings.api.ProductTradingTime;
import com.nabiki.corona.kernel.settings.api.TimeRange;

public class ProductTradingTimeImpl implements ProductTradingTime{
	public List<String> products = new LinkedList<>();
	public List<TimeRange> times = new LinkedList<>();
	
	public ProductTradingTimeImpl() {}

	@Override
	public List<String> products() {
		return this.products;
	}

	@Override
	public List<TimeRange> times() {
		return this.times;
	}
}

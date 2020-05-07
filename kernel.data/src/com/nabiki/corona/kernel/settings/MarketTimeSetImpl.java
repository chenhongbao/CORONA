package com.nabiki.corona.kernel.settings;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.kernel.settings.api.MarketTimeSet;
import com.nabiki.corona.kernel.settings.api.TimeRange;

public class MarketTimeSetImpl implements MarketTimeSet {
	@JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updateTime;
	public List<TimeRange> marketTimes = new LinkedList<>();
	
	public MarketTimeSetImpl() {}

	@Override
	public LocalDateTime updateTime() {
		return this.updateTime;
	}

	@Override
	public List<TimeRange> marketTimes() {
		return this.marketTimes;
	}
}

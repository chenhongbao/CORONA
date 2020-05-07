package com.nabiki.corona.kernel.settings;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

public class MarketTimeSet {
	@JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updateTime;
	public List<TimeRange> marketTimes = new LinkedList<>();
	
	public MarketTimeSet() {}
}

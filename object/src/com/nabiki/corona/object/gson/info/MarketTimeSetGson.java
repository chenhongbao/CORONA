package com.nabiki.corona.object.gson.info;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.system.info.api.MarketTimeSet;
import com.nabiki.corona.system.info.api.TimeRange;

public class MarketTimeSetGson implements MarketTimeSet {
	public LocalDateTime updateTime;
	public List<TimeRangeGson> marketTimes;
	
	public MarketTimeSetGson() {}

	@Override
	public LocalDateTime updateTime() {
		return this.updateTime;
	}

	@Override
	public List<TimeRange> marketTimes() {
		var r = new LinkedList<TimeRange>();
		for (var c : this.marketTimes)
			r.add(c);
		
		return r;
	}
}

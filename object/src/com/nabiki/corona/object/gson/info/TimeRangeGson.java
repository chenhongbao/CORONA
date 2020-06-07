package com.nabiki.corona.object.gson.info;

import java.time.LocalTime;

import com.nabiki.corona.system.info.api.TimeRange;

public class TimeRangeGson implements TimeRange {
	public int rank;
	public LocalTime from, to;
	
	public TimeRangeGson() {}
	
	@Override
	public int rank() {
		return this.rank;
	}
	@Override
	public LocalTime from() {
		return this.from;
	}
	@Override
	public LocalTime to() {
		return this.to;
	}
}

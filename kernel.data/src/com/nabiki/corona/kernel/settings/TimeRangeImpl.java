package com.nabiki.corona.kernel.settings;

import java.time.LocalTime;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.kernel.settings.api.TimeRange;

public class TimeRangeImpl implements TimeRange{
	// The smallest rank denotes the first trading period of the trading day.
	public int rank;
	@JsonbDateFormat("HH:mm:ss")
	public LocalTime from;
	@JsonbDateFormat("HH:mm:ss")
	public LocalTime to;
	
	public TimeRangeImpl() {}

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

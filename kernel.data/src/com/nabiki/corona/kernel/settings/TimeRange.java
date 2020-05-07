package com.nabiki.corona.kernel.settings;

import java.time.LocalTime;

import javax.json.bind.annotation.JsonbDateFormat;

public class TimeRange {
	// The smallest rank denotes the first trading period of the trading day.
	public int rank;
	@JsonbDateFormat("HH:mm:ss")
	public LocalTime from;
	@JsonbDateFormat("HH:mm:ss")
	public LocalTime to;
	
	public TimeRange() {}
}

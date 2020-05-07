package com.nabiki.corona.info.data;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.nabiki.corona.kernel.settings.TimeRange;

public class InstantList {
	public InstantList(List<TimeRange> times) {
		// TODO instant list
	}
	
	public boolean hit(int minPeriod, Instant now, int margin, TimeUnit mUnit) {
		// TODO hit
		return false;
	}
	
	public Instant firstInstant(int minPeriod) {
		// TODO first instant
		return null;
	}
	
	public Instant lastInstant(int minPeriod) {
		// TODO last instant
		return null;
	}
}

package com.nabiki.corona.info.data;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.client.api.CandleMinute;
import com.nabiki.corona.system.info.api.TimeRange;

public class TimeList {
	private static int[] periods = new int[] { CandleMinute.MINUTE, CandleMinute.FIVE_MINUTE, CandleMinute.QUARTER,
			CandleMinute.HALF_HOUR, CandleMinute.HALF_QUADTER_HOUR, CandleMinute.HOUR, CandleMinute.TWO_HOUR};
	
	// Instants are stored in default order for fast look up.
	// Time points are kept in minutes of day.
	private final Map<Integer, Set<Integer>> instants = new ConcurrentHashMap<>();
	
	// Local times are stored in the order of candle generation.
	private final Map<Integer, List<LocalTime>> orderedInstants = new ConcurrentHashMap<>();
	
	public TimeList(List<TimeRange> times) {
		calculateOrderedInstant(times);
	}
	
	private void calculateOrderedInstant(List<TimeRange> given) {
		var times = new LinkedList<TimeRange>(given);
		
		Collections.sort(times, new Comparator<TimeRange>() {
			@Override
			public int compare(TimeRange o1, TimeRange o2) {
				return Integer.compare(o1.rank(), o2.rank());
			}
		});
		
		// We get one minute times.
		calculateOneMinute(times);
		
		// Calculate other minute candles based on one minute.
		for (var m : TimeList.periods) {
			if (m == CandleMinute.MINUTE)
				continue;
			
			calculateTimeMinute(m);
		}
	}
	
	private void calculateTimeMinute(int minute) {
		var times = new LinkedList<LocalTime>();
		var minutes = new HashSet<Integer>();
		
		var one = this.orderedInstants.get(1);
		
		int count = 0;
		
		// Get minute candle at very minute number of one-minute candle.
		count += minute;
		while (count < one.size()) {
			// Get local time.
			times.add(one.get(count -1 ));
			// Get minute of day.
			int minOfDay = one.get(count -1).toSecondOfDay() / 60;
			minutes.add(minOfDay);
			// Update counter.
			count += minute;
		}
		
		times.add(one.get(one.size() - 1));
		int minOfDay = one.get(one.size() - 1).toSecondOfDay() / 60;
		minutes.add(minOfDay);
		
		// Save minutes.
		this.orderedInstants.put(minute, times);
		this.instants.put(minute, minutes);
	}
	
	private void calculateOneMinute(List<TimeRange> given) {
		var times = new LinkedList<LocalTime>();
		var minutes = new HashSet<Integer>();
		
		var iter = given.iterator();
		while (iter.hasNext()) {
			var range = iter.next();
			
			var cur = range.from();
			while (cur.isBefore(range.to())) {
				cur = cur.plusMinutes(CandleMinute.MINUTE);
				if (cur.isAfter(range.to())) {
					times.add(range.to());
				} else {
					times.add(cur);
				}
			}
		}
		
		this.orderedInstants.put(1, times);
		this.instants.put(1, minutes);
	}
	
	// After times' info is loaded, it won't change. So concurrent visiting will have no problem.
	public boolean hit(int minPeriod, Instant now) {
		var list = this.instants.get(minPeriod);
		if (list == null)
			return false;
		
		int minuteOfDay = LocalTime.ofInstant(now, ZoneId.systemDefault()).toSecondOfDay() / 60;
		return list.contains(minuteOfDay);
	}
	
	// No need to add more thread-safe because it won't change after loaded.
	public LocalTime firstOfDay(int minPeriod) {
		var list = this.orderedInstants.get(minPeriod);
		if (list == null)
			return null;
		
		return list.get(0);
	}
	
	// No need to add more thread-safe because it won't change after loaded.
	public LocalTime lastOfDay(int minPeriod) {
		var list = this.orderedInstants.get(minPeriod);
		if (list == null)
			return null;
		
		return list.get(list.size() - 1);
	}
}

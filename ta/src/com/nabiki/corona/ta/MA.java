package com.nabiki.corona.ta;

import java.util.Collection;

import com.nabiki.corona.ta.utils.Entry;

public class MA extends Series<Entry<Double>> {
	private static final long serialVersionUID = 1L;
	private final int track;
	private final Series<Double> underlying = new Series<>();
	
	public MA(int track) {
		super();
		this.track = track;
	}
	
	public MA(int track, Collection<Entry<Double>> toCopy) {
		super(toCopy);
		this.track = track;
	}

	public boolean add(double e) {
		this.underlying.add(e);
		final double[] total = {0};
		var realTrack = Math.min(this.track, this.underlying.size());
		// Calculate sum, then get the average.
		this.underlying.visit(value -> total[0] += value, realTrack);
		return super.add(new Entry<Double>(this.underlying.size() - 1, total[0] / realTrack));
	}
	
	
}

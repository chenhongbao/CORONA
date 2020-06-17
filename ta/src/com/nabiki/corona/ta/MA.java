package com.nabiki.corona.ta;

import java.util.Collection;

import com.nabiki.corona.ta.utils.Entry;

/**
 * Moving average computed with following equations:
 * <p>Supposing {@code n} is the range of averaging. If the size of Series is less than {@code n}:
 * <p>{@code ma = sum of all elements / size}
 * <p>If the size of Series is over {@code n}:
 * <p>{@code ma = sum of latest n elements / n}
 * 
 * @author Hongbao Chen
 *
 */
public class MA extends Series<Entry<Double>> {
	private static final long serialVersionUID = 1L;
	private final int n;
	private final Series<Double> underlying = new Series<>();
	
	/**
	 * Construct an empty MA series with range {@code n}.
	 * 
	 * @param n averaging range
	 */
	public MA(int n) {
		super();
		this.n = n;
	}
	
	/**
	 * Construct am MA series containing elements in order returned by collection's iterator, and its averaging range is
	 * set to {@code n}.
	 * 
	 * @param n averaging range
	 * @param toCopy the collection whose elements are to be placed into this Series
	 */
	public MA(int n, Collection<Entry<Double>> toCopy) {
		super(toCopy);
		this.n = n;
	}

	/**
	 * Process the element to get the moving average value.
	 * 
	 * @param e element to be processed
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public boolean add(double e) {
		this.underlying.add(e);
		final double[] total = {0};
		var realTrack = Math.min(this.n, this.underlying.size());
		// Calculate sum, then get the average.
		this.underlying.visit(value -> total[0] += value, realTrack);
		return super.add(new Entry<Double>(super.size(), total[0] / realTrack));
	}
	
	
}

package com.nabiki.corona.ta;

import java.util.Collection;

import com.nabiki.corona.ta.utils.Entry;

/**
 * SMA indicator computed with the following equations.
 * <p>Supposing {@code n} is the averaging range and {@code weight} is the weight of newly added element.
 * <p>{@code sma = (element * weight + sma' * (n - weight)) / n}
 * 
 * @author Hongbao Chen
 *
 */
public class SMA extends Series<Entry<Double>>{
	private static final long serialVersionUID = 6503541250241959182L;
	private final int n, weight;

	/**
	 * Construct an empty SMA series of range {@code n} and {@code weight}.
	 * 
	 * @param n averaging range
	 * @param weight weight of the newly added element
	 */
	public SMA(int n, int weight) {
		super();
		this.n = n;
		this.weight = weight;
	}
	
	/**
	 * Construct an SMA series containing the elements of specified collection in order returned by its iterator. And
	 * averaging range is {@code n} and weight is {@code weight}.
	 * 
	 * @param n averaging range
	 * @param weight weight of the newly added element
	 * @param toCopy the collection whose elements are to be placed into this Series
	 */
	public SMA(int n, int weight, Collection<Entry<Double>> toCopy) {
		super.addAll(toCopy);
		this.n = n;
		this.weight = weight;
	}
	
	/**
	 * Process the element to get the SMA value.
	 * 
	 * @param e element to be processed
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public boolean add(double e) {
		double y = 0;
		if (super.size() == 0)
			y = e;
		else
			y = (e * this.weight + super.ref(0).value() * (this.n - this.weight)) / n;
		return super.add(new Entry<Double>(super.size(), y));
	}
}

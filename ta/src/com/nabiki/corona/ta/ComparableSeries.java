package com.nabiki.corona.ta;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ComparableSeries<E> extends Series<E> {
	private static final long serialVersionUID = 1L;
	private Comparator<E> comparator;
	
	/**
	 * Construct an empty arithmetic series containing the specified elements.
	 * <p>The generic type must implements {@code Comparable}.
	 */
	public ComparableSeries() {
		super();
	}
	
	/**
	 * Construct an arithmetic series containing the specified elements, in order returned by iterator.
	 * <p>The generic type must implements {@code Comparable}.
	 * 
	 * @param toCopy the collection whose elements are to be placed into this Series
	 */
	public ComparableSeries(Collection<E> toCopy) {
		super(toCopy);
	}

	/**
	 * Construct an empty arithmetic series, whose elements are compared with the specified comparator.
	 * 
	 * @param comparator the comparator used to compared the elements
	 */
	public ComparableSeries(Comparator<E> comparator) {
		super();
		this.comparator = comparator;
	}
	
	/**
	 * Construct an arithmetic series containing the specified elements, in order returned by iterator and compared with
	 * the specified comparator.
	 * 
	 * @param toCopy the collection whose elements are to be placed into this Series
	 * @param comparator the comparator used to compared the elements
	 */
	public ComparableSeries(Collection<E> toCopy, Comparator<E> comparator) {
		super(toCopy);
		this.comparator = comparator;
	}
	
	// Get sorted list of the sub range [from, to) in this Series, in order defined by Comparator or Comparable.
	private List<E> sorted(int from, int to) {
		Objects.checkFromToIndex(from, to, size());
		List<E> v = new LinkedList<>(this.subList(from, to));
		if (this.comparator != null)
			Collections.sort(v, this.comparator);
		else
			Collections.sort(v, new Comparator<E>() {
				// Could throw ClassCastException if the type hasn't implemented Comparable.
				@SuppressWarnings("unchecked")
				@Override
				public int compare(E o1, E o2) {
					return ((Comparable<E>)o1).compareTo(o2);
				}});
		
		return v;
	}
	
	/**
	 * Get biggest comparable element in the latest elements to the specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * 
	 * @param count the number of latest elements to inspect
	 * @return largest element in the latest {@code count} elements
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E high(int count) {
		return high(size() - count, size());
	}
	
	/**
	 * Get biggest comparable element in the range of [from, to).
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * 
	 * @param from starting index, inclusive
	 * @param to ending index, exclusive
	 * @return largest element in the latest {@code count} elements
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E high(int from, int to) {
		return highAt(from, to, 0);
	}

	/**
	 * Get smallest comparable element in the latest elements to the specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * 
	 * @param count the number of latest elements to inspect
	 * @return smallest element in the latest {@code count} elements
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E low(int count) {
		return low(size() - count, size());
	}

	/**
	 * Get smallest comparable element in the range of [from, to).
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * 
	 * @param from starting index, inclusive
	 * @param to ending index, exclusive
	 * @return smallest element in the latest {@code count} elements
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E low(int from, int to) {
		return lowAt(from, to, 0);
	}
	
	/**
	 * Get medium comparable element in the latest elements to the specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * 
	 * @param count the number of latest elements to inspect
	 * @return largest element in the latest {@code count} elements
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E medium(int count) {
		return medium(size() - count, size());
	}
	
	/**
	 * Get medium comparable element in the range of [from, to).
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * 
	 * @param from starting index, inclusive
	 * @param to ending index, exclusive
	 * @return largest element in the latest {@code count} elements
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E medium(int from, int to) {
		return sorted(from, to).get((to - from)/2);
	}
	
	/**
	 * Get n'th biggest element in the latest elements to specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * <p>The specified n'th must be in range [0, count).
	 * 
	 * @param count the number of latest elements to inspect
	 * @param n n'th element
	 * @return n'th biggest element
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E highAt(int count, int n) {
		return highAt(size() - count, size(), n);
	}
	
	/**
	 * Get n'th biggest element in the latest elements to specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * <p>The specified n'th must be in range [0, to - from).
	 * 
	 * @param from starting index, inclusive
	 * @param to ending index, exclusive
	 * @param n n'th element
	 * @return n'th biggest element
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E highAt(int from, int to, int n) {
		Objects.checkIndex(n, to - from);
		return sorted(from, to).get(to - from - 1 - n);
	}
	
	/**
	 * Get n'th smallest element in the latest elements to specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * <p>The specified n'th must be in range [0, count).
	 * 
	 * @param count the number of latest elements to inspect
	 * @param n n'th element
	 * @return n'th smallest element
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E lowAt(int count, int n) {
		return lowAt(size() - count, size(), n);
	}
	
	/**
	 * Get n'th smallest element in the latest elements to specified count.
	 * <p>The generic parameter implements Comparable, or a comparator must be provided.
	 * <p>The specified n'th must be in range [0, to - from).
	 * 
	 * @param from starting index, inclusive
	 * @param to ending index, exclusive
	 * @param n n'th element
	 * @return n'th smallest element
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *         {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *         {@code (fromIndex > toIndex)}
	 */
	public E lowAt(int from, int to, int n) {
		Objects.checkIndex(n, to - from);
		return sorted(from, to).get(n);
	}
}

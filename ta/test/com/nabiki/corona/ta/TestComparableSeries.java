package com.nabiki.corona.ta;

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.LinkedList;

import org.junit.Test;

public class TestComparableSeries {
	
	private final LinkedList<Double> data;
	
	public TestComparableSeries() {
		this.data = new LinkedList<Double>();
		this.data.add(1.0);
		this.data.add(2.0);
		this.data.add(3.0);
		this.data.add(10.0);
		this.data.add(9.0);
		this.data.add(8.0);
		this.data.add(7.0);
		this.data.add(4.0);
		this.data.add(5.0);
		this.data.add(6.0);
	}
	
	@Test
	public void comparator() {
		var as = new ComparableSeries<Double>(this.data, new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}});
		
		assertTrue(as.high(5) == 8);
		assertTrue(as.high(10) == 10);
		assertTrue(as.high(0, 5) == 10);
		assertTrue(as.high(4, 10) == 9);
		assertTrue(as.low(5) == 4);
		assertTrue(as.low(10) == 1);
		assertTrue(as.low(0, 5) == 1);
		assertTrue(as.low(4, 10) == 4);
		assertTrue(as.medium(10) == 6);
		assertTrue(as.medium(5) == 6);
		assertTrue(as.medium(0, 5) == 3);
		assertTrue(as.medium(4,  10) == 7);
	}
	
	@Test
	public void comparable() {
		var as = new ComparableSeries<Double>(this.data);
		
		assertTrue(as.high(5) == 8);
		assertTrue(as.high(10) == 10);
		assertTrue(as.high(0, 5) == 10);
		assertTrue(as.high(4, 10) == 9);
		assertTrue(as.low(5) == 4);
		assertTrue(as.low(10) == 1);
		assertTrue(as.low(0, 5) == 1);
		assertTrue(as.low(4, 10) == 4);
		assertTrue(as.medium(10) == 6);
		assertTrue(as.medium(5) == 6);
		assertTrue(as.medium(0, 5) == 3);
		assertTrue(as.medium(4,  10) == 7);
	}
}

package com.nabiki.corona.ta;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestMA {
	@Test
	public void basic() {
		var ma = new MA(10);
		
		for (double to = 0; to < 99; ++to) {
			ma.add(to);
			
			var from = Math.max(0, to - 9);
			assertTrue(ma.ref(0).value() == average(from, to + 1));
		}
	}
	
	private double average(double from, double to) {
		double total = 0;
		int count = 0;
		for (double x = from; x < to; ++x, ++count)
			total += x;
		return total / count;
	}
}

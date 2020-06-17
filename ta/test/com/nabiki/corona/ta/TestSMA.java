package com.nabiki.corona.ta;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestSMA {
	@Test
	public void basic() {
		var sma = new SMA(10, 3);
		
		sma.add(1);
		sma.add(2);
		sma.add(3);
		
		assertTrue(sma.ref(2).value() == 1);
		assertTrue(sma.ref(1).value() == (2 * 3 + sma.ref(2).value() * 7)/10.0);
		assertTrue(sma.ref(0).value() == (3 * 3 + sma.ref(1).value() * 7)/10.0); 
	}
}

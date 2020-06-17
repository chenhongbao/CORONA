package com.nabiki.corona.ta;

import static org.junit.Assert.*;

import java.util.function.Consumer;

import org.junit.Test;

public class TestSeries {

	@Test
	public void basic() {
		var series = new Series<Integer>();
		for (int i = 0; i < 9; ++i)
			series.add(i);

		// ref().
		assertTrue(series.ref(0) == 8);

		final int[] total = new int[1];
		series.visit(new Consumer<Integer>() {

			@Override
			public void accept(Integer t) {
				total[0] += t;
			}

		}, 5);

		// visit().
		assertTrue(total[0] == (8 + 7 + 6 + 5 + 4));

		// iterablity.
		try {
			for (var e : series);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}

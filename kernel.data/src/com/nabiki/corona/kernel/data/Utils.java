package com.nabiki.corona.kernel.data;

import java.time.LocalDate;

public class Utils {
	public static LocalDate deepCopy(LocalDate source) {
		return LocalDate.of(source.getYear(), source.getMonthValue(), source.getDayOfMonth());
	}

}

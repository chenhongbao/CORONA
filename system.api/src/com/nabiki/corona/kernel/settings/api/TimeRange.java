package com.nabiki.corona.kernel.settings.api;

import java.time.LocalTime;

public interface TimeRange {
	int rank();
	
	LocalTime from();
	
	LocalTime to();
}

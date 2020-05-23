package com.nabiki.corona.system.info.api;

import java.time.LocalTime;

public interface TimeRange {
	int rank();
	
	LocalTime from();
	
	LocalTime to();
}

package com.nabiki.corona.kernel.biz.api;

import java.time.Instant;
import java.util.List;

import org.osgi.service.log.LogEntry;

public interface LogKeeper {
	List<LogEntry> logs();
	
	List<LogEntry> logs(int num);
	
	List<LogEntry> logs(Instant since);
	
	List<LogEntry> logs(Instant from, Instant to);
}

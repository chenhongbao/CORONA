package com.nabiki.corona.kernel.biz.api;

import java.time.Instant;
import java.util.List;

import org.osgi.service.log.LogEntry;

public interface LogKeeper {
	List<LogEntry> getLog();
	
	List<LogEntry> getLog(int num);
	
	List<LogEntry> getLog(Instant since);
	
	List<LogEntry> getLog(Instant from, Instant to);
}

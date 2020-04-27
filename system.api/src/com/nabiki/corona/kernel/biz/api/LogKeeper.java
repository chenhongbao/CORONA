package com.nabiki.corona.kernel.biz.api;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.osgi.service.log.LogEntry;

public interface LogKeeper {
	/**
	 * Get all active logger names.
	 * @return collection of logger names
	 */
	Collection<String> loggers();
	
	List<LogEntry> logs(String loggerName);

	List<LogEntry> logs(int num, String loggerName);

	List<LogEntry> logs(Instant since, String loggerName);
	
	List<LogEntry> logs(long sinceSeq, String loggerName);

	List<LogEntry> logs(Instant from, Instant to, String loggerName);
	
	List<LogEntry> logs(long begSeq, long endSeq, String loggerName);
}

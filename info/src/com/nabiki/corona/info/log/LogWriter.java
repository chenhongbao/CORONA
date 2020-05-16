package com.nabiki.corona.info.log;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LogWriter implements LogListener {
	
	private final LogCache cache;

	public LogWriter(LogCache cache) {
		this.cache = cache;
	}

	@Override
	public void logged(LogEntry entry) {
		this.cache.cache(entry);
	}

}

package com.nabiki.corona.info.log;

import org.osgi.service.log.LogEntry;

import com.nabiki.corona.system.api.KerError;

public interface CacheErrorListener {
	void error(KerError e, LogEntry l);
}

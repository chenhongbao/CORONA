package com.mabiki.corona.loghost.core;

import org.osgi.service.log.LogEntry;

import com.nabiki.corona.kernel.api.KerError;

public interface CacheErrorListener {
	void error(KerError e, LogEntry l);
}

package com.mabiki.corona.loghost;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.mabiki.corona.loghost.core.CacheErrorListener;
import com.mabiki.corona.loghost.core.LogCache;
import com.mabiki.corona.loghost.core.LogWriter;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.biz.api.LogQuery;

@Component
public class LogQueryService implements LogQuery {
	public final static int LOG_CACHE_NUM = 10000;
	public final static String LOG_CACHE_ROOT = "./.logs/";

	@Reference(service = LoggerFactory.class)
	private Logger log;

	@Reference(bind = "bindReaderService", unbind = "unbindReaderService", policy = ReferencePolicy.DYNAMIC)
	volatile LogReaderService readerService;

	public void bindReaderService(LogReaderService service) {
		this.readerService = service;
		register();
	}

	public void unbindReaderService(LogReaderService service) {
		if (this.readerService == service) {
			this.readerService = null;
			this.log.info("Unbind log reader service.");
		}
	}

	private LogCache cache;
	private LogWriter writer;

	// If cache encounters error, write that message for debug.
	private class CacheListener implements CacheErrorListener {
		private PrintWriter pw;

		public CacheListener() {
			var f = new File("fatal.txt");
			try {
				if (!f.exists())
					f.createNewFile();
				this.pw = new PrintWriter(f);
			} catch (IOException |SecurityException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void error(KerError e, LogEntry l) {
			if (e != null)
				e.cause().printStackTrace(this.pw);
			if (l != null)
				this.pw.println(l.toString());
		}

	}

	public LogQueryService() {
		this.cache = new LogCache(Paths.get(LogQueryService.LOG_CACHE_ROOT).toAbsolutePath(),
				LogQueryService.LOG_CACHE_NUM, new CacheListener());
		this.writer = new LogWriter(this.cache);
	}

	@Activate
	void start(ComponentContext ctx) {
		this.log.info("Log keeper starts.");
	}

	@Deactivate
	void stop(ComponentContext ctx) {
		unregister();
	}

	private void register() {
		if (this.cache == null || this.readerService == null)
			return;

		this.readerService.addLogListener(this.writer);
	}

	private void unregister() {
		this.log.info("Unregister log listener.");

		if (this.readerService != null)
			this.readerService.removeLogListener(this.writer);

		this.writer = null;
		this.cache = null;
	}

	@Override
	public Collection<String> loggers() {
		return this.loggers();
	}

	@Override
	public List<LogEntry> logs(String loggerName) {
		return this.cache.logs(0, Long.MAX_VALUE, loggerName);
	}

	@Override
	public List<LogEntry> logs(int num, String loggerName) {
		return this.cache.logs(num, loggerName);
	}

	@Override
	public List<LogEntry> logs(Instant since, String loggerName) {
		return this.cache.logs(since, Instant.now(), loggerName);
	}

	@Override
	public List<LogEntry> logs(Instant from, Instant to, String loggerName) {
		return this.cache.logs(from, to, loggerName);
	}

	@Override
	public List<LogEntry> logs(long sinceSeq, String loggerName) {
		return this.cache.logs(sinceSeq, Long.MAX_VALUE, loggerName);
	}

	@Override
	public List<LogEntry> logs(long begSeq, long endSeq, String loggerName) {
		return this.cache.logs(begSeq, endSeq, loggerName);
	}

}

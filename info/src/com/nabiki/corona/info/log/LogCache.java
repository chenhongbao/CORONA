package com.nabiki.corona.info.log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

import org.osgi.service.log.LogEntry;

import com.nabiki.corona.system.api.KerError;

public class LogCache {

	private final int maxLogs;
	private final Path root;
	private final PrintWriter pw;
	private final CacheErrorListener listener;
	private final Deque<LogEntry> cachedLogs = new ConcurrentLinkedDeque<>();
	private final Set<String> loggers = new ConcurrentSkipListSet<>();

	public LogCache(Path root, int cachedNum, CacheErrorListener l) {
		this.root = root;
		this.maxLogs = cachedNum;
		this.listener = l;
		this.pw = openFile();
	}

	private void ensureRoot() {
		if (this.root == null)
			return;

		try {
			if (!Files.isDirectory(this.root)) {
				Files.createDirectories(this.root);
			}
		} catch (IOException | SecurityException e) {
			try {if (this.listener != null)
				this.listener.error(new KerError("Fail creating logs root.", e), null);
			} catch (Exception ex) {}
		}
	}

	private PrintWriter openFile() {
		// Ensure the existence of the root directory.
		ensureRoot();

		// List all file names of that pattern.
		// 1.xml, 2.xml, 11.xml
		Pattern pattern = Pattern.compile("^\\d+\\.xml$");
		var files = this.root.toFile().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				var m = pattern.matcher(name);
				if (m.matches())
					return true;
				else
					return false;
			}
		});

		Path newPath = null;

		try {
			if (files.length == 0) {
				newPath = Files.createFile(Paths.get(this.root.toString(), "1.xml"));
			} else {
				int n = 0;
				for (var fn : files) {
					var n0 = Integer.parseInt(fn.substring(0, fn.length() - ".xml".length()));
					n = Math.max(n, n0);
				}
				// Increase the max file name by 1, create file with that new name.
				newPath = Files.createFile(Paths.get(this.root.toString(), ++n + ".xml"));
			}

			return new PrintWriter(newPath.toFile());
		} catch (IOException | NumberFormatException e) {
			try {if (this.listener != null)
				this.listener.error(new KerError("Fail creating logs file: " + newPath, e), null);
			} catch (Exception ex) {}
			
			return null;
		}
	}

	private void printLog(LogEntry entry) {
		if (this.pw == null)
			return;

		// Separator.
		this.pw.println("[BEG]");
		
		//Log message.
		this.pw.println("[MES]" + entry.getMessage());
		
		// Time info of the log.
		// Format currentMillis into readable date time.
		var dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getTime()), TimeZone.getDefault().toZoneId());
		this.pw.print(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")));
		this.pw.print("\t" + entry.getSequence());
		
		// Name of the logger.
		this.pw.print("\t" + entry.getLoggerName());
		this.pw.print("\t" + entry.getLogLevel());
		
		// Location of the log.
		this.pw.print("\t" + entry.getBundle().getSymbolicName() + " - " + entry.getBundle().getVersion());
		var location = entry.getLocation().getClassName() + "." + entry.getLocation().getMethodName() + "("
				+ entry.getLocation().getLineNumber() + ")";
		this.pw.print("\t" + location);
		
		// Separator.
		this.pw.println("\t" + entry.getMessage());
		
		if (this.pw.checkError()) {
			try {if (this.listener != null)
				this.listener.error(new KerError("Fail writing log to file."), entry);
			} catch (Exception ex) {}
		}
	}
	
	private void addEntry(LogEntry entry) {
		this.cachedLogs.add(entry);
		if (!this.loggers.contains(entry.getLoggerName()))
			this.loggers.add(entry.getLoggerName());
	}
	
	private void maintainCache() {
		while (this.cachedLogs.size() > this.maxLogs) {
			this.cachedLogs.poll();
		}
	}

	public void cache(LogEntry entry) {
		this.addEntry(entry);
		this.printLog(entry);
	}

	public List<LogEntry> logs(Instant from, Instant to, String loggerName) {
		maintainCache();
		
		long fromMillis = from.toEpochMilli();
		long toMillis = to.toEpochMilli();
		
		List<LogEntry> ret = new LinkedList<>();
		
		var iter = this.cachedLogs.descendingIterator();
		while (iter.hasNext()) {
			var entry = iter.next();
			var t = entry.getTime();
			if (fromMillis <= t && t < toMillis)
				ret.add(entry);
			else if (t < fromMillis)
				break;
		}
		
		return ret;
	}

	public List<LogEntry> logs(long begSeq, long endSeq, String loggerName) {
		maintainCache();
		
		List<LogEntry> ret = new LinkedList<>();
		
		var iter = this.cachedLogs.descendingIterator();
		while (iter.hasNext()) {
			var entry = iter.next();
			var t = entry.getSequence();
			if (begSeq <= t && t < endSeq)
				ret.add(entry);
			else if (t < begSeq)
				break;
		}
		
		return ret;
	}

	public List<LogEntry> logs(int num, String loggerName) {
		maintainCache();
		
		List<LogEntry> ret = new LinkedList<>();
		int cnt = 0;
		
		var iter = this.cachedLogs.descendingIterator();
		while (iter.hasNext() && cnt < num) {
			ret.add(iter.next());
			++cnt;
		}
		
		return ret;
	}

	public Collection<String> loggers() {
		return this.loggers;
	}
}

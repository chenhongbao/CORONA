package com.nabiki.corona.trade.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;

public class SessionWriter {
	private final Path root;
	private final DataCodec codec;
	
	public SessionWriter(Path root, DataCodec codec) {
		this.root = root;
		this.codec = codec;
		
		try {
			Utils.ensureDir(this.root);
		} catch (KerError e) {}
	}
	
	private Path ensureDir(String... subdir) {
		var p = Path.of(this.root.toAbsolutePath().toString(), subdir);
		try {
			Utils.ensureDir(p);
			return p;
		} catch (KerError e) {
			return null;
		}
	}
	
	private void write(byte[] bytes, Path file) throws KerError {
		try {
			if (!file.toFile().exists() && !file.toFile().createNewFile())
				throw new KerError("Fail creating file for session info: " + file.toAbsolutePath());
			
			var os = new FileOutputStream(file.toFile());
			os.write(bytes);
			os.flush();
			os.close();
		} catch (IOException e) {
			throw new KerError("Fail writing session info to file: " + file.toAbsolutePath());
		}
	}
	
	public void write(KerOrder order) throws KerError {
		var dir = ensureDir(order.sessionId());
		if (dir == null)
			throw new KerError("Fail creating directory to save session info: " + order.sessionId());
		
		var bytes = this.codec.encode(order);
		write(bytes, Path.of(dir.toAbsolutePath().toString(), "order.json"));
	}
	
	public String formatTime(LocalTime time) {
		var dt = LocalDateTime.of(LocalDate.now(), time);
		return dt.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
	}
	
	public void write(KerTradeReport rep) throws KerError {
		var dir = ensureDir(rep.sessionId(), "trades");
		if (dir == null)
			throw new KerError("Fail creating directory to save session info: " + rep.sessionId());
		
		var bytes = this.codec.encode(rep);
		write(bytes, Path.of(dir.toAbsolutePath().toString(), "trade." + formatTime(rep.tradeTime()) + ".json"));
	}
	
	public void write(KerOrderStatus status) throws KerError {
		var dir = ensureDir(status.sessionId(), "status");
		if (dir == null)
			throw new KerError("Fail creating directory to save session info: " + status.sessionId());
		
		var bytes = this.codec.encode(status);
		write(bytes, Path.of(dir.toAbsolutePath().toString(), "status." + formatTime(status.updateTime()) + ".json"));
	}
}

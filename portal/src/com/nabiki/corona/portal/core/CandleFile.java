package com.nabiki.corona.portal.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;

public class CandleFile {
	private final Path root;
	private final DataFactory factory = DefaultDataFactory.create();
	private final DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final DateTimeFormatter timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");

	public CandleFile(Path root) {
		this.root = root;
		try {
			Utils.ensureDir(this.root);
		} catch (KerError e) {
		}
	}
	
	public List<KerCandle> read(String symbol, int period) throws KerError {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file(symbol, period))))) {
			var r = new LinkedList<KerCandle>();
			// Skip header.
			br.readLine();
			// Read csv line by line.
			KerCandle candle = null;
			while((candle = csvLine(br.readLine())) != null)
				r.add(candle);
			return r;
		} catch (FileNotFoundException e) {
			throw new KerError("Data file not exist for candle: " + symbol + "/" + period);
		} catch (IOException e) {
			throw new KerError("Fail reading data file for candle: " + symbol + "/" + period);
		}
	}

	public void write(KerCandle candle) throws KerError {
		if (candle == null)
			return;
		// History candle is not real time.
		candle.isRealTime(false);
		
		try (FileWriter fw = new FileWriter(file(candle.symbol(), candle.minutePeriod()))) {
			fw.write(csvLine(candle));
			fw.flush();
		} catch (IOException e) {
			throw new KerError("Fail access file for candle: " + candle.symbol() + "/" + candle.minutePeriod());
		}
	}
	
	protected KerCandle csvLine(String line) throws KerError {
		if (line == null)
			return null;
		
		var str = line.trim();
		if (str.length() == 0)
			return null;
		
		String[] values = str.split(",");
		if (values.length != 14)
			return null;
		
		var r = this.factory.create(KerCandle.class);
		// Set fields.
		r.symbol(values[0]);
		r.openPrice(Double.valueOf(values[1]));
		r.highPrice(Double.valueOf(values[2]));
		r.lowPrice(Double.valueOf(values[3]));
		r.closePrice(Double.valueOf(values[4]));
		r.openInterest(Integer.valueOf(values[5]));
		r.volume(Integer.valueOf(values[6]));
		r.minutePeriod(Integer.valueOf(values[7]));
		r.isDay(Boolean.valueOf(values[8]));
		r.isLastOfDay(Boolean.valueOf(values[9]));
		r.isRealTime(Boolean.valueOf(values[10]));
		r.updateTime(LocalDateTime.parse(values[11], this.timeStamp).toInstant(ZoneOffset.UTC));
		r.tradingDay(LocalDate.parse(values[12], this.date));
		r.actionDay(LocalDate.parse(values[13], this.date));
		
		return r;
	}

	protected String csvLine(KerCandle candle) {
		String line = candle.symbol() + "," + candle.openPrice() + "," + candle.highPrice() + "," + candle.lowPrice()
				+ "," + candle.closePrice() + "," + candle.openInterest() + "," + candle.volume() + ","
				+ candle.minutePeriod() + "," + candle.isDay() + "," + candle.isLastOfDay() + "," + candle.isRealTime()
				+ ",";
		// Build time strings.
		var strUpdateTime = LocalDateTime.ofInstant(candle.updateTime(), ZoneId.systemDefault()).format(this.timeStamp);
		var strTradingDay = candle.tradingDay().format(this.date);
		var strActionDay = candle.actionDay().format(this.date);
		// Append time strings.
		line += strUpdateTime + "," + strTradingDay + "," + strActionDay;
		return line;
	}

	protected String csvHeader() {
		return "symbol,openPrice,highPrice,lowPrice,closePrice,openInterest,volume,minutePeriod,isDay,isLastOfDay,"
				+ "isRealTime,updateTime,tradingDay,actionDay";
	}

	protected File file(String symbol, int period) throws KerError {
		File file = null;

		try {
			var path = Path.of(this.root.toAbsolutePath().toString(), symbol + "." + period + ".csv");
			file = path.toFile();
			
			// Create if not exists and write header.
			if (!file.exists()) {
				file.createNewFile();
				Files.writeString(path, csvHeader());
			}
			
			// Set attributes.
			if (!file.canWrite())
				file.setWritable(true);
			if (!file.canRead())
				file.setReadable(true);
			return file;
		} catch (InvalidPathException e) {
			throw new KerError("Invalid candle data file name: " + file.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new KerError("Fail access candle data file: " + file.getAbsolutePath(), e);
		}
	}
}

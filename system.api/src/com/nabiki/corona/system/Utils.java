package com.nabiki.corona.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.nabiki.corona.DirectionFlag;
import com.nabiki.corona.system.api.KerError;

public class Utils {
	private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss");
	private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddhh:mm:ss");
	
	// Set system console writes to file.
	static {
		var out = filePrintStream(Path.of("./stdout.txt"));
		var err = filePrintStream(Path.of("./stderr.txt"));
		if (out != null)
			System.setOut(out);
		if (err != null)
			System.setErr(err);
	}

	/**
	 * Get file print output stream from path.
	 * 
	 * @param path path to file
	 * @return print stream to given path
	 */
	public static PrintStream filePrintStream(Path path) {
		try {
			var file = path.toFile();
			if (!file.exists() || !file.isFile())
				file.createNewFile();

			return new TimePrintStream(new FileOutputStream(file, true));
		} catch (IOException e) {
			return null;
		}
	}
	
	public static LocalDate date(String date) {
		return LocalDate.parse(date, dateFormat);
	}
	
	public static LocalTime time(String time) {
		return LocalTime.parse(time, timeFormat);
	}
	
	public static long minus(LocalDateTime d1, LocalDateTime d2) {
		return d1.toEpochSecond(ZoneOffset.UTC) - d2.toEpochSecond(ZoneOffset.UTC);
	}
	
	/**
	 * Get {@link LocalDateTime} from string valist.
	 * 
	 * @param vals string list starting with date, then time.
	 * @return date time object parsed from strings
	 */
	public static LocalDateTime dateTime(String date, String time) {
		return LocalDateTime.parse(date + time, dateTimeFormat);	
	}
	
	public static String date(LocalDate date) {
		return date.format(dateFormat);
	}
	
	public static String time(LocalTime time) {
		return time.format(timeFormat);
	}
	
	public static String dateTime(LocalDateTime dateTime) {
		return dateTime.format(dateTimeFormat);
	}
	
	public static Instant timeStamp(String date, String time, int millis) {
		var dt = dateTime(date, time);
		return Instant.ofEpochMilli(dt.toInstant(ZoneOffset.UTC).toEpochMilli() + millis);
	}
	
	public static boolean bool(int is) {
		return is != 0 ? true : false;
	}

	public static LocalDate deepCopy(LocalDate source) {
		return LocalDate.of(source.getYear(), source.getMonthValue(), source.getDayOfMonth());
	}
	
	public static Instant deepCopy(Instant source) {
		return Instant.ofEpochMilli(source.toEpochMilli());
	}

	public static String sessionId() {
		var timeStamp = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss.SSS")).concat(".");
		var id = UUID.randomUUID().toString();
		return timeStamp.concat(id.substring(0, id.indexOf('-')));
	}
	
	private static AtomicInteger incId = new AtomicInteger(0);
	public static int increaseGet() {
		if (incId.get() == Integer.MAX_VALUE)
			incId.set(0);
		return incId.incrementAndGet();
	}

	/**
	 * A unique way to calculate margin or commission.
	 * 
	 * @param price    trade price
	 * @param volume   trade volume
	 * @param multiple instrument's volume multiple
	 * @param byMny    margin/commission rate by money
	 * @param byVol    margin/commission rate by volume
	 * @return necessary margin for the given order
	 */
	public static double marginOrCommission(double price, int volume, int multiple, double byMny, double byVol) {
		if (byVol != 0)
			return volume * byVol;
		else
			return multiple * volume * price * byMny;
	}

	public static double profit(double open, double close, int volume, int multi, char direction) throws KerError {
		double ret = 0.0;
		switch (direction) {
		case DirectionFlag.DIRECTION_BUY:
			ret = (close - open) * volume * multi;
			break;
		case DirectionFlag.DIRECTION_SELL:
			ret = (open - close) * volume * multi;
			break;
		default:
			throw new KerError("Unhandled unknown direction: " + String.valueOf(direction));
		}

		return ret;
	}

	public static boolean validPrice(double price) {
		return 0 < price && price < Double.MAX_VALUE;
	}

	public static void ensureDir(Path p) throws KerError {
		try {
			// Build paths.
			if (!p.toFile().exists() || !p.toFile().isDirectory())
				Files.createDirectories(p);
		} catch (IOException e) {
			throw new KerError("Fail creating non-existing directory: " + p.toAbsolutePath());
		}
	}

	public static String[] getFileNames(Path p, boolean isFile) {
		return p.toFile().list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isFile() == isFile;
			}
		});
	}

	/**
	 * Read all bytes from path.
	 * 
	 * @param p path to file
	 * @return bytes read
	 * @throws KerError IO exception
	 */
	public static byte[] readFile(Path p) throws KerError {
		try {
			var is = new FileInputStream(p.toFile());
			return is.readAllBytes();
		} catch (IOException e) {
			throw new KerError("Fail inputing file: " + p.toAbsolutePath().toString(), e);
		}
	}

	public static boolean same(LocalDate d1, LocalDate d2) {
		if (d1 == null || d2 == null)
			return false;
		return d1.getYear() == d2.getYear() && d1.getDayOfYear() == d2.getDayOfYear();
	}
}

package com.nabiki.corona;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.nabiki.corona.kernel.api.KerError;

public class Utils {
	public static LocalDate deepCopy(LocalDate source) {
		return LocalDate.of(source.getYear(), source.getMonthValue(), source.getDayOfMonth());
	}
	
    public static String sessionId() {
        var timeStamp = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd.HHmmss.SSS"))
                .concat(".");
        var id = UUID.randomUUID().toString();
        return timeStamp.concat(id.substring(0, id.indexOf('-')));
    }

    /**
     * A unique way to calculate margin or commission.
     * 
     * @param price trade price
     * @param volume trade volume
     * @param multiple instrument's volume multiple
     * @param byMny margin/commission rate by money
     * @param byVol margin/commission rate by volume
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

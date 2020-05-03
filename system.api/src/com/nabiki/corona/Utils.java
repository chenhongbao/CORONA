package com.nabiki.corona;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.nabiki.corona.api.State;
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
		case State.DIRECTION_BUY:
			ret = (close - open) * volume * multi;
			break;
		case State.DIRECTION_SELL:
			ret = (open - close) * volume * multi;
			break;
		default:
			throw new KerError("Unhandled unknown direction: " + String.valueOf(direction));
		}

		return ret;
	}
}

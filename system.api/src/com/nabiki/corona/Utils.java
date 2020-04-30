package com.nabiki.corona;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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

}

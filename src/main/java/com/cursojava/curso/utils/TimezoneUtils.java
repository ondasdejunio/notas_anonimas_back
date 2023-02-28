package com.cursojava.curso.utils;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimezoneUtils {
    public static Timestamp getCurrentTimestamp(){
        ZoneId timezone = ZoneId.of("America/Bogota");
        LocalDateTime localDateTime = LocalDateTime.now(timezone);
        return Timestamp.valueOf(localDateTime);
    }
}

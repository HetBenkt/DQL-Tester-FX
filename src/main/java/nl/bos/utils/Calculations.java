package nl.bos.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class Calculations {

    private Calculations() {
    }

    public static String getDurationInSeconds(Instant start, Instant end) {
        long millis = Duration.between(start, end).toMillis();
        Timestamp ts = new Timestamp(millis);
        double time = ts.getTime();
        return String.valueOf(time / 1000) + " sec.";
    }
}

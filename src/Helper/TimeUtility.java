package Helper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * TimeUtility class provides utility methods for managing and converting time.
 * Specifically, it offers methods for converting time between UTC and local time zones.
 */
public class TimeUtility {

    /**
     * Converts the provided date and time string to its representation in UTC.
     * The method uses several intermediate steps and prints out each transformation
     * for debugging purposes. However, the current implementation will always
     * return the input string without any modifications.
     *
     * @param dateTime String representation of date and time to be converted.
     * @return The original dateTime string without modification.
     */
    public static String convertTimeDateUTC(String dateTime) {
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        LocalDateTime ldt = ts.toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime utczdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime ldtIn = utczdt.toLocalDateTime();
        ZonedDateTime zdtOut = ldtIn.atZone(ZoneId.of("UTC"));
        ZonedDateTime zdtOutToLocalTZ = zdtOut.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        LocalDateTime ldtOutFinal = zdtOutToLocalTZ.toLocalDateTime();

        // Print out each transformation for debugging
        System.out.println(ts);
        System.out.println(ldt);
        System.out.println(zdt);
        System.out.println(utczdt);
        System.out.println(ldtIn);
        System.out.println(zdtOut);
        System.out.println(zdtOutToLocalTZ);
        System.out.println(ldtOutFinal);

        return dateTime;
    }
}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.engine.tool;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/* ******************************************************************************** */
/*                                                                                  */
/* EngineTool, */
/*                                                                                  */
/* Different tools */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EngineTool {

    public static String dateToString(LocalDateTime time) {
        if (time == null)
            return null;
        //  datecreation: "2021-01-30T18:52:10.973"
        DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return time.format(sdt);
    }
    /**
     * Transform the date for Human 
     * @param time
     * @return
     */
    public static String dateToHumanString(LocalDateTime time) {
        if (time == null)
            return null;
        //  datecreation: "2021-01-30T18:52:10.973"
        DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm:ss");
        return time.format(sdt);
    }
    /**
     * The local date is saved as absolute. But to send it back to the browser (which are in a time zone), a translation is mandatory 
     * @param time
     * @return
     */
    public static String dateToString(LocalDate time) {
        if (time == null)
            return null;
        // Attention, we have to get the time in UTC first
        //  datecreation: "2021-01-30T18:52:10.973"
        DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return time.format(sdt);
    }
    
    /**
     * Transform the date to a complete string using the TimeZone. Browser received form example "2021-04-12T08:00:00Z" in it is in the Pacific time zone (and for him, it's 2021-04-12'). 
     * If it received 2021-04-12T00:00:00Z, it will display March 11, because Midnigh UTC = day before in California 
     * @param time
     * @param timezoneOffset
     * @return
     */
    public static String dateToTimeString(LocalDate time,long timezoneOffset) {
        if (time == null)
            return null;
        // Attention, we have to get the time in UTC first
        //  datecreation: "2021-01-30T18:52:10.973"
        LocalDateTime localDateTime = time.atStartOfDay( ZoneId.of(ZoneOffset.UTC.getId())).toLocalDateTime();
        localDateTime= localDateTime.minusMinutes( - timezoneOffset);
        DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return time.format(sdt);
    }

    public static LocalDateTime stringToDateTime(String dateInString) {
        if (dateInString == null)
            return null;
        // format is yyyy-MM-ddTHH:mm:ss.sssZ" ==> UTC date
        Instant instant = Instant.parse(dateInString);
        return LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));       
    }

    public static LocalDate stringToDate(String dateInString, long timezoneOffset) {
        if (dateInString == null)
            return null;
        if (dateInString.length()==10) {
            // this is only the format yyyy-MM-dd
            return LocalDate.parse(dateInString);
        }
        Instant instant = Instant.parse(dateInString);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        localDateTime = localDateTime.minusMinutes( timezoneOffset);
        return localDateTime.toLocalDate();        
    }
}

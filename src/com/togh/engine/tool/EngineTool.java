/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.engine.tool;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

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
   *
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
   *
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
   * If it received 2021-04-12T00:00:00Z, it will display March 11, because Midnight UTC = day before in California
   *
   * @param date
   * @param timezoneOffset Browser offset
   * @return
   */
  public static String dateToTimeString(LocalDate date, long timezoneOffset) {
    if (date == null)
      return null;
    // Attention, we have to get the time in UTC first
    //  datecreation: "2021-01-30T18:52:10.973"
    LocalDateTime localDateTime = date.atStartOfDay(ZoneId.of(ZoneOffset.UTC.getId())).toLocalDateTime();
    localDateTime = localDateTime.minusMinutes(timezoneOffset);
    DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    return localDateTime.format(sdt);
  }

  /**
   * Translate a datetime to the local time according the timezoneOffset
   *
   * @param time           Time Date Time
   * @param timezoneOffset Browser offset
   * @return
   */
  public static String dateTimeToTimeString(LocalDateTime time, long timezoneOffset) {
    if (time == null)
      return null;
    // Attention, we have to get the time in UTC first
    //  datecreation: "2021-01-30T18:52:10.973"
    LocalDateTime localDateTime = time.minusMinutes(timezoneOffset);
    DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    return localDateTime.format(sdt);
  }

  /**
   * Translate a datetime to the local time according the timezoneOffset
   *
   * @param time           Time Date Time
   * @param timezoneOffset Browser offset
   * @return date time in the local time
   */
  public static String dateTimeToHumanString(LocalDateTime time, long timezoneOffset) {
    if (time == null)
      return null;
    // Attention, we have to get the time in UTC first
    //  datecreation: "2021-01-30T18:52:10.973"
    LocalDateTime localDateTime = time.minusMinutes(timezoneOffset);
    DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return localDateTime.format(sdt);
  }

  public static LocalDateTime stringToDateTime(String dateInString) {
    if (dateInString == null)
      return null;
    // format is yyyy-MM-ddTHH:mm:ss.sssZ" ==> UTC date
    Instant instant = Instant.parse(dateInString);
    return LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
  }

  /**
   * Received a date from the browser, and translate it to a LocalDate, according the browser timezone offset
   *
   * @param dateInString   Date from browser. Example in California: 2021-11-09T08:00:00.000Z
   * @param timezoneOffset timeZoneOffset from the browser. Example in California: 480
   * @return a LocalDate
   */
  public static LocalDate stringToDate(String dateInString, long timezoneOffset) {
    if (dateInString == null)
      return null;
    if (dateInString.length() == 10) {
      // this is only the format yyyy-MM-dd
      return LocalDate.parse(dateInString);
    }
    Instant instant = Instant.parse(dateInString);
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
    localDateTime = localDateTime.minusMinutes(timezoneOffset);
    return localDateTime.toLocalDate();
  }

  /**
   * Received a date + time from the browser, and translate it to a LocalDateTime, according the browser timezone offset
   *
   * @param dateInString   Date from browser. Example in California: 2021-11-09T08:00:00.000Z
   * @param timezoneOffset timeZoneOffset from the browser. Example in California: 480
   * @param hourMinutes    example : 22:00
   * @return a LocalDateTime (convention: it is in UTC)
   */
  public static LocalDateTime stringToDateTime(String dateInString, long timezoneOffset, String hourMinutes) {
    if (dateInString == null)
      return null;
    LocalDate localDate;
    if (dateInString.length() == 10) {
      // this is only the format yyyy-MM-dd
      localDate = LocalDate.parse(dateInString);
    } else {
      Instant instant = Instant.parse(dateInString);
      LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
      localDateTime = localDateTime.minusMinutes(timezoneOffset);
      localDate = localDateTime.toLocalDate();
    }
    // now add the hourMinute
    LocalDateTime localDateTime = localDate.atStartOfDay();

    if (hourMinutes != null) {
      StringTokenizer st = new StringTokenizer(hourMinutes, ":");
      // hour
      try {
        int hours = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
        int minutes = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 0;
        localDateTime = localDateTime.plusHours(hours);
        localDateTime = localDateTime.plusMinutes(minutes);

      } catch (Exception e) {
        // do nothing, ignore it
      }
    }
    return localDateTime;
  }
}

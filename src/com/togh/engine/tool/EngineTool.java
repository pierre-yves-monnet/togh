/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.engine.tool;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/* ******************************************************************************** */
/*                                                                                  */
/*  EngineTool,                                                                     */
/*                                                                                  */
/*  Different tools                                                                 */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EngineTool {
    
    public static String dateToString(LocalDateTime time ) {
        if (time== null)
            return null;
        //  datecreation: "2021-01-30T18:52:10.973"
        DateTimeFormatter sdt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return time.format(sdt);
    }
    
    
    public static LocalDateTime stringToDate(String dateInString ) {
        
        if (dateInString==null)
            return null;
        // format is yyyy-MM-ddTHH:mm:ss.sssZ" ==> UTC date
        Instant instant = Instant.parse(dateInString);
        LocalDateTime result = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));
        return result;
            }
}

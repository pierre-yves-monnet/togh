/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;
/* -------------------------------------------------------------------- */
/*                                                                      */
/* Ping */
/*                                                                      */
/* -------------------------------------------------------------------- */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("togh")
public class RestPingController {

    private final Logger logger = Logger.getLogger(RestPingController.class.getName());
    private final static String logHeader = RestLoginController.class.getSimpleName() + ": ";

    @Autowired
    DataSource dataSource;

    /**
     * Call the ping function
     *
     * @param message message to send back, to verify this is not a cache URL - optional
     * @return ping information
     */
    @CrossOrigin
    @GetMapping(value = "togh/api/ping", produces = "application/json")
    public Map<String, Object> ping(@RequestParam(required = false) String message) {
        logger.info(logHeader + "Ping!");
        Map<String, Object> result = new HashMap<>();
        result.put("now", LocalDateTime.now());
        if (message != null)
            result.put("message", message);
        // information on the datasource: are we connected?
        try {
            Connection con = dataSource.getConnection();
            con.getMetaData().getDatabaseProductName();
            result.put("database", "Database is up and running");
        } catch (Exception e) {
            result.put("database", "Can't connect");
        }

        return result;
    }

    /**
     * Call the ping function
     *
     * @param message message to send back, to verify this is not a cache URL - optional
     * @return ping information
     */
    @CrossOrigin
    @GetMapping(value = "togh/ping", produces = "application/json")
    public Map<String, Object> directPing(@RequestParam(required = false) String message) {
        return ping(message);
    }

    /**
     * Call the ping function
     *
     * @param message message to send back, to verify this is not a cache URL - optional
     * @return ping information
     */
    @CrossOrigin
    @GetMapping(value = "ping", produces = "application/json")
    public Map<String, Object> toghPing(@RequestParam(required = false) String message) {
        return ping(message);
    }
}

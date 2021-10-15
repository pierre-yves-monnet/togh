/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.engine.logevent.LogEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Monitor service */
/*                                                                      */
/* This service is in charge to track any performance issue, and monitor */
/* the number of time a function is used for example    */
/* -------------------------------------------------------------------- */

@Service
public class MonitorService {

    private final static String logHeader = "com.togh.MonitorService";
    private Logger logger = Logger.getLogger(MonitorService.class.getName());

    public Chrono startOperation(String operationName) {
        Chrono chrono = new Chrono();
        chrono.name = operationName;
        return chrono;
    }

    public void endOperation(Chrono chrono) {
        LocalDateTime timeReference = LocalDateTime.now();
        long milliseconds = ChronoUnit.MILLIS.between(chrono.startChrono, timeReference);
        if (milliseconds > 500)
            logger.info(logHeader + " ****** PERFORMANCE ISSUE " + chrono.name + " in " + milliseconds + " ms");
        // next will be to register that somewhere
    }

    public void endOperationWithStatus(Chrono chrono, String status) {
        LocalDateTime timeReference = LocalDateTime.now();
        long milliseconds = ChronoUnit.MILLIS.between(chrono.startChrono, timeReference);
        if (milliseconds > 500)
            logger.info(logHeader + " ****** PERFORMANCE ISSUE " + chrono.name + " in " + milliseconds + " ms");
        // next will be to register that somewhere
    }

    /**
     * When an error arrived on the server, we want to register it for the adminstrator, and then display it
     *
     * @param listEvents
     */
    public void registerErrorEvents(List<LogEvent> listEvents) {
        /**
         * To be saved somewhere, only the error event.
         * To not register multiple time a day the same error, then we check if it is not already present for this day. If yes, then just add in the counter
         */
    }

    public class Chrono {
        protected LocalDateTime startChrono = LocalDateTime.now();
        protected int nbHits = 0;
        protected String name;

        public void touch() {
            nbHits++;
        }
    }

}

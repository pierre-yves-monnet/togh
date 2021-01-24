package com.togh.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Monitor service */
/*                                                                      */
/* This service is in charge to track any performance issue, and monitor */
/* the number of time a function is used for example    */
/* -------------------------------------------------------------------- */

@Service
public class MonitorService {
    
    private Logger logger = Logger.getLogger( MonitorService.class.getName());
    private final static String logHeader = "com.togh.MonitorService";
    
    public class Chrono {
        protected LocalDateTime startChrono = LocalDateTime.now();
        protected int nbHits=0;
        protected String name;
        public void touch() {
            nbHits++;
        }
    }

    
    public Chrono startOperation( String operationName) {
        Chrono chrono = new Chrono();
        chrono.name = operationName;
        return chrono;
    }
    
    
    public void endOperation( Chrono chrono) {
        LocalDateTime timeReference = LocalDateTime.now();
        long milliseconds = ChronoUnit.MILLIS.between(chrono.startChrono, timeReference);
        if (milliseconds>500)
            logger.info(logHeader+" ****** PERFORMANCE ISSUE "+chrono.name+" in "+milliseconds+" ms");
        // next will be to register that somewhere
    }
    public void endOperationWithStatus( Chrono chrono, String status) {
        LocalDateTime timeReference = LocalDateTime.now();
        long milliseconds = ChronoUnit.MILLIS.between(chrono.startChrono, timeReference);
        if (milliseconds>500)
            logger.info(logHeader+" ****** PERFORMANCE ISSUE "+chrono.name+" in "+milliseconds+" ms");
        // next will be to register that somewhere
    }
}

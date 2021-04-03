package com.togh.engine.chrono;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/* ******************************************************************************** */
/*                                                                                  */
/* ChronoSet, */
/*                                                                                  */
/* Manage a set of chronometer                                                      */
/* When a class need to track time of operation, it creates a ChronoSet
 * Then, it get Chronometer, and use begin/stop
 */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


public class ChronoSet {
    
    private Logger logger = Logger.getLogger(ChronoSet.class.getName());
    private final static String logHeader = "com.togh.engine.chrono.ChronoSet: ";

    public Map<String, Chronometer> mapChrono = new HashMap<>();
    public ChronoSet() {
        // nothing to do
    }

    
    public Chronometer getChronometer( String operationName ) {
        Chronometer chrono = mapChrono.computeIfAbsent(operationName, k -> new Chronometer( operationName));
        // do systematicaly a start
        chrono.start();
        return chrono;
    }
    
    public void logChronometer() {
        StringBuilder logChrono = new StringBuilder();
        for (Chronometer chrono : mapChrono.values()) {
            logChrono.append( chrono.getName()+": "+chrono.getTimeInMs()+" ms ("+chrono.getNbExecution()+"),");
        }
        logger.info(logHeader+logChrono.toString());
    }
    /**
     * 
     * @return
     */
    public Map<String,Object> getMap() {
        Map<String,Object> result = new HashMap<>();
        for (Chronometer chrono : mapChrono.values()) {
            result.put( chrono.getName(), chrono.getMap());
        }
        return result;
    }
}

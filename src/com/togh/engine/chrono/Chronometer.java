package com.togh.engine.chrono;

import java.util.HashMap;
import java.util.Map;

/* ******************************************************************************** */
/*                                                                                  */
/* Chronometer */
/*                                                                                  */
/* Manage one chronometer */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class Chronometer {

    private long beginTime;
    private long endTime;
    private String name;
    private long cumulateTime = 0;
    private long nbExecutions;

    /**
     * Get the chronometer implied it is started
     * Default Constructor.
     * 
     * @param operationName
     */
    protected Chronometer(String operationName) {
        this.name = operationName;
    }

    /**
     * restart the chronometer
     */
    public void start() {
        this.beginTime = System.currentTimeMillis();
    }

    /**
     * 
     */
    public void stop() {
        endTime = System.currentTimeMillis();
        cumulateTime += endTime - beginTime;
        nbExecutions++;
    }

    public String getName() {
        return name;
    }

    public long getTimeInMs() {
        return cumulateTime;
    }

    public long getNbExecution() {
        return nbExecutions;
    }
    
    public long getAverageInMs() {
        return (getNbExecution() > 0 ? (int) (getTimeInMs()/ getNbExecution()): 0);
    }
    
    public Map<String,Object> getMap() {
        Map<String,Object> resultChrono = new HashMap<>();
        resultChrono.put("name", getName());            
        resultChrono.put("timeinms", getTimeInMs() );
        resultChrono.put("nbexecutions", getNbExecution());
        resultChrono.put("average", getAverageInMs());
        return resultChrono;
   
    }
}

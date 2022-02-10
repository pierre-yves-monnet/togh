package com.togh.engine.chrono;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
    private long cumulateTime = 0;
    private long nbExecutions;

    private final static Logger LOGGER = Logger.getLogger(Chronometer.class.getName());
    private final String name;

    /**
     * Create a chronometer
     * Default Constructor.
     *
     * @param operationName name of operation to monitor
     */
    protected Chronometer(String operationName) {
        this.name = operationName;
    }

    /**
     * Restart the chronometer
     */
    public void start() {
        this.beginTime = System.currentTimeMillis();
    }

    /**
     * Stop the chronometer
     */
    public void stop() {
        cumulateTime += System.currentTimeMillis() - beginTime;
        nbExecutions++;
    }

    /**
     * Stop the chronometer and log it if it is more than the expected limit
     *
     * @param logWhenSlowerThanInMs limit in ms. If the time is upper, then log it at info level
     */
    public void stopAndLog(int logWhenSlowerThanInMs) {
        long executionTime = System.currentTimeMillis() - beginTime;
        cumulateTime += executionTime;
        nbExecutions++;
        if (executionTime > logWhenSlowerThanInMs) {
            LOGGER.info(Chronometer.class.getName() + ": operation [" + name + "] execution is " + executionTime + " ms");
        }
    }

    /**
     * Get the name of the chronometer
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the total time accumulated by the chronometer
     *
     * @return the total time in ms
     */
    public long getTimeInMs() {
        return cumulateTime;
    }

    /**
     * A chronometer may have multiple stop / start. Get the number of executions
     *
     * @return the number of executions
     */
    public long getNbExecution() {
        return nbExecutions;
    }

    /**
     * Get the average, per execution
     *
     * @return the average per execution. If there was no execution, return 0
     */
    public long getAverageInMs() {
        return (getNbExecution() > 0 ? (int) (getTimeInMs() / getNbExecution()) : 0);
    }

    /**
     * Get a map to display in JSON for example
     *
     * @return a Map to describe the chronometer
     */
    public Map<String, Object> getMap() {
        Map<String, Object> resultChrono = new HashMap<>();
        resultChrono.put("name", getName());
        resultChrono.put("timeinms", getTimeInMs());
        resultChrono.put("nbexecutions", getNbExecution());
        resultChrono.put("average", getAverageInMs());
        return resultChrono;
    }
}

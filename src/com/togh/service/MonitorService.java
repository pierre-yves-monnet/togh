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
import org.springframework.beans.factory.annotation.Autowired;
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

  private final static String logHeader = MonitorService.class.getName() + ":";
  private final Logger logger = Logger.getLogger(MonitorService.class.getName());

  @Autowired
  private LogService LogService;

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
   * When an error arrive in the server, we want to register it for the administrator, and then display it
   *
   * @param listEvents list of event to register
   */
  public void registerErrorEvents(List<LogEvent> listEvents) {
    LogService.registerLog(listEvents, null);
  }

  public static class Chrono {
    protected LocalDateTime startChrono = LocalDateTime.now();
    protected int nbHits = 0;
    protected String name;

    public void touch() {
      nbHits++;
    }
  }

}

package com.togh.service;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Log service */
/*                                                                      */
/* This service save any main info / error in the database              */
/* the number of time a function is used for example    */
/* -------------------------------------------------------------------- */

import com.togh.engine.logevent.LogEvent;
import com.togh.entity.LogEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.logging.Logger;

@Service
public class LogService {
    private final static String LOG_HEADER = LogService.class.getName() + ":";
    private final Logger logger = Logger.getLogger(LogService.class.getName());

    @Autowired
    private LogRepository logRepository;

    /**
     * registerLog. A list of events are created, information are managed
     *
     * @param listEvents event to logs
     */
    public void registerLog(List<LogEvent> listEvents, ToghUserEntity userEntity) {
        try {
            listEvents.stream()
                    .filter(t -> {
                        return t.isError() || t.getLevel().equals(LogEvent.Level.MAININFO) || t.getLevel().equals(LogEvent.Level.SUCCESS);
                    })
                    .map(t -> {
                        LogEntity logEntity = new LogEntity();
                        logEntity.setLogEventDate(LocalDateTime.now(ZoneOffset.UTC));
                        logEntity.setLogEventLevel(t.getLevel().toString());
                        logEntity.setLogEventTitle(t.getTitle());
                        logEntity.setLogEventParameters(t.getParameters());
                        if (userEntity != null)
                            logEntity.setUserName(userEntity.getLogLabel());
                        return logEntity;
                    })
                    .forEach(t -> {
                        logRepository.save(t);
                    });

        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't save log " + e.toString());
        }

    }
}

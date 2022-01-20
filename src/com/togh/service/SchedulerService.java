/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class SchedulerService {

    @Autowired
    LoginService loginService;

    @Autowired
    EventService eventService;

    @Scheduled(fixedDelay = 3600000)
    public void scheduleDisconnectUser() {
        loginService.disconnectInactiveUsers();
    }

    // 18000000 : 6 H
    // 3600000 : 1 H
    // 60000 : 1 mn
    @Scheduled(fixedDelay = 18000000)
    public void scheduleCloseEvents() {
        eventService.closeOldEvents(true);
        eventService.purgeOldEvents(true);
    }

}



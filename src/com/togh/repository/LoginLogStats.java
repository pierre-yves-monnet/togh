/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.service.LoginService;

public class LoginLogStats {
    private String timeSlot;
    private LoginService.LoginStatus statusConnection;
    private long numberOfEvents;
    private long numberOfTentatives;

    public LoginLogStats(String timeSlot, LoginService.LoginStatus statusConnection, long numberOfEvents, long numberOfTentatives) {
        this.timeSlot = timeSlot;
        this.statusConnection = statusConnection;
        this.numberOfEvents = numberOfEvents;
        this.numberOfTentatives = numberOfTentatives;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public LoginService.LoginStatus getStatusConnection() {
        return statusConnection;
    }

    public long getNumberOfEvents() {
        return numberOfEvents;
    }

    public long getNumberOfTentatives() {
        return numberOfTentatives;
    }
}

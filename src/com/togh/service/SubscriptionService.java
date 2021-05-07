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
import org.springframework.stereotype.Service;

import com.togh.entity.EventEntity.SubscriptionEventEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.entity.base.BaseEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* SubscriptionService, All information about subscription */
/*                                                                                  */
/*  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
@Service
public class SubscriptionService {

    @Autowired
    StatsService statsService;

    /**
     * How many event a user can create in the month?
     * 
     * @param subscription
     * @return
     */
    public int getMaximumEventsPerMonth(SubscriptionUserEnum subscription) {
        switch (subscription) {
            case FREE:
                return 10;
            case PREMIUM:
                return 100;
            case EXCELLENCE:
                return 1000;
            default:
                return 20;
        }
    }

    public int getMaximumEntityPerEvent(SubscriptionEventEnum subscription, BaseEntity baseEntity) {
        // the for the tchat, we add 50 lines
        int baseValue = 0;
        switch (subscription) {
            case FREE:
                return 1 * baseValue + 20;
            case PREMIUM:
                return 3 * baseValue + 100;
            case EXCELLENCE:
                return 5 * baseValue + 200;
            default:
                return 20;
        }
    }

    /**
     * Attention, this entity is used in entity - pay attention to the size
     */

    public enum LimitReach {
        CREATIONEVENT, TASKLIST, ITINERARY, SHOPPING, SURVEY, SURVEYCHOICE
    };

    /**
     * A new time, a user reach the limit. Register it.
     * 
     * @param toghUserEntity
     * @param limit
     */
    public void registerTouchLimitSubscription(ToghUserEntity toghUserEntity, LimitReach limit) {
        statsService.registerLimitSubcription(toghUserEntity.getSubscriptionUser(), limit);
    }

}
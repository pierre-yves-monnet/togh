/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.entity.EventEntity.SubscriptionEventEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.service.event.EventChatController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
   * @param subscription subscription
   * @return the maximum event according the subscription
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

  public int getMaximumEntityPerEvent(SubscriptionEventEnum subscription, Class<?> eventController) {
    // the for the tchat, we add 50 lines
    int baseValue = 0;
    if (eventController == EventChatController.class)
      baseValue = 100;
    switch (subscription) {
      case FREE:
        return baseValue + 20;
      case PREMIUM:
        return 3 * baseValue + 100;
      case EXCELLENCE:
        return 5 * baseValue + 200;
      default:
        return 20;
    }
  }

  /**
   * A new time, a user reach the limit. Register it.
   *
   * @param toghUserEntity the user
   * @param limit          the limit
   */
  public void registerTouchLimitSubscription(ToghUserEntity toghUserEntity, LimitReach limit) {
    statsService.registerLimitSubcription(toghUserEntity.getSubscriptionUser(), limit);
  }

  /**
   * Attention, this entity may be large, pay attention to the size
   */

  public enum LimitReach {
    CREATIONEVENT, TASKLIST, ITINERARY, SHOPPING, SURVEY, SURVEYCHOICE, CHATGROUP, CHAT, PARTICIPANT, GAME
  }

}

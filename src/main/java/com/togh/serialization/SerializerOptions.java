/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

/* ******************************************************************************** */
/*                                                                                  */
/* Option to serialize */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import com.togh.entity.ToghUserEntity;
import com.togh.eventgrantor.access.EventAccessGrantor;
import com.togh.service.event.EventController;
import lombok.Data;

public @Data
class SerializerOptions {

  private final ToghUserEntity toghUser;
  private final EventController eventController;
  private final Long timezoneOffset;
  private final ContextAccess contextAccess;
  private final EventAccessGrantor eventAccessGrantor;
  private final boolean isHighProtectionUser;

  /**
   * Serialization option. EventEntity os carry by the EventController object
   *
   * @param toghUser        User behind the request
   * @param eventController event controller. The EventEntity is carry by the controller
   * @param contextAccess   Context Access (Search? Admin?)
   * @param timezoneOffset  Time zone offset of the access
   */
  public SerializerOptions(ToghUserEntity toghUser,
                           EventController eventController,
                           Long timezoneOffset,
                           ContextAccess contextAccess,
                           EventAccessGrantor eventAccessGrantor) {
    this.toghUser = toghUser;
    this.eventController = eventController;
    this.timezoneOffset = timezoneOffset;
    this.contextAccess = contextAccess;
    this.eventAccessGrantor = eventAccessGrantor;
    this.isHighProtectionUser = !eventAccessGrantor.isOtherParticipantsVisible();
  }
  /**
   * In a Secret Event, participants name must be hide everywhere
   */

  /**
   * Options to serialize something else than an event
   *
   * @param toghUser       User behind the request
   * @param timezoneOffset ime zone offset of the access
   * @aram contextAccess Context Access (Search? Admin?)
   */
  public SerializerOptions(ToghUserEntity toghUser,
                           Long timezoneOffset,
                           ContextAccess contextAccess) {
    this.toghUser = toghUser;
    this.eventController = null;
    this.timezoneOffset = timezoneOffset;
    this.contextAccess = contextAccess;
    this.eventAccessGrantor = null;
    this.isHighProtectionUser = false;
  }

  public EventAccessGrantor getEventAccessGrantor() {
    return eventAccessGrantor;
  }

  // define how the information is accessed. Is that for a Search, by an admin? is the access is made from a SecretEvent?
  // SEARCH : Search (User, event)
  // EVENTACCESS: access an event
  // ADMIN: administrator access, give back everything
  // MYPROFILE: I want to access my profile
  public enum ContextAccess {
    ADMIN, MYPROFILE, EVENTACCESS, SEARCH

  }


}

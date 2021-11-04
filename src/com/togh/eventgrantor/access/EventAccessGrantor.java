/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.eventgrantor.access;

import com.togh.entity.ParticipantEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.serialization.SerializerOptions;
import com.togh.service.event.EventController;

import java.util.Map;

public class EventAccessGrantor {

    private EventController eventController;
    /**
     * Different information on the way to see an event
     * This information pilot the Grant, and serialization
     * Example:
     * - access as an ADMIN: see all
     * event:
     * Scope
     * Life cycle user      SECRET          |  LIMITED      | OPENINVITATION    | PUBLIC
     * External user       No access       |  No access    | No access         | ReadOnly
     * User invited        Read Only       | Read Only     | Read Only         | Read Only
     * Invitation accepted Parti. protected| All           | All               | All
     */
    /**
     * Is the user can access the event? Or only the header, then he asks to get an invitation?
     */
    private boolean hasAccess;
    /**
     * The event is in Read Only
     */
    private boolean isReadOnly;
    /**
     * Is the participants are visible? Depends on the type of event, and the position of the ToghUser
     * Example, event=SECRET, not visible. OPENCONF? Visible only when user is accepted.
     */
    private boolean isOtherParticipantsVisible;
    /**
     * Is the user can access this event by himself?
     */
    private boolean isAbleToJoinByMyself;


    /**
     * According to the user, and the type of event, the ContextAccess is calculated
     *
     * @param toghUser the toghUser
     * @return the context access
     */
    public static EventAccessGrantor getEventAccessGrantor(EventController eventController,
                                                           ToghUserEntity toghUser,
                                                           SerializerOptions.ContextAccess contextAccess) {
        EventAccessGrantor eventAccessGrantor = new EventAccessGrantor();
        eventAccessGrantor.eventController = eventController;
        if (contextAccess == SerializerOptions.ContextAccess.ADMIN) {
            eventAccessGrantor.hasAccess = true;
            eventAccessGrantor.isReadOnly = false;
            eventAccessGrantor.isOtherParticipantsVisible = true;
            eventAccessGrantor.isAbleToJoinByMyself = false;
            return eventAccessGrantor;
        }

        ParticipantEntity participant = eventController.getParticipant(toghUser);
        boolean inTheEvent = true;
        if (participant == null
                || participant.getStatus() == ParticipantEntity.StatusEnum.INVITED
                || participant.getStatus() == ParticipantEntity.StatusEnum.LEFT) {
            eventAccessGrantor.isReadOnly = true;
            inTheEvent = false;

        }


        switch (eventController.getEvent().getTypeEvent()) {
            case OPEN:
                eventAccessGrantor.hasAccess = true;
                eventAccessGrantor.isAbleToJoinByMyself = true;
                break;
            // Open Conf: I can see it, but I need to be invited to join
            case OPENCONF:
                eventAccessGrantor.hasAccess = inTheEvent;
                // In the event? Participants information can be accessible
                eventAccessGrantor.isOtherParticipantsVisible = inTheEvent;
                break;

            case LIMITED:
                if (inTheEvent || participant != null) {
                    eventAccessGrantor.hasAccess = true;
                    eventAccessGrantor.isOtherParticipantsVisible = true;
                } else {
                    eventAccessGrantor.hasAccess = false;
                }
                break;


            case SECRET:
                eventAccessGrantor.isOtherParticipantsVisible = false;
                if (participant != null)
                    eventAccessGrantor.hasAccess = true;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + eventController.getEvent().getTypeEvent());
        }
        return eventAccessGrantor;
        /*
        // event is public : so show only what you want to show to public
        if (eventEntity.getTypeEvent() == EventEntity.TypeEventEnum.OPEN)
            return ContextAccess.PUBLICACCESS;
        // event is secret : hide all at maximum
        if (eventEntity.getTypeEvent() == EventEntity.TypeEventEnum.SECRET)
            return ContextAccess.SECRETACCESS;

        ParticipantEntity participant = getParticipant(toghUser);
        if (eventEntity.getTypeEvent() == EventEntity.TypeEventEnum.OPENCONF) {
            // the user is not accepted : show the minimum.
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == ParticipantEntity.StatusEnum.ACTIF)
                return ContextAccess.PUBLICACCESS;
            // user left, or wait for the confirmation
            return ContextAccess.SECRETACCESS;
        }
        if (eventEntity.getTypeEvent() == EventEntity.TypeEventEnum.LIMITED) {
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == ParticipantEntity.StatusEnum.ACTIF)
                return ContextAccess.FRIENDACCESS;
            // user left, or wait for the confirmation
            return ContextAccess.SECRETACCESS;
        }
        // should not be here
        return ContextAccess.SECRETACCESS;
        */

    }

    public boolean isOtherParticipantsVisible() {
        return isOtherParticipantsVisible;
    }

    /**
     * return true when the user who ask the request is an active participant on the event
     *
     * @return
     */

    public boolean isActiveParticipant(ToghUserEntity toghUser) {
        return eventController.isActiveParticipant(toghUser);
    }

    /**
     * @return true if the access is a public access
     */
    public boolean isPublicAccess() {
        return false;
    }

    public Map<String, Object> getControlInformation() {
        return Map.of("readOnly", isReadOnly,
                "isAbleToJoinByMyself", isAbleToJoinByMyself);
    }

}

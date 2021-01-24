package com.togh.event;

import org.springframework.beans.factory.annotation.Autowired;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.repository.EndUserRepository;
import com.togh.service.EventService;
import com.togh.service.ToghUserService;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventControler,                                                                 */
/*                                                                                  */
/*  Control what's happen on an event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventControler {

    @Autowired
    private EventService eventService;

    @Autowired
    private ToghUserService userService;
    

    /* ******************************************************************************** */
    /*                                                                                  */
    /* operations on event                                                      */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * This method check the event consistant. It may be read from the database, and this version change some item.
     * Or it can be just created, and we want to have the default information
     */
    public void completeConsistant(EventEntity event) {
        // the author is a Organizer participant
        boolean authorIsReferenced=false;
        for (ParticipantEntity participant : event.getParticipants()) {
            if (participant.getUserId().equals( event.getAuthorId())) {
                authorIsReferenced=true;
                participant.setRole( ParticipantRoleEnum.ORGANIZER);
            }
        }
        if (! authorIsReferenced) {
            ParticipantEntity participant = new ParticipantEntity();
            // if the user does not exist, this is an issue.... ==> ToghEvent
            
            participant.setUserId( event.getAuthorId() );
            participant.setRole( ParticipantRoleEnum.ORGANIZER );
            participant.setStatus( StatusEnum.ACTIF );
            event.addPartipants( participant );
        }
        
        
                
    }
        
    

}

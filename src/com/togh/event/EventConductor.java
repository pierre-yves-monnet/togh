package com.togh.event;

import org.springframework.beans.factory.annotation.Autowired;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.repository.EndUserRepository;
import com.togh.service.EventService;
import com.togh.service.FactoryService;
import com.togh.service.ToghUserService;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventConductor,                                                                 */
/*                                                                                  */
/*  Control what's happen on an event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventConductor {

    @Autowired
    private FactoryService factoryService;

   
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
                participant.setRole( ParticipantRoleEnum.OWNER);
            }
        }
        if (! authorIsReferenced) {
            // if the user does not exist, this is an issue.... ==> ToghEvent
            event.addPartipant( event.getAuthor(), ParticipantRoleEnum.OWNER,StatusEnum.ACTIF  );
        }
        
        // a date policy must be set
        if (event.getDatePolicy()==null)
            event.setDatePolicy( DatePolicyEnum.ONEDATE);
    }
        
    

}

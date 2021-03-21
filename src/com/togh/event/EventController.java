/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.event;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.FactoryService;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventController,                                                                 */
/*                                                                                  */
/*  Control what's happen on an event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventController {

    @Autowired
    private FactoryService factoryService;

    private EventEntity event;
    
    public EventController(EventEntity event ) {
        this.event = event;
    }
    
    public static EventController getInstance( EventEntity event ) {
        return new EventController( event );        
    }
    
    
    public EventEntity getEvent() {
        return event;
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* operations on event                                                      */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * This method check the event consistant. It may be read from the database, and this version change some item.
     * Or it can be just created, and we want to have the default information
     */
    public void completeConsistant() {
        
        // the author is a Organizer participant
        boolean authorIsReferenced=false;
        for (ParticipantEntity participant : event.getParticipants()) {
            if (participant.getUser() != null && participant.getUser().getId().equals( event.getAuthorId())) {
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
        


    /* ******************************************************************************** */
    /*                                                                                  */
    /* Authorisation                                                                    */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * isRegisteredParticipant. if this user is part of this event?
     * @param userId
     * @return
     */
    public boolean isAccess( ToghUserEntity toghUser ) {
        if (event.getTypeEvent() ==  TypeEventEnum.OPEN)
            return true;
        return getParticipant( toghUser ) != null;
    }
    /**
     * User must be the author, or a partipant, or should be invited
     * @param userId
     * @param event
     * @return
     */
    public boolean isActiveParticipant( ToghUserEntity toghUser) {
        ParticipantEntity participant  = getParticipant( toghUser );
        if (participant==null )
            return false;
        if (ParticipantRoleEnum.OWNER.equals( participant.getRole()) 
                || ParticipantRoleEnum.ORGANIZER.equals(participant.getRole() )
                || ParticipantRoleEnum.PARTICIPANT.equals( participant.getRole() ))
        {
            return StatusEnum.ACTIF.equals( participant.getStatus() );
        }
        return false;
    }
    /**
     * is this user an organizer? Some operation, like invitation, is allowed only for organizer
     * @param userId
     * @return
     */
    public boolean isOrganizer(ToghUserEntity toghUser) {
        ParticipantEntity participant  = getParticipant( toghUser );
        if (participant==null )
            return false;
        return participant.getRole().equals(ParticipantRoleEnum.OWNER) ||participant.getRole().equals(ParticipantRoleEnum.ORGANIZER);
    }
    /**
     *  get the role of this userId in the event. Return null if the user does not have any participation
     * @param userId
     * @return
     */
    public ParticipantRoleEnum getRoleEnum( ToghUserEntity toghUser) {
        ParticipantEntity participant  = getParticipant( toghUser );
        return (participant==null ? null : participant.getRole());
    }
    
    /**
     * 
     * @param userId
     * @return
     */
    public ParticipantEntity getParticipant(ToghUserEntity toghUser) {
        for (ParticipantEntity participant : event.getParticipants()) {
            if (participant.getUser() !=null && participant.getUser().getId().equals( toghUser.getId()))
                return participant;
        }
        return null;
    }
    
    /**
     * According the user, and the type of event, the ContextAccess is calculated
     * @param event
     * @return
     */
    public ContextAccess getTypeAccess( ToghUserEntity toghUser ) {
        // event is public : so show onkly what you want to show to public
        if (event.getTypeEvent()== TypeEventEnum.OPEN)
            return ContextAccess.PUBLICACCESS;
        // event is secret : hide all at maximum
        if (event.getTypeEvent() == TypeEventEnum.SECRET)
            return ContextAccess.SECRETACCESS;

        ParticipantEntity participant = getParticipant( toghUser );
        if (event.getTypeEvent() == TypeEventEnum.OPENCONF) {
            // the user is not accepted : show the minimum.
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == StatusEnum.ACTIF)
                return ContextAccess.PUBLICACCESS;
            // user left, or wait for the confirmation 
            return ContextAccess.SECRETACCESS;
        }
        if (event.getTypeEvent() == TypeEventEnum.LIMITED) {
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == StatusEnum.ACTIF)
                return ContextAccess.FRIENDACCESS;
            // user left, or wait for the confirmation 
            return ContextAccess.SECRETACCESS;
        }
        // should not be here
        return ContextAccess.SECRETACCESS;
        
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Invitation                                                                    */
    /*                                                                                  */
    /* ******************************************************************************** */
    public  InvitationResult invite(EventEntity event, ToghUserEntity invitedByToghUser, List<Long> listUsersId, String userInvitedEmail, ParticipantRoleEnum role, String message) {
        EventInvitation eventInvitation = new EventInvitation( this );
        return eventInvitation.invite( event,  invitedByToghUser, listUsersId,  userInvitedEmail,  role,  message);
    }
 
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Update                                                                    */
    /*                                                                                  */
    /* ******************************************************************************** */

    public EventOperationResult update(List<Map<String,Object>>  listSlab) {
        
        EventUpdate eventUpdate = new EventUpdate( this );
        return eventUpdate.update( listSlab);
        
    }
    

}

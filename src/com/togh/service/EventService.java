package com.togh.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.togh.controller.RestEventControler;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.event.EventConductor;
import com.togh.repository.EventRepository;
import com.togh.service.MonitorService.Chrono;
import com.togh.service.ToghUserService.CreationStatus;

/* ******************************************************************************** */
/*                                                                                  */
/*    EventService, access and manipulate event                                     */
/*                                                                                  */
/* The eventService is the Facade, it use classes under com.togh.event package      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class EventService {

    
    @Autowired
    FactoryService factoryService;
    
    @Autowired
    private EventRepository eventRepository;


    
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    /**
     * 
     * @param user
     * @return
     */
    public EventEntity createEvent( ToghUserEntity user) {
        EventEntity event = new EventEntity();
        event.setAuthor(user);
        event.setName("New event");
        event.setDatecreation( LocalDateTime.now( ZoneOffset.UTC ));
        event.setStatusEvent(StatusEventEnum.INPREPAR);
        event.setTypeEvent(TypeEventEnum.LIMITED);
        event.setDatePolicy(DatePolicyEnum.ONEDATE);
        
        EventConductor eventConductor = new EventConductor();
        // let's the conductor create the participant and all needed information
        eventConductor.completeConsistant( event );
        eventRepository.save(event);
        
        return event;
        
    }
   public List<EventEntity> getEvents(long userId, String filterEvents) {

       return eventRepository.findEventsUser( userId );
       
    }
    
   /**
    * 
    * @param userId
    * @param eventId
    * @return
    */
    public EventEntity getEventById( long userId, long eventId) {
        EventEntity event =  eventRepository.findByEventId( eventId );
        if (! event.isAccess( userId ))
            return null;
        
        // check consistant now
        EventConductor eventControler = new EventConductor();
        eventControler.completeConsistant( event );
        
        return event; 
        
    }
   
    
    public enum InvitationStatus {
        DONE, ALREADYAPARTICIPANT, NOTAUTHORIZED, ERRORDURINGCREATIONUSER, ERRORDURINVITATION, INVITATIONSENT, INVALIDUSERID
    };
    public class InvitationResult {
        public InvitationStatus status;
        public ToghUserEntity thogUserInvited;
        
        public ParticipantEntity participant;
    }
    public InvitationResult invite( EventEntity event, Long invitedByUserId, Long userInvitedId, String userInvitedEmail, ParticipantRoleEnum role, String message ) {

        MonitorService monitorService = factoryService.getMonitorService();
        InvitationResult invitationResult = new InvitationResult();
        if (! event.isOrganizer(invitedByUserId)) {
            invitationResult.status =InvitationStatus.NOTAUTHORIZED;
            return invitationResult;
        }

        Chrono chronoInvitation = monitorService.startOperation("Invitation");
        ToghUserService userService = factoryService.getToghUserService();
        
        // first, check if this email is a registered Toghuser
        ToghUserEntity invitedByUser = invitedByUserId==null ? null : userService.getUserFromId(invitedByUserId);
        invitationResult.thogUserInvited = null;
       
        // invitation by the email? 
        if (userInvitedId == null && userInvitedEmail!=null) {
            invitationResult.thogUserInvited  = userService.getFromEmail( userInvitedEmail );
            if (invitationResult.thogUserInvited ==null) {
                // this is a real new user, register and invite it to join Togh
                CreationStatus creationStatus = userService.inviteNewUser(userInvitedEmail, invitedByUser, event);
                if (creationStatus.userEntity == null) {
                    invitationResult.status = InvitationStatus.ERRORDURINGCREATIONUSER;
                    return invitationResult;
                }
                invitationResult.thogUserInvited  = creationStatus.userEntity;
                
                invitationResult.status = InvitationStatus.INVITATIONSENT;
            }
        }
        else if (userInvitedId !=null) {
            invitationResult.thogUserInvited  = userService.getUserFromId( userInvitedId);
            if (invitationResult.thogUserInvited  == null) {
                invitationResult.status = InvitationStatus.INVALIDUSERID;
                monitorService.endOperation(chronoInvitation);
                return invitationResult;
            }
        }
            
        // check if it is not already invited
        if (invitationResult.thogUserInvited==null) {
            // Not by the email, not by the ID ? Something is not expected here
            invitationResult.status = InvitationStatus.ERRORDURINVITATION;
            monitorService.endOperation(chronoInvitation);
            return invitationResult;
            
        }
        invitationResult.participant = event.getParticipant (invitationResult.thogUserInvited.getId() );
        // already in the participant list ? 
        if (invitationResult.participant !=null) {
            invitationResult.status = InvitationStatus.ALREADYAPARTICIPANT;
            monitorService.endOperation(chronoInvitation);
            return invitationResult;
        }
        // create a new participant then!
        
        invitationResult.participant = event.addPartipant(invitationResult.thogUserInvited, role, StatusEnum.INVITED );
        
        eventRepository.save(event);
        if (invitationResult.status == null)
            invitationResult.status = InvitationStatus.DONE;
        
        monitorService.endOperation(chronoInvitation);
        return invitationResult;
    }

    
    
}

package com.togh.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.event.EventController;
import com.togh.repository.EventRepository;
import com.togh.restcontroller.RestTool;
import com.togh.service.MonitorService.Chrono;
import com.togh.service.ToghUserService.CreationResult;

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
        
        EventController eventConductor = new EventController( event );
        // let's the conductor create the participant and all needed information
        eventConductor.completeConsistant();
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
        EventController eventConductor = new EventController( event );
        if (! eventConductor.isAccess( userId ))
            return null;
        
        // check consistant now

        eventConductor.completeConsistant( );
        
        return event; 
        
    }
   
    
    public enum InvitationStatus {
        DONE, NOUSERSGIVEN, ALREADYAPARTICIPANT, NOTAUTHORIZED, ERRORDURINGCREATIONUSER, ERRORDURINVITATION, INVITATIONSENT, INVALIDUSERID
    };
    public class InvitationResult {
        public InvitationStatus status;
        public List<ToghUserEntity> listThogUserInvited = new ArrayList<>();        
        public List<ParticipantEntity> newParticipants = new ArrayList<>();
        public StringBuilder errorMessage = new StringBuilder();
        public StringBuilder okMessage = new StringBuilder();
    }
    public InvitationResult invite( EventEntity event, Long invitedByUserId, List<Long> listUsersId, String userInvitedEmail, ParticipantRoleEnum role, String message ) {

        MonitorService monitorService = factoryService.getMonitorService();
        InvitationResult invitationResult = new InvitationResult();
        
        EventController eventConductor = new EventController( event );
        if (! eventConductor.isOrganizer(invitedByUserId)) {
            invitationResult.status =InvitationStatus.NOTAUTHORIZED;
            return invitationResult;
        }

        Chrono chronoInvitation = monitorService.startOperation("Invitation");
        ToghUserService userService = factoryService.getToghUserService();
        NotifyService notifyService = factoryService.getNotifyService();
        
        // first, check if this email is a registered Toghuser
        ToghUserEntity invitedByUser = invitedByUserId==null ? null : userService.getUserFromId(invitedByUserId);
       
        // invitation by the email? 
        if (userInvitedEmail!=null && ! userInvitedEmail.trim().isEmpty()) {
            ToghUserEntity toghUser  = userService.getFromEmail( userInvitedEmail );
            if ( toghUser ==null) {
                // this is a real new user, register and invite it to join Togh
                CreationResult creationStatus = userService.inviteNewUser(userInvitedEmail, invitedByUser, event);
                if (creationStatus.userEntity == null) {
                    invitationResult.status = InvitationStatus.ERRORDURINGCREATIONUSER;
                    // This is an internal message here , cant sent back to the user error information 

                    return invitationResult;
                }
                invitationResult.listThogUserInvited.add( creationStatus.userEntity );
            }
            else {
                invitationResult.listThogUserInvited.add( toghUser );
            }
        }
        
        // ---- from the list of ToghUserId
        
        if (listUsersId !=null && ! listUsersId.isEmpty()) {
            for (Object userIdSt : listUsersId) {
                // Javascript will pass a Integer or a String (JS doesn not manage correctly large Long number as Integer)
                Long userId = RestTool.getLong( userIdSt, null);
                ToghUserEntity toghUser =null;
                if (userId!=null)
                    toghUser = userService.getUserFromId( userId );
                if (toghUser  == null) {
                    
                    // caller has supposed to give a valid userId. Stop immediatelly
                    
                    invitationResult.status = InvitationStatus.INVALIDUSERID;
                    // This is an internal message here , cant sent back to the user error information 
                    monitorService.endOperation(chronoInvitation);
                    return invitationResult;
                }
                invitationResult.listThogUserInvited.add( toghUser );

            }
        }
        
        
        // check if one users was already a participant ?
        boolean doubleInvitation=false;
        for (ToghUserEntity toghUser : invitationResult.listThogUserInvited ) {
                ParticipantEntity participant = eventConductor.getParticipant(toghUser.getId() );
                if ( participant !=null) {
                    doubleInvitation = true;
                    invitationResult.errorMessage.append(toghUser.getFirstname()+" "+toghUser.getLastName()+", ");
                }
                else {
                    // send the invitation and register the guy
                    notifyService.notifyNewUserInEvent(toghUser, invitedByUser, event);
                    invitationResult.newParticipants.add( event.addPartipant(toghUser, role, StatusEnum.INVITED ));
                    invitationResult.okMessage.append(toghUser.getFirstname()+" "+toghUser.getLastName()+", ");
                }
        }

        eventRepository.save(event);
        
        
        // status now
        if (invitationResult.listThogUserInvited.isEmpty())
            invitationResult.status = InvitationStatus.NOUSERSGIVEN; 
        else if (doubleInvitation)
            invitationResult.status = InvitationStatus.ALREADYAPARTICIPANT; 
        else
            invitationResult.status = InvitationStatus.INVITATIONSENT;

        monitorService.endOperation(chronoInvitation);
        
        return invitationResult;
    }

    
    
}

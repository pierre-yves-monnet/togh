package com.togh.service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.event.EventController;
import com.togh.repository.EventRepository;

/* ******************************************************************************** */
/*                                                                                  */
/*    EventService, access and manipulate event                                     */
/*                                                                                  */
/* The eventService delegate operation to the com.togh.event.EventController        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class EventService {

    private static final LogEvent eventAccessError = new LogEvent(EventService.class.getName(), 1, Level.APPLICATIONERROR, "Can't access this event", "This event can't be accessed", "Operations is not executed", "Check user and permission");
    private static final LogEvent eventSaveError = new LogEvent(EventService.class.getName(), 2, Level.APPLICATIONERROR, "Can't save this event", "This event can't be saved", "Operations are not executed", "Check Database");

    private Logger logger = Logger.getLogger( EventService.class.getName());
    private final static String logHeader = EventService.class.getSimpleName()+": ";
 
    @Autowired
    FactoryService factoryService;
    
    @Autowired
    private EventRepository eventRepository;


  
    public static class EventOperationResult {
        public EventEntity eventEntity;
        public List<LogEvent> listEvents = new ArrayList<>();
        public Object childEntity = null;
        
        public Long getEventId() {
            return eventEntity !=null ? eventEntity.getId() : null;
        }
        public List<Map<String, Serializable>> getEventsJson() {
            return LogEventFactory.getJson(listEvents);
        }
        
    }
    /**
     * 
     * @param user
     * @return
     */
    public EventOperationResult createEvent( ToghUserEntity toghUser) {
        
        EventEntity event = new EventEntity();
        event.setAuthor( toghUser );
        event.setName("New event");
        event.setDatecreation( LocalDateTime.now( ZoneOffset.UTC ));
        event.touch();
        event.setStatusEvent(StatusEventEnum.INPREPAR);
        event.setTypeEvent(TypeEventEnum.LIMITED);
        event.setDatePolicy(DatePolicyEnum.ONEDATE);
        
        EventController eventController = new EventController( event );
        // let's the conductor create the participant and all needed information
        eventController.completeConsistant();
        eventRepository.save(event);
       
        EventOperationResult eventOperationResult = new EventOperationResult();
        eventOperationResult.eventEntity = event;
        
        return eventOperationResult;
        
    }
    
    public EventOperationResult updateEvent( ToghUserEntity toghUser, EventEntity event, List<Map<String,Object>> listSlab) {
        
        EventController eventConductor = new EventController( event );
        if (! eventConductor.isAccess( toghUser ))
        {
            EventOperationResult eventOperationResult = new EventOperationResult();
            eventOperationResult.listEvents.add( eventAccessError );
            return eventOperationResult;
        }

        EventOperationResult eventOperationResult = eventConductor.update( listSlab);
        
        try {
            eventRepository.save(event);
        }catch(Exception e) {
            eventOperationResult.listEvents.add( new LogEvent(eventSaveError, e, "Save event"));
        }
        
        
        return eventOperationResult;
        
    }
 
 
   public List<EventEntity> getEvents(ToghUserEntity toghUser, String filterEvents) {
       return eventRepository.findEventsUser( toghUser.getId() );       
    }
    
   
   public EventEntity getEventById( Long eventId) {
       if (eventId==null)
           return null;
       EventEntity event =  eventRepository.findByEventId( eventId );
       if (event==null)
           return null;
       EventController eventConductor = new EventController( event );
       
       // check consistant now
       eventConductor.completeConsistant( );
       
       return event; 
       
   }
   
   /**
    * 
    * @param userId
    * @param eventId
    * @return
    */
    public EventEntity getAllowedEventById( ToghUserEntity toghUser, long eventId) {
        EventEntity event =  getEventById( eventId );
        if (event==null)
            return null;
        EventController eventConductor = new EventController( event );
        if (! eventConductor.isAccess( toghUser ))
            return null;
        return event; 
        
    }
   
    
    /**
     * invite
     * 
     *
     */
    public enum InvitationStatus {
        DONE, NOUSERSGIVEN, ALREADYAPARTICIPANT, NOTAUTHORIZED, ERRORDURINGCREATIONUSER, ERRORDURINVITATION, INVITATIONSENT, INVALIDUSERID
    }
    
    public static class InvitationResult {
        public InvitationStatus status;
        public List<ToghUserEntity> listThogUserInvited = new ArrayList<>();        
        public List<ParticipantEntity> newParticipants = new ArrayList<>();
        public StringBuilder errorMessage = new StringBuilder();
        public StringBuilder okMessage = new StringBuilder();
    }
    public InvitationResult invite( EventEntity event, ToghUserEntity invitedByUser, List<Long> listUsersId, String userInvitedEmail, ParticipantRoleEnum role, String message ) {

        EventController eventControler = new EventController( event );
        if (! eventControler.isOrganizer(invitedByUser)) {
            InvitationResult invitationResult = new InvitationResult();
            invitationResult.status =InvitationStatus.NOTAUTHORIZED;
            return invitationResult;
        }

        // this operation is delegated to the evenController
        return  eventControler.invite( event,  invitedByUser, listUsersId,  userInvitedEmail,  role,  message);
        
    }

   
        
    
}

package com.togh.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.togh.entity.EventExpenseEntity;
import com.togh.entity.EventItineraryStepEntity;
import com.togh.entity.EventShoppingListEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.UserEntity;
import com.togh.repository.EventExpenseRepository;
import com.togh.repository.EventItineraryStepRepository;
import com.togh.repository.EventRepository;
import com.togh.repository.EventShoppingListRepository;
import com.togh.repository.EventSurveyRepository;
import com.togh.repository.EventTaskRepository;
import com.togh.service.event.EventController;

/* ******************************************************************************** */
/*                                                                                  */
/* EventService, access and manipulate event */
/*                                                                                  */
/* The eventService delegate operation to the com.togh.event.EventController */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
@Service
public class EventService {

    private static final LogEvent eventAccessError = new LogEvent(EventService.class.getName(), 1, Level.APPLICATIONERROR, "Can't access this event", "This event can't be accessed", "Operations is not executed", "Check user and permission");
    private static final LogEvent eventSaveError = new LogEvent(EventService.class.getName(), 2, Level.APPLICATIONERROR, "Can't save this event", "This event can't be saved", "Operations are not executed", "Check Database");
    private static final LogEvent eventFindEventError = new LogEvent(EventService.class.getName(), 3, Level.ERROR, "Can't get the list of event", "The list of events can't be read", "Operations are not executed, list is empty", "Check Exception");

    private static final LogEvent eventEntityNotFound = new LogEvent(EventService.class.getName(), 4, Level.APPLICATIONERROR, "Entity not found", "The entity requested can't be found, it may be deleted by an another user in the mean time", "Entity is not found, can't be linked in this object", "Check the ID");
    private static final LogEvent eventNoId = new LogEvent(EventService.class.getName(), 5, Level.ERROR, "No ID", "To load an entity, an ID must be give", "Entity is not found, can't be linked in this object", "Check the ID");
    private static final LogEvent eventBadEntity = new LogEvent(EventService.class.getName(), 6, Level.ERROR, "Bad Entity", "This Entity can't be load", "Entity is not found, can't be linked in this object", "Check the Entity");

    private Logger logger = Logger.getLogger(EventService.class.getName());
    private final static String logHeader = EventService.class.getSimpleName() + ": ";

    @Autowired
    FactoryService factoryService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventItineraryStepRepository eventItineraryStepRepository;
    
    @Autowired
    private EventTaskRepository eventTaskRepository;

    @Autowired
    private EventShoppingListRepository eventShoppingListRepository;
    
    @Autowired
    private EventExpenseRepository eventExpenseRepository;
    
    @Autowired
    private EventSurveyRepository surveyRepository;
    
    @Autowired
    private ToghUserService toghUserService;

    
    public static class EventOperationResult {

        public EventEntity eventEntity;
        public List<LogEvent> listLogEvents = new ArrayList<>();
        public List<BaseEntity> listChildEntity = new ArrayList<>();
        
        // in case of Remove, entity are deleted but then we send back the entityId
        public List<Long> listChildEntityId = new ArrayList<>();
        
        public Long getEventId() {
            return eventEntity != null ? eventEntity.getId() : null;
        }

        public List<Map<String, Serializable>> getEventsJson() {
            return LogEventFactory.getJson(listLogEvents);
        }
        public void add(EventOperationResult complement ) {
            this.listLogEvents.addAll( complement.listLogEvents);
            this.listChildEntity.addAll( complement.listChildEntity);
            this.listChildEntityId.addAll( complement.listChildEntityId);
        }
        public void addLogEvent( LogEvent event ) {
            listLogEvents.add( event );
        }
        public void addLogEvents( List<LogEvent> listEvents ) {
            listLogEvents.addAll( listEvents );
        }
        public boolean isError() {
            return LogEventFactory.isError( listLogEvents);
        }
    }

    /**
     * @param user
     * @return
     */
    public EventOperationResult createEvent(ToghUserEntity toghUser, String eventName) {

        EventEntity event = new EventEntity();
        event.setAuthor(toghUser);
        event.setName(eventName);
        event.setStatusEvent(StatusEventEnum.INPREPAR);
        event.setTypeEvent(TypeEventEnum.LIMITED);
        event.setDatePolicy(DatePolicyEnum.ONEDATE);

        EventController eventController = new EventController(this, event);
        // let's the conductor create the participant and all needed information
        eventController.completeConsistant();
        eventRepository.save(event);

        EventOperationResult eventOperationResult = new EventOperationResult();
        eventOperationResult.eventEntity = event;

        return eventOperationResult;

    }

    public static class UpdateContext {
        public ToghUserEntity toghUser;
        public long timeZoneOffset;
    }
    public EventOperationResult updateEvent( EventEntity event, List<Map<String, Object>> listSlab,UpdateContext updateContext) {

        EventController eventConductor = new EventController(this, event);
        if (!eventConductor.isAccess(updateContext.toghUser)) {
            EventOperationResult eventOperationResult = new EventOperationResult();
            eventOperationResult.addLogEvent(eventAccessError);
            return eventOperationResult;
        }

        EventOperationResult eventOperationResult = eventConductor.update(listSlab, updateContext);

        try {
            
            eventRepository.save(event);
        } catch (Exception e) {
            eventOperationResult.listLogEvents.add(new LogEvent(eventSaveError, e, "Save event"));
        }
        eventOperationResult.eventEntity = event;
        return eventOperationResult;

    }

    public class EventResult {

        public List<EventEntity> listEvents;
        public List<LogEvent> listLogEvent = new ArrayList<>();
    }

    public EventResult getEvents(ToghUserEntity toghUser, String filterEvents) {
        EventResult eventResult = new EventResult();
        try {
            eventResult.listEvents = eventRepository.findEventsUser(toghUser.getId());
        } catch (Exception e) {
            // something bad arrived
            logger.severe(logHeader + " Error during finEventsUser toghUser[" + toghUser.getId() + "] :" + e.toString());
            eventResult.listLogEvent.add(new LogEvent(eventFindEventError, e, "User [" + toghUser.getId()));
        }
        return eventResult;
    }

    public EventEntity getEventById(Long eventId) {
        if (eventId == null)
            return null;
        EventEntity event = eventRepository.findByEventId(eventId);
        if (event == null)
            return null;
        EventController eventConductor = new EventController(this, event);

        // check consistant now
        eventConductor.completeConsistant();

        return event;

    }

    /**
     * @param userId
     * @param eventId
     * @return
     */
    public EventEntity getAllowedEventById(ToghUserEntity toghUser, long eventId) {
        EventEntity event = getEventById(eventId);
        if (event == null)
            return null;
        EventController eventConductor = new EventController(this, event);
        if (!eventConductor.isAccess(toghUser))
            return null;
        return event;

    }

    /**
     * invite
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

    public InvitationResult invite(EventEntity event, ToghUserEntity invitedByUser, List<Long> listUsersId, String userInvitedEmail, ParticipantRoleEnum role, String message) {

        EventController eventControler = new EventController(this, event);
        if (!eventControler.isOrganizer(invitedByUser)) {
            InvitationResult invitationResult = new InvitationResult();
            invitationResult.status = InvitationStatus.NOTAUTHORIZED;
            return invitationResult;
        }

        // this operation is delegated to the evenController
        return eventControler.invite(event, invitedByUser, listUsersId, userInvitedEmail, role, message);

    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data ItineraryStep                                                               */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /** Add a task in the event
     * task is saved, then it got an id. Event is not saved.
     * @param event
     * @return
     */
    public EventItineraryStepEntity addItineraryStep(EventEntity event, EventItineraryStepEntity itineraryStep) {        
        eventItineraryStepRepository.save(itineraryStep);
        event.addItineraryStep(itineraryStep);
        return itineraryStep;
    }
    public List<LogEvent> removeItineraryStep(EventEntity event, Long taskId) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        Optional<EventItineraryStepEntity> child = eventItineraryStepRepository.findById(taskId);
        if (child.isPresent()) {
            eventItineraryStepRepository.delete(child.get());
            event.removeItineraryStep( child.get() );
        } else {
            listLogEvent.add( new LogEvent(eventBadEntity, "Can't find itineraryStep "+taskId));
        }

        return listLogEvent;
    }
    
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data ShoppingList                                                               */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
    public EventShoppingListEntity addShoppingList(EventEntity event, EventShoppingListEntity shoppingListEntity) {        
        eventShoppingListRepository.save(shoppingListEntity);
        event.addShoppingList(shoppingListEntity);
        return shoppingListEntity;
    }
    public List<LogEvent> removeShoppingList(EventEntity event, Long shoppingListId) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        Optional<EventShoppingListEntity> child = eventShoppingListRepository.findById(shoppingListId);
        if (child.isPresent()) {
            eventShoppingListRepository.delete(child.get());
            event.removeShoppingList( child.get() );
        } else {
            listLogEvent.add( new LogEvent(eventBadEntity, "Can't find ShoppingId "+shoppingListId));
        }
        return listLogEvent;
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data operation, */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /** Add a task in the event
     * task is saved, then it got an id. Event is not saved.
     * @param event
     * @return
     */
    public EventTaskEntity addTask(EventEntity event, EventTaskEntity task) {
        eventTaskRepository.save(task);
        event.addTask(task);
        return task;
    }
    public List<LogEvent> removeTask(EventEntity event, Long taskId) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        Optional<EventTaskEntity> task = eventTaskRepository.findById(taskId);
        if (task.isPresent()) {
            eventTaskRepository.delete(task.get());
            event.removeTask( task.get() );
        } else {
            listLogEvent.add( new LogEvent(eventBadEntity, "Can't find taskId "+taskId));
        }

        return listLogEvent;
    }
    
    
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data Survey                                                               */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
    public EventSurveyEntity addSurvey(EventEntity event, EventSurveyEntity surveyEntity) {        
        surveyRepository.save(surveyEntity);
        /* bob event.addSurvey(surveyEntity); */
        return surveyEntity;
    }
    public List<LogEvent> removeSurvey(EventEntity event, Long surveyId) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        Optional<EventSurveyEntity> child = surveyRepository.findById(surveyId);
        if (child.isPresent()) {
            surveyRepository.delete(child.get());
            /* bob event.removeSurvey( child.get() );*/
        } else {
            listLogEvent.add( new LogEvent(eventBadEntity, "Can't find SurveyId "+surveyId));
        }
        return listLogEvent;
    }
    
    
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Add child                                                              */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    public BaseEntity add(String nameEntity, UserEntity parentEntity) {
        if ("expense".equalsIgnoreCase(nameEntity) && (parentEntity.acceptExpense())) {
            EventExpenseEntity expense = new EventExpenseEntity();            
            eventExpenseRepository.save(expense);
            parentEntity.setExpense(expense);
           
            return expense;
            
        }
        return null;
    }

    
    /**
     * Load an entity by its class and an ID
     *
     */
    public class LoadEntityResult {
        public BaseEntity entity;
        public List<LogEvent> listLogEvents = new ArrayList<>();
    }
    public LoadEntityResult loadEntity( Class<?> classEntity, Long id) {
        LoadEntityResult loadEntityResult = new LoadEntityResult();
        if (id==null) {
            loadEntityResult.listLogEvents.add( new LogEvent(eventNoId, "Entity["+classEntity.getName()+"]"));
            return loadEntityResult;
        }
        if (ToghUserEntity.class.equals(classEntity))
        {
            loadEntityResult.entity = toghUserService.getUserFromId(id);
        }
        else {
            loadEntityResult.listLogEvents.add( new LogEvent(eventBadEntity, "Entity["+classEntity.getName()+"] ,Id=["+id+"]"));            
        }
        
        
        if (loadEntityResult.entity==null) {
            loadEntityResult.listLogEvents.add( new LogEvent(eventEntityNotFound, "Entity["+classEntity.getName()+"] ,Id=["+id+"]"));
        }
        return loadEntityResult;
    }
    
}

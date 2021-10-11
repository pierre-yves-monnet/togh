/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.EventEntity.SubscriptionEventEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.EventExpenseEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.EventBaseEntity;
import com.togh.repository.EventExpenseRepository;
import com.togh.repository.EventRepository;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventController;
import com.togh.service.event.EventUpdate.Slab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/* ******************************************************************************** */
/*                                                                                  */
/* EventService, access and manipulate event */
/*                                                                                  */
/* The eventService delegate operations to the com.togh.event.EventController */
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
    private static final LogEvent eventBadEntity = new LogEvent(EventService.class.getName(), 6, Level.ERROR, "Bad Entity", "This Entity can't be load", "Entity is not the correct one", "Check the Entity");

    private static final String LOG_HEADER = EventService.class.getSimpleName() + ": ";
    private final Logger logger = Logger.getLogger(EventService.class.getName());

    @Autowired
    FactoryService factoryService;

    @Autowired
    EventFactoryRepository factoryRepository;

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    private EventRepository eventRepository;


    @Autowired
    private EventExpenseRepository eventExpenseRepository;


    @Autowired
    private ToghUserService toghUserService;

    @Autowired
    private NotifyService notifyService;

    /**
     * Update event: update attribut, create new item, delete item. All operations on event are done via the Slab Mechanism, which is a Updater design
     *
     * @param eventEntity   the event to update
     * @param listSlab      the list of Slab, list of operations to apply
     * @param updateContext the update context
     * @return the result of the update
     */
    public EventOperationResult updateEvent(EventEntity eventEntity, List<Slab> listSlab, UpdateContext updateContext) {
        EventController eventController = getEventController(eventEntity);
        updateContext.eventController = eventController;
        if (!eventController.hasAccess(updateContext.toghUser)) {
            EventOperationResult eventOperationResult = new EventOperationResult(eventEntity);
            eventOperationResult.addLogEvent(eventAccessError);
            return eventOperationResult;
        }

        EventOperationResult eventOperationResult = eventController.update(listSlab, updateContext);

        try {
            eventRepository.save(eventEntity);
        } catch (Exception e) {
            eventOperationResult.listLogEvents.add(new LogEvent(eventSaveError, e, "Save event"));
        }
        eventOperationResult.eventEntity = eventEntity;
        return eventOperationResult;
    }

    /**
     * @param toghUserEntity the user
     * @param  eventName
     * @return the EventOperationResult
     */
    public EventOperationResult createEvent(ToghUserEntity toghUserEntity, String eventName) {

        LocalDateTime timeCheck = LocalDateTime.now(ZoneOffset.UTC);
        timeCheck = timeCheck.minusDays(60);

        // Do we access the maximum number of event for this users?
        Long numberOfEvents = eventRepository.countLastEventsUser(toghUserEntity.getId(), ParticipantRoleEnum.OWNER, timeCheck);
        int maximumSubscription = subscriptionService.getMaximumEventsPerMonth(toghUserEntity.getSubscriptionUser());
        if (numberOfEvents >= maximumSubscription) {
            /// reject it
            subscriptionService.registerTouchLimitSubscription(toghUserEntity, LimitReach.CREATIONEVENT);

            EventOperationResult eventOperationResult = new EventOperationResult(null);
            eventOperationResult.limitSubscription = true;
            return eventOperationResult;
        }

        EventEntity eventEntity = new EventEntity();
        eventEntity.setAuthor(toghUserEntity);
        eventEntity.setName(eventName);
        eventEntity.setStatusEvent(StatusEventEnum.INPREPAR);
        eventEntity.setTypeEvent(TypeEventEnum.LIMITED);
        eventEntity.setDatePolicy(DatePolicyEnum.ONEDATE);
        switch (toghUserEntity.getSubscriptionUser()) {
            case PREMIUM:
                eventEntity.setSubscriptionEvent(SubscriptionEventEnum.PREMIUM);
                break;
            case EXCELLENCE:
                eventEntity.setSubscriptionEvent(SubscriptionEventEnum.EXCELLENCE);
                break;
            default:
                eventEntity.setSubscriptionEvent(SubscriptionEventEnum.FREE);
        }

        EventController eventController = getEventController(eventEntity);

        // let's the conductor create the participant and all needed information
        eventController.completeConsistant();
        eventRepository.save(eventEntity);

        return new EventOperationResult(eventEntity);

    }

    /**
     * Send invitations to a list of Togh User, or to a new email
     *
     * @param eventEntity       event from the user is invited
     * @param invitedByToghUser The ToghUser who invite
     * @param listUsersId       List userId to invite. May be empty
     * @param userInvitedEmail  the email user to invite. May be empty
     * @param role              Role proposed in the event
     * @param useMyEmailAsFrom  if true, the expeditor of the message is the invitedByUser email
     * @param message           additional message
     * @return the invitation result
     */
    public InvitationResult invite(EventEntity eventEntity,
                                   ToghUserEntity invitedByToghUser,
                                   List<Long> listUsersId,
                                   String userInvitedEmail,
                                   ParticipantRoleEnum role,
                                   boolean useMyEmailAsFrom,
                                   String message) {

        EventController eventController = getEventController(eventEntity);
        if (!eventController.isOrganizer(invitedByToghUser)) {
            InvitationResult invitationResult = new InvitationResult();
            invitationResult.status = InvitationStatus.NOTAUTHORIZED;
            return invitationResult;
        }

        // this operation is delegated to the evenController
        InvitationResult invitationResult = eventController.invite(eventEntity, invitedByToghUser, listUsersId, userInvitedEmail, role, useMyEmailAsFrom, message);
        try {
            eventRepository.save(eventEntity);
        } catch (Exception e) {
            invitationResult.listLogEvents.add(new LogEvent(eventSaveError, e, "Save event"));
        }
        return invitationResult;
    }

    /**
     * a User access an event: do all need information (notification, etc...)
     *
     * @param toghUserEntity
     */
    public EventOperationResult accessByUser(EventEntity eventEntity, ToghUserEntity toghUserEntity) {
        EventOperationResult eventOperationResult = new EventOperationResult(eventEntity);

        EventController eventController = getEventController(eventEntity);
        boolean isModified = eventController.accessByUser(toghUserEntity);
        if (isModified) {
            try {
                eventRepository.save(eventEntity);
            } catch (Exception e) {
                eventOperationResult.listLogEvents.add(new LogEvent(eventSaveError, e, "Save event"));
            }
        }
        return eventOperationResult;
    }

    /**
     * Entity is given by its name. Then, it is added to the parent, and saved.
     *
     * @param nameEntity   the name of the entity to add
     * @param parentEntity the parentEntity of the entity to add
     * @return the BaseEntity added
     */

    public BaseEntity add(String nameEntity, EventBaseEntity parentEntity) {
        if (EventExpenseEntity.CST_ENTITY_NAME.equalsIgnoreCase(nameEntity) && (parentEntity.acceptExpense())) {
            EventExpenseEntity expense = new EventExpenseEntity();
            eventExpenseRepository.save(expense);
            parentEntity.setExpense(expense);

            return expense;

        }
        return null;
    }

    public class EventResult {

        public List<EventEntity> listEvents;
        public List<LogEvent> listLogEvent = new ArrayList<>();
    }

    /**
     * Get Events
     *
     * @param toghUserEntity
     * @param filterEvents
     * @return
     */
    public EventResult getEvents(ToghUserEntity toghUserEntity, FilterEvents filterEvents) {
        EventResult eventResult = new EventResult();
        try {
            switch (filterEvents) {
                case MYEVENTS:
                    eventResult.listEvents = eventRepository.findMyEventsUser(toghUserEntity.getId());
                    break;
                case ALLEVENTS:
                    eventResult.listEvents = eventRepository.findEventsUser(toghUserEntity.getId());
                    break;
                case MYINVITATIONS:
                    eventResult.listEvents = eventRepository.findEventsUserByStatusParticipant(toghUserEntity.getId(), ParticipantEntity.StatusEnum.INVITED);
                    break;
            }
        } catch (Exception e) {
            // something bad arrived
            logger.severe(LOG_HEADER + " Error during finEventsUser toghUser[" + toghUserEntity.getId() + "] :" + e.toString());
            eventResult.listLogEvent.add(new LogEvent(eventFindEventError, e, "User [" + toghUserEntity.getId()));
        }
        return eventResult;
    }

    public enum FilterEvents {
        MYEVENTS, ALLEVENTS, MYINVITATIONS
    }

    public EventEntity getEventById(Long eventId) {
        if (eventId == null)
            return null;
        EventEntity eventEntity = eventRepository.findByEventId(eventId);
        if (eventEntity == null)
            return null;
        EventController eventController = getEventController(eventEntity);

        // check consistant now
        eventController.completeConsistant();

        return eventEntity;
    }

    /**
     * @param toghUserEntity userEntity
     * @param eventId eventId
     * @return the eventEntity
     */
    public EventEntity getAllowedEventById(ToghUserEntity toghUserEntity, long eventId) {
        EventEntity eventEntity = getEventById(eventId);
        if (eventEntity == null)
            return null;
        EventController eventController = getEventController(eventEntity);
        if (!eventController.hasAccess(toghUserEntity))
            return null;
        return eventEntity;

    }

    
    public ContextAccess getContextAccess(EventEntity eventEntity, ToghUserEntity toghUser ) {
        return EventController.getInstance( eventEntity, factoryService, factoryRepository).getTypeAccess(toghUser);
    }

    
    public Map<String, Object> getMap( EventEntity eventEntity, ToghUserEntity toghUserEntity, Long timezoneOffset) {
        EventController eventController = getEventController(eventEntity);
        return eventEntity.getMap(eventController.getTypeAccess(toghUserEntity), timezoneOffset);
    }

    /**
     * invitation
     */
    public enum InvitationStatus {
        DONE, NOUSERSGIVEN, ALREADYAPARTICIPANT, NOTAUTHORIZED, ERRORDURINGCREATIONUSER, ERRORDURINVITATION, INVITATIONSENT, INVALIDUSERID
    }

    /**
     * return a eventController
     *
     * @param eventEntity the EventEntity we search the controller
     * @return the EventController attached to the entity
     */
    private EventController getEventController(EventEntity eventEntity) {
        return new EventController(eventEntity, factoryService, factoryRepository);
    }

    public static class InvitationResult {

        public InvitationStatus status;
        public final List<ToghUserEntity> listThogUserInvited = new ArrayList<>();
        public final List<ParticipantEntity> newParticipants = new ArrayList<>();
        private final List<ToghUserEntity> errorMessage = new ArrayList<>();
        private final List<ToghUserEntity> errorSendEmail = new ArrayList<>();
        private final List<ToghUserEntity> okMessage = new ArrayList<>();

        public final List<LogEvent> listLogEvents = new ArrayList<>();

        public void addErrorMessage( ToghUserEntity toghUserEntity) {
            errorMessage.add( toghUserEntity);
        }

        public String getErrorMessage() {
            return errorMessage.stream()
                    .map(ToghUserEntity::getLabel)
                    .collect( Collectors.joining( "," ) );
        }

        public void addErrorSendEmail( ToghUserEntity toghUserEntity) {
            errorSendEmail.add( toghUserEntity);
        }

        public String getErrorSendEmail() {
            return errorSendEmail.stream()
                    .map(ToghUserEntity::getLabel)
                    .collect( Collectors.joining( "," ) );
        }

        public void addOkMessage( ToghUserEntity toghUserEntity) {
            okMessage.add(toghUserEntity);
        }

        public String getOkMessage() {
            return okMessage.stream()
                    .map(ToghUserEntity::getLabel)
                    .collect(Collectors.joining(","));
        }
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data ItineraryStep */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * Add a task in the event
     * task is saved, then it got an id. Event is not saved.
     * 
     * @param eventEntity
     * @return
//     */
//    public EventItineraryStepEntity addItineraryStep(EventEntity eventEntity, EventItineraryStepEntity itineraryStep) {
//        eventItineraryStepRepository.save(itineraryStep);
//        eventEntity.addItineraryStep(itineraryStep);
//        return itineraryStep;
//
//    }

    /**
     * removeItineraryStep
     * 
     * @param eventEntity
     * @param taskId
     * @return
     */
//    public List<LogEvent> removeItineraryStep(EventEntity eventEntity, Long taskId) {
//        List<LogEvent> listLogEvent = new ArrayList<>();
//        Optional<EventItineraryStepEntity> child = eventItineraryStepRepository.findById(taskId);
//        if (child.isPresent()) {
//            eventItineraryStepRepository.delete(child.get());
//            eventEntity.removeItineraryStep(child.get());
//        } else {
//            listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find itineraryStep " + taskId));
//        }
//
//        return listLogEvent;
//    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data ShoppingList */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
//    public EventShoppingListEntity addShoppingList(EventEntity eventEntity, EventShoppingListEntity shoppingListEntity) {
//        eventShoppingListRepository.save(shoppingListEntity);
//        eventEntity.addShoppingList(shoppingListEntity);
//        return shoppingListEntity;
//    }

    /**
     * RemoveShoppingList
     * 
     * @param eventEntity
     * @param shoppingListId
     * @return
     */
//    public List<LogEvent> removeShoppingList(EventEntity eventEntity, Long shoppingListId) {
//        List<LogEvent> listLogEvent = new ArrayList<>();
//        Optional<EventShoppingListEntity> child = eventShoppingListRepository.findById(shoppingListId);
//        if (child.isPresent()) {
//            eventShoppingListRepository.delete(child.get());
//            eventEntity.removeShoppingList(child.get());
//        } else {
//            listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find ShoppingId " + shoppingListId));
//        }
//        return listLogEvent;
//    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data operation, */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * Add a task in the event
     * task is saved, then it got an id. Event is not saved.
     *
     * @param eventEntity
     * @return
     */
//    public EventTaskEntity addTask(EventEntity eventEntity, EventTaskEntity task, EventOperationResult eventOperationResult) {
//        EventController eventController = getEventController(eventEntity);
//        eventController.addTask(task, eventOperationResult);
//        return task;
//    }

    /**
     * RemoveTask
     *
     * @param eventEntity
     * @param taskId
     * @return
     */
//    public void removeTask(EventEntity eventEntity, Long taskId, EventOperationResult eventOperationResult) {
//        EventController eventController = getEventController(eventEntity);
//        eventController.removeTaskById(taskId, eventOperationResult);
//
//        /*
//         * List<LogEvent> listLogEvent = new ArrayList<>();
//         * Optional<EventTaskEntity> task = eventTaskRepository.findById(taskId);
//         * if (task.isPresent()) {
//         * EventController eventController = getEventController( eventEntity );
//         * return eventController.removeTask( eventEntity, task );
//         * } else {
//         * listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find taskId " + taskId));
//         * }
//         * return listLogEvent;
//         */
//    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Data Survey */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
//    public EventSurveyEntity addSurvey(EventEntity eventEntity, EventSurveyEntity surveyEntity) {
//        surveyRepository.save(surveyEntity);
//        eventEntity.addSurvey(surveyEntity);
//        return surveyEntity;
//    }
//
//    public EventSurveyChoiceEntity addSurveyChoice(EventEntity eventEntity, EventSurveyEntity surveyEntity, EventSurveyChoiceEntity surveyChoice) {
//        EventController eventControler = getEventController(eventEntity);
//        return eventControler.addSurveyChoice(surveyEntity, surveyChoice);
//        /*
//         * Deported
//         * surveyChoiceRepository.save(surveyChoice);
//         * List<EventSurveyChoiceEntity> choicelist = surveyEntity.getChoicelist();
//         * choicelist.add(surveyChoice);
//         * surveyEntity.setChoicelist(choicelist);
//         * surveyRepository.save(surveyEntity);
//         * return surveyChoice;
//         */
//    }
//
//    public EventSurveyAnswerEntity addSurveyAnswser(EventEntity eventEntity, EventSurveyEntity surveyEntity, EventSurveyAnswerEntity surveyAnswerEntity) {
//        EventController eventControler = getEventController(eventEntity);
//        return eventControler.addSurveyAnswser(surveyEntity, surveyAnswerEntity);
//        /*
//         * Deported
//         * surveyAnswerRepository.save(surveyAnswerEntity);
//         * List<EventSurveyAnswerEntity> answerlist = surveyEntity.getAnswerlist();
//         * answerlist.add(surveyAnswerEntity);
//         * surveyEntity.setAnswerlist(answerlist);
//         * surveyRepository.save(surveyEntity);
//         * return surveyAnswerEntity;
//         */
//    }
//
//    /**
//     * RemoveSurvey
//     * 
//     * @param eventEntity
//     * @param surveyId
//     * @return
//     */
//    public List<LogEvent> removeSurvey(EventEntity eventEntity, Long surveyId) {
//        List<LogEvent> listLogEvent = new ArrayList<>();
//        Optional<EventSurveyEntity> child = surveyRepository.findById(surveyId);
//        if (child.isPresent()) {
//            surveyRepository.delete(child.get());
//        } else {
//            listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find SurveyId " + surveyId));
//        }
//        return listLogEvent;
//    }
//
//    public List<LogEvent> removeSurveyChoice(EventEntity eventEntity, EventSurveyEntity surveyEntity, Long choiceId) {
//        EventController eventControler = getEventController(eventEntity);
//        return eventControler.removeSurveyChoice(surveyEntity, choiceId);
//        /*
//         * Deported
//         * List<LogEvent> listLogEvent = new ArrayList<>();
//         * Optional<EventSurveyChoiceEntity> choice = surveyChoiceRepository.findById(choiceId);
//         * if (choice.isPresent()) {
//         * surveyChoiceRepository.delete(choice.get());
//         * List<EventSurveyChoiceEntity> choicelist = surveyEntity.getChoicelist();
//         * choicelist.remove(choice.get());
//         * surveyEntity.setChoicelist(choicelist);
//         * } else {
//         * listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find choiceId[" + choiceId + "]"));
//         * }
//         * return listLogEvent;
//         */
//    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Chat operation */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
//    public EventGroupChatEntity addGroupChat(EventEntity eventEntity, EventGroupChatEntity groupChatEntity) {
//        eventGroupChatRepository.save(groupChatEntity);
//        eventEntity.addGroupChat(groupChatEntity);
//        return groupChatEntity;
//    }
//
//    public EventChatEntity addChatInGroup(EventEntity eventEntity, EventGroupChatEntity groupChatEntity, EventChatEntity chatEntity) {
//        EventController eventControler = getEventController(eventEntity);
//        return eventControler.addChatInGroup(groupChatEntity, chatEntity);
//        /* Deported eventChatRepository.save(chatEntity);
//        groupChatEntity.addChat(chatEntity);
//        return chatEntity;
//        */
//    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Add child */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * Status of operation
     */
    public static class EventOperationResult {
        private static final String LOG_HEADER = EventOperationResult.class.getSimpleName() + ": ";
        public final List<LogEvent> listLogEvents = new ArrayList<>();
        public final List<BaseEntity> listChildEntity = new ArrayList<>();
        // in case of Remove, entity are deleted but then we send back the entityId
        public final List<Long> listChildEntityId = new ArrayList<>();
        private final Logger logger = Logger.getLogger(EventOperationResult.class.getName());
        public EventEntity eventEntity;
        public boolean limitSubscription = false;
        public boolean reachTheLimit = false;

        public EventOperationResult(EventEntity eventEntity) {
            this.eventEntity = eventEntity;
        }

        public Long getEventId() {
            return eventEntity != null ? eventEntity.getId() : null;
        }

        public List<Map<String, Serializable>> getEventsJson() {
            return LogEventFactory.getJson(listLogEvents);
        }

        public void add(EventOperationResult complement) {
            this.listLogEvents.addAll(complement.listLogEvents);
            this.listChildEntity.addAll(complement.listChildEntity);
            this.listChildEntityId.addAll(complement.listChildEntityId);
        }

        public void addLogEvent(LogEvent event) {
            if (event.isError())
                logger.severe(LOG_HEADER + event);
            listLogEvents.add(event);
        }

        public void addLogEvents(List<LogEvent> listEvents) {
            listLogEvents.addAll(listEvents);
        }

        public boolean isError() {
            return LogEventFactory.isError(listLogEvents);
        }
    }

    /**
     * Load an entity by its class and an ID
     */
    public class LoadEntityResult {

        public BaseEntity entity;
        public List<LogEvent> listLogEvents = new ArrayList<>();
    }

    public LoadEntityResult loadEntity(Class<?> classEntity, Long id) {
        LoadEntityResult loadEntityResult = new LoadEntityResult();
        if (id == null) {
            loadEntityResult.listLogEvents.add(new LogEvent(eventNoId, "Entity[" + classEntity.getName() + "]"));
            return loadEntityResult;
        }
        if (ToghUserEntity.class.equals(classEntity)) {
            loadEntityResult.entity = toghUserService.getUserFromId(id);
        } else {
            loadEntityResult.listLogEvents.add(new LogEvent(eventBadEntity, "Entity[" + classEntity.getName() + "] ,Id=[" + id + "]"));
        }

        if (loadEntityResult.entity == null) {
            loadEntityResult.listLogEvents.add(new LogEvent(eventEntityNotFound, "Entity[" + classEntity.getName() + "] ,Id=[" + id + "]"));
        }
        return loadEntityResult;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Close old events */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * Close old events
     */
    public List<EventEntity> closeOldEvents(boolean doTheOperation) {
        LocalDateTime timeCheck = LocalDateTime.now(ZoneOffset.UTC);

        // modified last than 2 H? Keep it open.
        LocalDateTime timeGrace = LocalDateTime.now(ZoneOffset.UTC);
        timeGrace = timeGrace.minusMinutes(120);

        List<EventEntity> listEventsToClose = eventRepository.findOldEvents(timeCheck, timeGrace, StatusEventEnum.CLOSED, PageRequest.of(0, 1000));
        if (doTheOperation) {
            for (EventEntity eventEntity : listEventsToClose) {
                StatusEventEnum oldStatus = eventEntity.getStatusEvent();
                eventEntity.setStatusEvent(StatusEventEnum.CLOSED);
                eventRepository.save(eventEntity);
                notifyService.notifyEventChangeStatus(eventEntity, oldStatus);
            }
        }
        return listEventsToClose;
    }

    /**
     * Close old events
     */
    public List<EventEntity> purgeOldEvents(boolean doTheOperation) {

        // modified last than 2 H? Keep it open.
        LocalDateTime timeGrace = LocalDateTime.now(ZoneOffset.UTC);
        timeGrace = timeGrace.minusDays(30);

        List<EventEntity> listEventsToClose = eventRepository.findEventsToPurge(timeGrace, StatusEventEnum.CLOSED, PageRequest.of(0, 1000));
        if (doTheOperation) {
            for (EventEntity eventEntity : listEventsToClose) {
                eventRepository.delete(eventEntity);
                notifyService.notifyEventPurge(eventEntity);
            }
        }
        return listEventsToClose;
    }

    /**
     * UpdateContext class, to pass context to the different operations
     */
    public static class UpdateContext {

        private final ToghUserEntity toghUser;
        private final long timezoneOffset;
        private final FactoryService factoryService;
        private final EventEntity eventEntity;

        public EventController eventController = null;

        public UpdateContext(ToghUserEntity toghUser,
                             long timezoneOffset,
                             FactoryService factoryService,
                             EventEntity eventEntity) {
            this.toghUser = toghUser;
            this.timezoneOffset = timezoneOffset;
            this.factoryService = factoryService;
            this.eventEntity = eventEntity;
        }

        public ToghUserEntity getToghUser() {
            return toghUser;
        }

        public long getTimezoneOffset() {
            return timezoneOffset;
        }

        public FactoryService getFactoryService() {
            return factoryService;
        }

        public EventEntity getEventEntity() {
            return eventEntity;
        }

        public EventController getEventController() {
            return eventController;
        }

        public void setEventController(EventController eventController) {
            this.eventController = eventController;
        }

    }

}

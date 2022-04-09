/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.engine.chrono.ChronoSet;
import com.togh.engine.chrono.Chronometer;
import com.togh.engine.logevent.LogEvent;
import com.togh.engine.tool.JpaTool;
import com.togh.entity.*;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.EventBaseEntity;
import com.togh.serialization.FactorySerializer;
import com.togh.service.*;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.event.EventUpdate.Slab;
import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/* ******************************************************************************** */
/*                                                                                  */
/* EventController, */
/*                                                                                  */
/* Control what's happen on an event. Pilot all operations.
 * EventController is created for one EventEntity */
/*
 * to help on operation, a list of Controller exist for each component.
 * /*
 */
/*                                                                                  */
/* ******************************************************************************** */
public class EventController {

    private static final Logger logger = Logger.getLogger(EventController.class.getName());
    private static final String LOG_HEADER = EventController.class.getSimpleName() + ": ";

    private final FactoryService factoryService;
    private final EventFactoryRepository factoryRepository;
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Mutex exclusion                                                                  */
    /* If the code want to make an exclusion on the entity, this return a String,
     * and it's possible to synchronize on this string.
     * Note: in a multi nodes environment, make a synchronized is no sense, it's better
     * to rely on constraints in the database                                            */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
    private static final ConcurrentReferenceHashMap<Long, String> concurrentXMutex = new ConcurrentReferenceHashMap<>();

    private final EventEntity eventEntity;

    /**
     * Declare all friend controller
     */
    private final EventTaskController eventTaskController;
    private final EventItineraryController eventItineraryController;
    private final EventShoppingController eventShoppingController;
    private final EventSurveyController eventSurveyController;
    private final EventSurveyChoiceController eventSurveyChoiceControllerList;
    private final EventSurveyAnswerController eventSurveyAnswerController;
    private final EventGroupChatController eventGroupChatController;
    private final EventChatController eventChatController;
    private final EventExpenseController eventExpenseController;
    private final EventGameController eventGameController;
    // Note: EventGameParticipantController and EventGameTruthOrLieController are generated
    // by the eventGameController: it may have multiple games in the event
    private final FactorySerializer factorySerializer;

    /**
     * Keep all Repository
     * Default Constructor.
     *
     * @param eventEntity       the eventEntity attached to the controller
     * @param factoryService    the factory service
     * @param factoryRepository the factory repository
     */

    public EventController(EventEntity eventEntity, FactoryService factoryService, EventFactoryRepository factoryRepository, FactorySerializer factorySerializer) {
        this.eventEntity = eventEntity;
        this.factoryService = factoryService;
        this.factoryRepository = factoryRepository;
        this.factorySerializer = factorySerializer;
        eventTaskController = new EventTaskController(this, eventEntity);
        eventItineraryController = new EventItineraryController(this, eventEntity);
        eventShoppingController = new EventShoppingController(this, eventEntity);

        eventSurveyController = new EventSurveyController(this, eventEntity);
        eventSurveyChoiceControllerList = new EventSurveyChoiceController(this, eventSurveyController, eventEntity);
        eventSurveyAnswerController = new EventSurveyAnswerController(this, eventSurveyController, eventEntity);

        eventGroupChatController = new EventGroupChatController(this, eventEntity);
        eventChatController = new EventChatController(this, eventGroupChatController, eventEntity);
        eventExpenseController = new EventExpenseController(this, eventEntity);
        eventGameController = new EventGameController(this, eventEntity);

    }

    public EventEntity getEvent() {
        return eventEntity;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* operations on event */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * Build an instance from an event
     *
     * @param event             event to build the controller on
     * @param factoryService    factory service
     * @param factoryRepository factory repository
     * @param factorySerializer factory serializer to get user label when needed
     * @return eventController
     */
    public static EventController getInstance(EventEntity event,
                                              FactoryService factoryService,
                                              EventFactoryRepository factoryRepository,
                                              FactorySerializer factorySerializer) {
        return new EventController(event, factoryService, factoryRepository, factorySerializer);
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Presentation */
    /*                                                                                  */
    /* ******************************************************************************** */
    public EventPresentation getEventPresentation() {
        return new EventPresentation(this, factoryService);
    }

    public EventExpenseController getEventExpenseController() {
        return eventExpenseController;
    }

    public EventSurveyChoiceController getEventControllerSurveyChoiceList() {
        return eventSurveyChoiceControllerList;
    }

    public EventSurveyAnswerController getEventControllerSurveyAnswerList() {
        return eventSurveyAnswerController;
    }

    public EventGroupChatController getEventGroupChatController() {
        return eventGroupChatController;
    }

    public EventChatController getEventChatController() {
        return eventChatController;
    }

    public EventGameController getEventGameController() {
        return eventGameController;
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Authorisation */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * isRegisteredParticipant. if this user is part of this event?
     *
     * @param toghUser user
     * @return true if the user can access this event
     */
    public boolean hasAccess(ToghUserEntity toghUser) {
        if (eventEntity.getTypeEvent() == TypeEventEnum.OPEN)
            return true;
        return getParticipant(toghUser) != null;
    }

    /**
     * User must be the author, or a participant, or should be invited
     *
     * @param toghUser the togh user
     * @return true if the participant is active
     */
    public boolean isActiveParticipant(ToghUserEntity toghUser) {
        ParticipantEntity participant = getParticipant(toghUser);
        if (participant == null)
            return false;
        if (ParticipantRoleEnum.OWNER.equals(participant.getRole())
                || ParticipantRoleEnum.ORGANIZER.equals(participant.getRole())
                || ParticipantRoleEnum.PARTICIPANT.equals(participant.getRole())) {
            return StatusEnum.ACTIF.equals(participant.getStatus());
        }
        return false;
    }

    /**
     * is this user an organizer? Some operation, like invitation, is allowed only for organizer
     *
     * @param toghUser the toghUser
     * @return true is this user is an organizer of the event
     */
    public boolean isOrganizer(ToghUserEntity toghUser) {
        ParticipantEntity participant = getParticipant(toghUser);
        if (participant == null)
            return false;
        return participant.getRole().equals(ParticipantRoleEnum.OWNER) || participant.getRole().equals(ParticipantRoleEnum.ORGANIZER);
    }

    /**
     * Return the owner of the event
     *
     * @return the owner of the event
     */
    public ToghUserEntity getOwner() {
        for (ParticipantEntity participant : eventEntity.getParticipantList()) {
            if (participant.getRole().equals(ParticipantRoleEnum.OWNER))
                return participant.getUser();
        }
        return null;
    }

    /**
     * get the role of this userId in the event. Return null if the user does not have any participation
     *
     * @param toghUser the toghUser
     * @return a ParticipantRoleEnum status
     */
    public ParticipantRoleEnum getRoleEnum(ToghUserEntity toghUser) {
        ParticipantEntity participant = getParticipant(toghUser);
        return (participant == null ? null : participant.getRole());
    }

    /**
     * @param toghUser the toghUser
     * @return a Participant Entity
     */
    public ParticipantEntity getParticipant(ToghUserEntity toghUser) {
        for (ParticipantEntity participant : eventEntity.getParticipantList()) {
            if (participant.getUser() != null && participant.getUser().getId().equals(toghUser.getId()))
                return participant;
        }
        return null;
    }

    /**
     * Return the participant from it's ID
     *
     * @param participantId the participant ID
     * @return the participant Entity. null if no participant is found with this ID
     */
    public ParticipantEntity getParticipantById(long participantId) {
        for (ParticipantEntity participant : eventEntity.getParticipantList()) {
            if (participant.getId().equals(participantId))
                return participant;
        }
        return null;
    }


    /**
     * true if the event is a Secret Event
     *
     * @return true when the event is a secret event
     */
    public boolean isSecretEvent() {
        return false;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Invitation */
    /*                                                                                  */
    /* ******************************************************************************** */

    public static String getMutex(Long eventId) {
        concurrentXMutex.putIfAbsent(eventId, eventId.toString());
        return concurrentXMutex.get(eventId);
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Operations */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * A user access to the event.
     * We can deal with operation. For example, if the user is INVITED, then we moved to ACTIF
     *
     * @param toghUserEntity toghUserEntity
     * @return true if the event were modified, and need to be saved
     */
    public boolean accessByUser(ToghUserEntity toghUserEntity) {
        boolean isModified = false;
        ParticipantEntity participant = getParticipant(toghUserEntity);
        if (participant != null && participant.getStatus() == StatusEnum.INVITED) {
            participant.setStatus(StatusEnum.ACTIF);
            isModified = true;

        }
        return isModified;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Update */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * This method check the event consistant. It may be read from the database, and this version change some item.
     * Or it can be just created, and we want to have the default information
     */
    public List<LogEvent> completeConsistant() {
        List<LogEvent> listLogEvents = new ArrayList<>();
        ChronoSet chronoSet = new ChronoSet();
        Chronometer chronoConsistency = chronoSet.getChronometer("eventconsistancy");
        chronoConsistency.start();

        // the author is a Organizer participant
        boolean authorIsReferenced = false;
        for (ParticipantEntity participant : eventEntity.getParticipantList()) {
            if (participant.getUser() != null && participant.getUser().getId().equals(eventEntity.getAuthorId())) {
                authorIsReferenced = true;
                participant.setRole(ParticipantRoleEnum.OWNER);
            }
        }
        if (!authorIsReferenced) {
            // if the user does not exist, this is an issue.... ==> ToghEvent
            eventEntity.addParticipant(eventEntity.getAuthor(), ParticipantRoleEnum.OWNER, StatusEnum.ACTIF);
        }

        // a date policy must be set
        if (eventEntity.getDatePolicy() == null)
            eventEntity.setDatePolicy(DatePolicyEnum.ONEDATE);

        // Preferences must be ready (old event may not have a preference entity)
        if (eventEntity.getPreferences() == null) {
            EventPreferencesEntity preferencesEntity = new EventPreferencesEntity();
            preferencesEntity.setCurrencyCode("USD");
            preferencesEntity.setAccessChat(true);
            preferencesEntity.setAccessItinerary(false);
            preferencesEntity.setAccessTasks(false);
            preferencesEntity.setAccessBring(true);
            preferencesEntity.setAccessSurveys(false);
            preferencesEntity.setAccessLocalisation(true);
            preferencesEntity.setAccessGames(false);
            preferencesEntity.setAccessPhotos(false);
            preferencesEntity.setAccessExpenses(false);
            preferencesEntity.setAccessBudget(false);
            eventEntity.setPreferences(preferencesEntity);
        }

        // itinerary: must be in the date
        listLogEvents.addAll(eventItineraryController.checkItinerary());


        // check game
        for (EventGameEntity eventGameEntity : eventEntity.getGameList()) {
            eventGameController.completeConsistant(eventGameEntity);
        }

        chronoConsistency.stopAndLog(100);

        return listLogEvents;
    }

    protected EventService getEventService() {
        return factoryService.getEventService();
    }

    /**
     * send an invitation
     *
     * @param eventEntity       the eventEntity
     * @param invitedByToghUser the toghUser who sent the invitation
     * @param listUsersId       list of ToghUserId to invites
     * @param userInvitedEmail  list of email : there are not yet toghUser
     * @param role              role in this event
     * @param useMyEmailAsFrom  if true, the Email "From" used in the message is the InvitedByToghUser email
     * @param message           Message to come with the invitation
     * @return the invitation Result
     */
    public InvitationResult invite(EventEntity eventEntity,
                                   ToghUserEntity invitedByToghUser,
                                   List<Long> listUsersId,
                                   String userInvitedEmail,
                                   ParticipantRoleEnum role,
                                   boolean useMyEmailAsFrom,
                                   String subject,
                                   String message) {
        EventInvitation eventInvitation = new EventInvitation(this, factoryService);
        return eventInvitation.invite(eventEntity, invitedByToghUser, listUsersId, userInvitedEmail, role, useMyEmailAsFrom, subject, message, factorySerializer);
    }

    protected NotifyService getNotifyService() {
        return factoryService.getNotifyService();
    }


    protected EventFactoryRepository getFactoryRepository() {
        return factoryRepository;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Factory of controller */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */


    /**
     * Factory of EventController. According to the SlabOperation, created the correct event Controller/
     *
     * @param slab the operation to realize
     * @return the status
     */
    protected EventAbsChildController getEventControllerFromSlabOperation(Slab slab) {
        if (EventTaskEntity.CST_SLABOPERATION_TASKLIST.equals(slab.attributName)) {
            return eventTaskController;

        } else if (EventItineraryStepEntity.CST_SLABOPERATION_ITINERARYSTEPLIST.equals(slab.attributName)) {
            return eventItineraryController;

        } else if (EventShoppingListEntity.CST_SLABOPERATION_SHOPPINGLIST.equals(slab.attributName)) {
            return eventShoppingController;

        } else if (EventSurveyEntity.CST_SLABOPERATION_SURVEYLIST.equals(slab.attributName)) {
            return eventSurveyController;

        } else if (EventSurveyChoiceEntity.CST_SLABOPERATION_CHOICELIST.equals(slab.attributName)) {
            return eventSurveyChoiceControllerList;

        } else if (EventSurveyAnswerEntity.CST_SLABOPERATION_ANSWERLIST.equals(slab.attributName)) {
            return eventSurveyAnswerController;

        } else if (EventGroupChatEntity.SLABOPERATION_GROUPCHATLIST.equals(slab.attributName)) {
            return eventGroupChatController;

        } else if (EventChatEntity.CST_SLABOPERATION_CHAT.equals(slab.attributName)) {
            return eventChatController;
        } else if (EventGameEntity.CST_SLABOPERATION_GAMELIST.equals(slab.attributName)) {
            return eventGameController;
        }
        return null;
    }

    /**
     * Localise the BaseEntity according the localisation. Localisation is a string like "/tasklist/1"
     *
     * @param baseEntity   Base Entity (root of the localisation
     * @param localisation localisation, example /survey/12/subject
     * @return the entity pointed by the research
     */
    @SuppressWarnings("unchecked")
    protected BaseEntity localise(@Nonnull BaseEntity baseEntity, @Nonnull String localisation) {

        // source is <name>/id/ <<something else 
        if (localisation.isEmpty())
            return baseEntity;
        StringTokenizer stLocalisation = new StringTokenizer(localisation, "/");
        BaseEntity indexEntity = baseEntity;
        try {
            while (stLocalisation.hasMoreTokens()) {
                String nameEntity = stLocalisation.nextToken();

                Method method = JpaTool.searchMethodByName(indexEntity, nameEntity);
                if (method == null) {
                    logger.severe(LOG_HEADER + "Can't localise [" + nameEntity + "] in [" + indexEntity + "]");
                    return null;
                }


                // get the object
                Object getObject = method.invoke(indexEntity);
                if (getObject instanceof List) {
                    // then the idEntity take the sens
                    String idEntity = stLocalisation.hasMoreTokens() ? stLocalisation.nextToken() : null;
                    Long idEntityLong = idEntity == null ? null : Long.valueOf(idEntity);
                    List<BaseEntity> listChildrenEntity = (List<BaseEntity>) getObject;
                    BaseEntity childEntityById = null;
                    for (BaseEntity child : listChildrenEntity) {
                        if (child.getId().equals(idEntityLong)) {
                            childEntityById = child;
                            break;
                        }
                    }
                    if (childEntityById == null)
                        return null;
                    indexEntity = childEntityById;
                } else if (getObject instanceof BaseEntity)
                    indexEntity = (BaseEntity) getObject;
                else if (getObject == null) {
                    // time to add this object

                    indexEntity = getEventService().add(nameEntity, (EventBaseEntity) indexEntity);
                    if (indexEntity == null)
                        return null;
                }

            }
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't localise item [" + localisation + "] currentIndexItem[" + indexEntity.getClass().getName() + "]");
            return null;
        }
        return indexEntity;
    }

    /**
     * Update the event. All update are done via the Slab objects
     *
     * @param listSlab      List of operations (list of Slob)
     * @param updateContext Context of update
     * @return the result of operation
     */
    public EventOperationResult update(List<Slab> listSlab, UpdateContext updateContext) {
        EventUpdate eventUpdate = new EventUpdate(this);

        EventOperationResult eventOperationResult = eventUpdate.update(listSlab, updateContext);
        eventOperationResult.listLogEvents.addAll(completeConsistant());

        return eventOperationResult;
    }

    protected ToghUserService getUserService() {
        return factoryService.getToghUserService();
    }
}

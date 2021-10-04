/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.engine.tool.JpaTool;
import com.togh.entity.*;
import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.EventBaseEntity;
import com.togh.service.EventFactoryRepository;
import com.togh.service.EventService;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.FactoryService;
import com.togh.service.event.EventUpdate.Slab;

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

    private final EventEntity eventEntity;

    /**
     * Declare all friend controller
     */
    private final EventControllerTask eventControllerTask;
    private final EventControllerItinerary eventControllerItinerary;
    private final EventControllerShopping eventControllerShopping;
    private final EventControllerSurvey eventControllerSurvey;
    private final eventControllerSurveyChoiceList eventControllerSurveyChoiceList;
    private final EventControllerSurveyAnswerList eventControllerSurveyAnswerList;
    private final EventControllerGroupChat eventControllerGroupChat;
    private final EventControllerChat eventControllerChat;

    /**
     * Keep all Repository
     * Default Constructor.
     *
     * @param eventEntity       the eventEntity attached to the controller
     * @param factoryService    the factory service
     * @param factoryRepository the factory repository
     */

    public EventController(EventEntity eventEntity, FactoryService factoryService, EventFactoryRepository factoryRepository) {
        this.eventEntity = eventEntity;
        this.factoryService = factoryService;
        this.factoryRepository = factoryRepository;
        eventControllerTask = new EventControllerTask(this, eventEntity);
        eventControllerItinerary = new EventControllerItinerary(this, eventEntity);
        eventControllerShopping = new EventControllerShopping(this, eventEntity);

        eventControllerSurvey = new EventControllerSurvey(this, eventEntity);
        eventControllerSurveyChoiceList = new eventControllerSurveyChoiceList(this, eventControllerSurvey, eventEntity);
        eventControllerSurveyAnswerList = new EventControllerSurveyAnswerList(this, eventControllerSurvey, eventEntity);

        eventControllerGroupChat = new EventControllerGroupChat(this, eventEntity);
        eventControllerChat = new EventControllerChat(this, eventControllerGroupChat, eventEntity);
    }

    public static EventController getInstance(EventEntity event, FactoryService factoryService, EventFactoryRepository factoryRepository) {
        return new EventController(event, factoryService, factoryRepository);
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
     * This method check the event consistant. It may be read from the database, and this version change some item.
     * Or it can be just created, and we want to have the default information
     */
    public List<Slab> completeConsistant() {
        List<Slab> listSlab = new ArrayList<>();

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
            eventEntity.addPartipant(eventEntity.getAuthor(), ParticipantRoleEnum.OWNER, StatusEnum.ACTIF);
        }

        // a date policy must be set
        if (eventEntity.getDatePolicy() == null)
            eventEntity.setDatePolicy(DatePolicyEnum.ONEDATE);

        // itinerary: must be in the date
        listSlab.addAll(eventControllerItinerary.checkItinerary());

        return listSlab;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Presentation */
    /*                                                                                  */
    /* ******************************************************************************** */
    public EventPresentation getEventPresentation() {
        return new EventPresentation(this, factoryService);
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
     * @return
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
     * @return
     */
    public ParticipantRoleEnum getRoleEnum(ToghUserEntity toghUser) {
        ParticipantEntity participant = getParticipant(toghUser);
        return (participant == null ? null : participant.getRole());
    }

    /**
     * @param toghUser the toghUser
     * @return
     */
    public ParticipantEntity getParticipant(ToghUserEntity toghUser) {
        for (ParticipantEntity participant : eventEntity.getParticipantList()) {
            if (participant.getUser() != null && participant.getUser().getId().equals(toghUser.getId()))
                return participant;
        }
        return null;
    }

    /**
     * According to the user, and the type of event, the ContextAccess is calculated
     *
     * @param toghUser the toghUser
     * @return
     */
    public ContextAccess getTypeAccess(ToghUserEntity toghUser) {
        // event is public : so show onkly what you want to show to public
        if (eventEntity.getTypeEvent() == TypeEventEnum.OPEN)
            return ContextAccess.PUBLICACCESS;
        // event is secret : hide all at maximum
        if (eventEntity.getTypeEvent() == TypeEventEnum.SECRET)
            return ContextAccess.SECRETACCESS;

        ParticipantEntity participant = getParticipant(toghUser);
        if (eventEntity.getTypeEvent() == TypeEventEnum.OPENCONF) {
            // the user is not accepted : show the minimum.
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == StatusEnum.ACTIF)
                return ContextAccess.PUBLICACCESS;
            // user left, or wait for the confirmation 
            return ContextAccess.SECRETACCESS;
        }
        if (eventEntity.getTypeEvent() == TypeEventEnum.LIMITED) {
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
    /* Invitation */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * send an invitation
     *
     * @param eventEntity       the eventEntity
     * @param invitedByToghUser the togfUser who sent the invitation
     * @param listUsersId       list of ToghUserId to invites
     * @param userInvitedEmail  list of email : there are not yet toghUser
     * @param role              role in this event
     * @param useMyEmailAsFrom  if true, the From used in the message is the InvitedByToghUser email
     * @param message           Message to come with the invitation
     * @return
     */
    public InvitationResult invite(EventEntity eventEntity,
                                   ToghUserEntity invitedByToghUser,
                                   List<Long> listUsersId,
                                   String userInvitedEmail,
                                   ParticipantRoleEnum role,
                                   boolean useMyEmailAsFrom,
                                   String message) {
        EventInvitation eventInvitation = new EventInvitation(this, factoryService);
        return eventInvitation.invite(eventEntity, invitedByToghUser, listUsersId, userInvitedEmail, role, useMyEmailAsFrom, message);
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
     * Update the event. All update are done via the Slab objects
     *
     * @param listSlab
     * @param updateContext
     * @return
     */
    public EventOperationResult update(List<Slab> listSlab, UpdateContext updateContext) {
        EventUpdate eventUpdate = new EventUpdate(this);

        EventOperationResult eventOperationResult = eventUpdate.update(listSlab, updateContext);
        List<Slab> listComplementSlab = completeConsistant();
        eventOperationResult.add(eventUpdate.update(listComplementSlab, updateContext));
        return eventOperationResult;
    }

    protected EventService getEventService() {
        return factoryService.getEventService();
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
    protected EventControllerAbsChild getEventControllerFromSlabOperation(Slab slab) {
        if (EventTaskEntity.CST_SLABOPERATION_TASKLIST.equals(slab.attributName)) {
            return eventControllerTask;

        } else if (EventItineraryStepEntity.CST_SLABOPERATION_ITINERARYSTEPLIST.equals(slab.attributName)) {
            return eventControllerItinerary;

        } else if (EventShoppingListEntity.CST_SLABOPERATION_SHOPPINGLIST.equals(slab.attributName)) {
            return eventControllerShopping;

        } else if (EventSurveyEntity.CST_SLABOPERATION_SURVEYLIST.equals(slab.attributName)) {
            return eventControllerSurvey;

        } else if (EventSurveyChoiceEntity.CST_SLABOPERATION_CHOICELIST.equals(slab.attributName)) {
            return eventControllerSurveyChoiceList;

        } else if (EventSurveyAnswerEntity.CST_SLABOPERATION_ANSWERLIST.equals(slab.attributName)) {
            return eventControllerSurveyAnswerList;

        } else if (EventGroupChatEntity.CST_SLABOPERATION_GROUPCHATLIST.equals(slab.attributName)) {
            return eventControllerGroupChat;

        } else if (EventChatEntity.CST_SLABOPERATION_CHAT.equals(slab.attributName)) {
            return eventControllerChat;
        }
        return null;
    }

    /**
     * Localise the BaseEntity according the localisation. Localisation is a string like "/tasklist/1"
     *
     * @param baseEntity
     * @param localisation
     * @return
     */
    @SuppressWarnings("unchecked")
    protected BaseEntity localise(BaseEntity baseEntity, String localisation) {

        // source is <name>/id/ <<something else 
        if (localisation.isEmpty())
            return baseEntity;
        StringTokenizer stLocalisation = new StringTokenizer(localisation, "/");
        BaseEntity indexEntity = baseEntity;
        try {
            while (stLocalisation.hasMoreTokens()) {
                String nameEntity = stLocalisation.nextToken();

                Method method = JpaTool.searchMethodByName(indexEntity, nameEntity);
                if (method == null)
                    return null;

                // get the object
                Object getObject = method.invoke(indexEntity);
                if (getObject instanceof List) {
                    // then the idEntity take the sens
                    String idEntity = stLocalisation.hasMoreTokens() ? stLocalisation.nextToken() : null;
                    Long idEntityLong = Long.valueOf(idEntity);
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
}

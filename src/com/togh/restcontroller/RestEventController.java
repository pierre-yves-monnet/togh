/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;


import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.AdditionalInformationEvent;
import com.togh.entity.EventGroupChatEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.access.EventAccessGrantor;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.serialization.BaseSerializer;
import com.togh.serialization.EventSerializer;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.service.EventFactoryRepository;
import com.togh.service.EventService;
import com.togh.service.EventService.*;
import com.togh.service.FactoryService;
import com.togh.service.event.EventController;
import com.togh.service.event.EventUpdate.Slab;
import com.togh.tool.ToolCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestEventController */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestEventController {

  @Autowired
  EventFactoryRepository factoryRepository;
  @Autowired
  EventService eventService;
  @Autowired
  private FactoryService factoryService;
  @Autowired
  private FactorySerializer factorySerializer;
  @Autowired
  private FactoryUpdateGrantor factoryUpdateGrantor;

  /**
   * Create a list of Slab from a Map
   *
   * @param listSlabMap list Slap from the REST call
   * @return the list of Slab from the Rest Call
   */
  protected static List<Slab> getListSlab(List<Map<String, Object>> listSlabMap) {
    List<Slab> listSlab = new ArrayList<>();
    for (Map<String, Object> recordSlab : listSlabMap) {
      listSlab.add(new Slab(recordSlab));
    }
    return listSlab;
  }

  /**
   * @param filterEvents     filterEvents
   * @param timezoneOffset   timezoneOffset of the browser
   * @param withParticipants with the participants of the event
   * @param connectionStamp  Information on the connected user
   * @return a Map of Events
   */
  @CrossOrigin
  @GetMapping("/api/event/list")
  public Map<String, Object> events(@RequestParam(RestJsonConstants.CST_FILTER_EVENTS) String filterEvents,
                                    @RequestParam(name = RestJsonConstants.CST_TIMEZONEOFFSET, required = false) Long timezoneOffset,
                                    @RequestParam(name = "withParticipants", required = false) Boolean withParticipants,
                                    @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);

    if (toghUser == null) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    AdditionalInformationEvent additionalInformation = new AdditionalInformationEvent();
    additionalInformation.withParticipantsAsString = withParticipants;

    Map<String, Object> payload = new HashMap<>();
    completePayloadListEvents(payload, toghUser, filterEvents, additionalInformation, timezoneOffset);

    return payload;

  }

  /**
   * Get an event to display it
   *
   * @param eventId         eventId
   * @param timezoneOffset  timezoneOffset of the browser
   * @param connectionStamp Information on the connected user
   * @return a Map of one event
   */
  @CrossOrigin
  @GetMapping("/api/event")
  public Map<String, Object> event(@RequestParam("id") Long eventId,
                                   @RequestParam(name = RestJsonConstants.CST_TIMEZONEOFFSET, required = false) Long timezoneOffset,
                                   @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUserEntity == null)
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

    EventEntity eventEntity = eventService.getAllowedEventById(toghUserEntity, eventId);
    if (eventEntity == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);

    eventService.accessedByUser(eventEntity, toghUserEntity);


    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_EVENT, eventService.getMap(eventEntity, toghUserEntity, timezoneOffset, false));

    return payload;

  }

  /**
   * Create a new event
   *
   * @param connectionStamp Information on the connected user
   * @return a map of the Event
   */
  @CrossOrigin
  @PostMapping(value = "/api/event/create", produces = "application/json")
  @ResponseBody
  public Map<String, Object> createEvent(
      @Param(RestJsonConstants.CST_NAME) String eventName,
      @Param("getlist") Boolean getList,
      @Param(RestJsonConstants.CST_FILTER_EVENTS) String filterEvents,
      @RequestBody Map<String, Object> postData,
      @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    Map<String, Object> payload = new HashMap<>();
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long timezoneOffset = ToolCast.getLong(postData, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);

    if (eventName == null || eventName.trim().length() == 0)
      eventName = "New event";
    EventOperationResult eventOperationResult = eventService.createEvent(toghUser, eventName);
    if (Boolean.TRUE.equals(getList)) {
      completePayloadListEvents(payload,
          toghUser,
          RestJsonConstants.CST_FILTER_EVENTS_V_ALLEVENTS,
          new AdditionalInformationEvent(),
          timezoneOffset);
    }
    payload.put(RestJsonConstants.CST_LIMIT_SUBSCRIPTION, eventOperationResult.limitSubscription);
    payload.put(RestJsonConstants.CST_EVENT_ID, eventOperationResult.getEventId());
    payload.put(RestJsonConstants.CST_LOG_EVENTS, eventOperationResult.getEventsJson());
    payload.put(RestJsonConstants.CST_LIST_CHILD_ENTITIES, eventOperationResult.listChildEntities);

    return payload;

  }

  /**
   * Invite
   * /invite?
   * /invitation?
   * {
   * var param = {
   * eventid : <eventid>,
   * email : '',
   * userid: <thoguserId>,
   * message : '',
   * role: <ORGANIZER|PARTICIPANT|OBSERVER>
   * }
   * payload
   * {
   * "eventid" :<eventId>,
   * "useremail" :"email",
   * "userid" : <userId>,
   * "role" : "ORGANIZER|PARTICIPANT|OBSERVER"
   * }
   *
   * @param inviteData      Data for invitation
   * @param connectionStamp Information on the connected user
   * @return list of invitation
   */
  @CrossOrigin
  @PostMapping("/api/event/invitation")
  public Map<String, Object> invite(@RequestBody Map<String, Object> inviteData,
                                    @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUserEntity == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(inviteData, "eventid", null);
    List<Long> listUsersId = ToolCast.getListLong(inviteData, "listUsersid", null);
    String userInvitedEmail = ToolCast.getString(inviteData, "email", null);
    String subject = ToolCast.getString(inviteData, "subject", null);
    String message = ToolCast.getString(inviteData, "message", null);
    String role = ToolCast.getString(inviteData, "role", null);
    boolean useMyEmailAsFrom = ToolCast.getBoolean(inviteData, "useMyEmailAsFrom", false);
    Long timezoneOffset = ToolCast.getLong(inviteData, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);

    ParticipantRoleEnum roleEnum;
    try {
      roleEnum = ParticipantRoleEnum.valueOf(role);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect role[" + role + "]");

    }
    if (eventId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");

    // get service
    EventEntity eventEntity = eventService.getAllowedEventById(toghUserEntity, eventId);
    if (eventEntity == null) {
      // same error as not found: we don't want to give the information that the eventEntity exist
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
    }

    // we send the list of UserId, then the eventService will control each userId given, and will update the answer invitation per invitation
    InvitationResult invitationResult = eventService.invite(eventEntity, toghUserEntity, listUsersId, userInvitedEmail, roleEnum, useMyEmailAsFrom, subject, message);

    Map<String, Object> payload = new HashMap<>();
    List<Map<String, Object>> listParticipants = new ArrayList<>();
    payload.put("participants", listParticipants);

    EventController eventController = new EventController(eventEntity, factoryService, factoryRepository, factorySerializer);
    EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, toghUserEntity, SerializerOptions.ContextAccess.EVENTACCESS);
    SerializerOptions serializerOptions = new SerializerOptions(toghUserEntity,
        eventController,
        timezoneOffset,
        SerializerOptions.ContextAccess.EVENTACCESS,
        eventAccessGrantor);

    for (ParticipantEntity participant : invitationResult.newParticipants) {
      BaseSerializer serializer = factorySerializer.getFromEntity(participant);
      listParticipants.add(serializer.getMap(participant, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.STATUS, invitationResult.status.toString());
    payload.put(RestJsonConstants.MESSAGE_OK, invitationResult.getOkMessage());
    payload.put(RestJsonConstants.MESSAGE_ERROR, invitationResult.getErrorMessage());
    payload.put(RestJsonConstants.MESSAGE_ERROR_SEND_EMAIL, invitationResult.getErrorSendEmail());

    payload.put(RestJsonConstants.IS_INVITATION_SENT, invitationResult.status == InvitationStatus.INVITATIONSENT);
    return payload;
  }

  @CrossOrigin
  @PostMapping("/api/event/invite/resend")
  public Map<String, Object> resendInvitation(@RequestBody Map<String, Object> inviteData,
                                              @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(inviteData, "eventId", null);
    Long participantId = ToolCast.getLong(inviteData, "participantId", null);
    String subject = ToolCast.getString(inviteData, "subject", "");
    String message = ToolCast.getString(inviteData, "message", "");
    boolean useMyEmailAsFrom = ToolCast.getBoolean(inviteData, "useMyEmailAsFrom", false);

    if (eventId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
    if (participantId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ParticipantId not found");

    // get service
    EventEntity event = eventService.getAllowedEventById(toghUser, eventId);
    if (event == null) {
      // same error as not found: we don't want to give the information that the event exist
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
    }
    InvitationResult invitationResult = eventService.resendInvitation(event, toghUser, participantId, subject, message, useMyEmailAsFrom);
    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.STATUS, invitationResult.status.toString());
    payload.put(RestJsonConstants.MESSAGE_OK, invitationResult.getOkMessage());
    payload.put(RestJsonConstants.MESSAGE_ERROR, invitationResult.getErrorMessage());
    payload.put(RestJsonConstants.MESSAGE_ERROR_SEND_EMAIL, invitationResult.getErrorSendEmail());

    payload.put(RestJsonConstants.IS_INVITATION_SENT, invitationResult.status == InvitationStatus.INVITATIONSENT);
    return payload;

  }

  /**
   * Update an event. Give a list of Slabs
   *
   * @param updateMap       map of Update
   * @param connectionStamp Information on the connected user
   * @return the list of update status
   */
  @CrossOrigin
  @PostMapping("/api/event/update")
  public Map<String, Object> update(@RequestBody Map<String, Object> updateMap,
                                    @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUser == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(updateMap, "eventid", null);
    Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> slabEventList = ToolCast.getList(updateMap, "listslab", new ArrayList<>());

    EventEntity eventEntity = eventService.getEventById(eventId);
    if (eventEntity == null) {
      // same error as not found: we don't want to give the information that the event exist
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);
    }

    UpdateContext updateContext = new UpdateContext(toghUser, timezoneOffset, factoryService, eventEntity);

    EventOperationResult eventOperationResult = eventService.updateEvent(eventEntity, getListSlab(slabEventList), updateContext);

    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_EVENT_ID, eventOperationResult.getEventId());
    payload.put(RestJsonConstants.CST_LOG_EVENTS, eventOperationResult.getEventsJson());

    EventController eventController = new EventController(eventEntity, factoryService, factoryRepository, factorySerializer);
    EventAccessGrantor levelEventGrant = EventAccessGrantor.getEventAccessGrantor(eventController, toghUser, SerializerOptions.ContextAccess.EVENTACCESS);
    SerializerOptions serializerOptions = new SerializerOptions(toghUser,
        eventController,
        timezoneOffset,
        SerializerOptions.ContextAccess.EVENTACCESS,
        levelEventGrant);

    List<Map<String, Object>> listEntities = new ArrayList<>();
    for (BaseEntity baseEntity : eventOperationResult.listChildEntities) {
      if (baseEntity == null)
        continue;
      BaseSerializer serializer = factorySerializer.getFromEntity(baseEntity);

      listEntities.add(serializer.getMap(baseEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.CST_LIST_CHILD_ENTITIES, listEntities);
    payload.put(RestJsonConstants.CST_LIST_CHILD_ENTITIES_ID, eventOperationResult.listChildEntitiesId);

    // send back all the Chat group at each update - too important to miss one.
    List<EventGroupChatEntity> listGroupChat = eventEntity.getGroupChatList();
    List<Map<String, Object>> listGroupChatMap = listGroupChat.stream()
        .map(
            t -> {
              BaseSerializer serializer = factorySerializer.getFromEntity(t);
              return serializer.getMap(t, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor);
            }
        ).collect(Collectors.toList());

    payload.put(EventGroupChatEntity.SLABOPERATION_GROUPCHATLIST, listGroupChatMap);
    payload.put(RestJsonConstants.CST_LIMIT_SUBSCRIPTION, eventOperationResult.limitSubscription);

    if (eventOperationResult.eventEntity != null) {
      BaseSerializer serializer = factorySerializer.getFromEntity(eventOperationResult.eventEntity);
      payload.put(RestJsonConstants.CST_EVENT, serializer.getMap(eventOperationResult.eventEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.STATUS, eventOperationResult.isError() ? RestJsonConstants.STATUS_V_ERROR : RestJsonConstants.STATUS_V_OK);

    return payload;
  }

  /**
   * Get the list of events and populate the payload with it
   *
   * @param payload                    Rest payload
   * @param toghUser                   user who execute the request
   * @param filterEventsSt             should be RestJsonConstants.CST_PARAM_FILTER_EVENTS_V_...
   * @param additionalInformationEvent additional events
   * @param timezoneOffset             time zone of the browser
   */
  private void completePayloadListEvents(Map<String, Object> payload,
                                         ToghUserEntity toghUser,
                                         String filterEventsSt,
                                         AdditionalInformationEvent additionalInformationEvent,
                                         Long timezoneOffset) {
    List<Map<String, Object>> listEventsMap = new ArrayList<>();
    FilterEvents filterEvent;
    if (RestJsonConstants.CST_FILTER_EVENTS_V_ALLEVENTS.equals(filterEventsSt))
      filterEvent = FilterEvents.ALLEVENTS;
    else if (RestJsonConstants.CST_FILTER_EVENTS_V_NEXTEVENTS.equals(filterEventsSt))
      filterEvent = FilterEvents.NEXTEVENTS;
    else if (RestJsonConstants.CST_FILTER_EVENTS_V_MYEVENTS.equals(filterEventsSt))
      filterEvent = FilterEvents.MYEVENTS;
    else if (RestJsonConstants.CST_FILTER_EVENTS_V_MYINVITATIONS.equals(filterEventsSt))
      filterEvent = FilterEvents.MYINVITATIONS;
    else
      filterEvent = FilterEvents.ALLEVENTS;
    EventResult eventResult = eventService.getEvents(toghUser, filterEvent);

    if (LogEventFactory.isError(eventResult.listLogEvent)) {
      payload.put(RestJsonConstants.CST_LOG_EVENTS, LogEventFactory.getJson(eventResult.listLogEvent));
    } else {
      for (EventEntity eventEntity : eventResult.listEvents) {
        EventSerializer serializer = (EventSerializer) factorySerializer.getFromEntity(eventEntity);
        EventController eventController = new EventController(eventEntity, factoryService, factoryRepository, factorySerializer);
        EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, toghUser, SerializerOptions.ContextAccess.EVENTACCESS);
        SerializerOptions serializerOptions = new SerializerOptions(toghUser,
            eventController,
            timezoneOffset,
            SerializerOptions.ContextAccess.EVENTACCESS,
            eventAccessGrantor);

        listEventsMap.add(serializer.getHeaderMap(eventEntity, serializerOptions, additionalInformationEvent, factorySerializer));
      }
      payload.put(RestJsonConstants.CST_LIST_EVENTS, listEventsMap);
    }
  }


}

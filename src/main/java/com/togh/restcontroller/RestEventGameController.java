/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;


import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.access.EventAccessGrantor;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.serialization.BaseSerializer;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.service.EventFactoryRepository;
import com.togh.service.EventService;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.FactoryService;
import com.togh.service.event.EventController;
import com.togh.tool.ToolCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestEventController */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestEventGameController {

  public static final String PARAM_EVENT_ID = "eventId";
  public static final String PARAM_GAME_ID = "gameId";
  public static final String PARAM_PLAYER_ID = "playerId";
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

  @CrossOrigin
  @PostMapping("/api/event/game/synchronizeplayers")
  public Map<String, Object> gameSynchronizePlayer(@RequestBody Map<String, Object> gameData,
                                                   @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUserEntity == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(gameData, PARAM_EVENT_ID, null);
    Long gameId = ToolCast.getLong(gameData, PARAM_GAME_ID, null);
    Long timezoneOffset = ToolCast.getLong(gameData, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);

    if (eventId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, PARAM_EVENT_ID + " not found");
    if (gameId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, PARAM_GAME_ID + " not found");
    boolean reset = ToolCast.getBoolean(gameData, "reset", false);

    EventEntity eventEntity = eventService.getAllowedEventById(toghUserEntity, eventId);
    if (eventEntity == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);

    // check if user is an owner of the event
    EventOperationResult eventOperationResult = eventService.gameSynchronizePlayer(eventEntity, gameId, reset, toghUserEntity);

    EventController eventController = new EventController(eventEntity, factoryService, factoryRepository, factorySerializer);
    EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, toghUserEntity, SerializerOptions.ContextAccess.EVENTACCESS);
    SerializerOptions serializerOptions = new SerializerOptions(toghUserEntity,
        eventController,
        timezoneOffset,
        SerializerOptions.ContextAccess.EVENTACCESS,
        eventAccessGrantor);


    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_EVENT_ID, eventOperationResult.getEventId());
    payload.put(RestJsonConstants.CST_LOG_EVENTS, eventOperationResult.getEventsJson());
    List<Map<String, Object>> listEntities = new ArrayList<>();
    for (BaseEntity baseEntity : eventOperationResult.listChildEntities) {
      if (baseEntity == null)
        continue;
      BaseSerializer serializer = factorySerializer.getFromEntity(baseEntity);

      listEntities.add(serializer.getMap(baseEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.CST_LIST_CHILD_ENTITIES, listEntities);

    payload.put(RestJsonConstants.CST_EVENT, eventService.getMap(eventEntity, toghUserEntity, timezoneOffset, false));

    return payload;
  }


  @CrossOrigin
  @PostMapping("/api/event/game/synchronizetruthorlie")
  public Map<String, Object> gameSynchronizeTruthOrLie(@RequestBody Map<String, Object> gameData,
                                                       @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUserEntity == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(gameData, PARAM_EVENT_ID, null);
    Long gameId = ToolCast.getLong(gameData, PARAM_GAME_ID, null);
    Long playerId = ToolCast.getLong(gameData, PARAM_PLAYER_ID, null);
    Long timezoneOffset = ToolCast.getLong(gameData, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);

    if (eventId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, PARAM_EVENT_ID + " not found");
    if (gameId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, PARAM_GAME_ID + " not found");
    if (playerId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, PARAM_PLAYER_ID + " not found");
    EventController eventController;
    EventOperationResult eventOperationResult;
    EventEntity eventEntity;
    // we may have 2 players at the same time. We do not want to create in double everything
    synchronized (EventController.getMutex(eventId)) {
      eventEntity = eventService.getAllowedEventById(toghUserEntity, eventId);
      if (eventEntity == null)
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);

      // check if user is an owner of the event

      eventController = new EventController(eventEntity, factoryService, factoryRepository, factorySerializer);
      eventOperationResult = eventService.gameSynchronizeTruthOrLie(eventEntity, gameId, playerId, toghUserEntity);
    }

    EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, toghUserEntity, SerializerOptions.ContextAccess.EVENTACCESS);
    SerializerOptions serializerOptions = new SerializerOptions(toghUserEntity,
        eventController,
        timezoneOffset,
        SerializerOptions.ContextAccess.EVENTACCESS,
        eventAccessGrantor);


    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_EVENT_ID, eventOperationResult.getEventId());
    payload.put(RestJsonConstants.CST_LOG_EVENTS, eventOperationResult.getEventsJson());
    List<Map<String, Object>> listEntities = new ArrayList<>();
    for (BaseEntity baseEntity : eventOperationResult.listChildEntities) {
      if (baseEntity == null)
        continue;
      BaseSerializer serializer = factorySerializer.getFromEntity(baseEntity);

      listEntities.add(serializer.getMap(baseEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.CST_LIST_CHILD_ENTITIES, listEntities);

    payload.put(RestJsonConstants.CST_EVENT, eventService.getMap(eventEntity, toghUserEntity, timezoneOffset, false));

    return payload;
  }


  @CrossOrigin
  @PostMapping("/api/event/game/votetruthorlie")
  public Map<String, Object> gameVotetruthorlie(@RequestBody Map<String, Object> gameData,
                                                @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUserEntity == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(gameData, PARAM_EVENT_ID, null);
    Long gameId = ToolCast.getLong(gameData, PARAM_GAME_ID, null);
    Long trueOrLieId = ToolCast.getLong(gameData, "trueOrLieId", null);
    Long voteId = ToolCast.getLong(gameData, "voteId", null);
    Long timezoneOffset = ToolCast.getLong(gameData, RestJsonConstants.CST_TIMEZONEOFFSET, 0L);


    @SuppressWarnings("unchecked")
    List<Map<String, Object>> slabEventList = ToolCast.getList(gameData, "listslab", new ArrayList<>());


    if (eventId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "eventId not found");
    if (gameId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "gameId not found");
    if (trueOrLieId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trueOrLieId not found");
    if (voteId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "voteId not found");


    EventEntity eventEntity = eventService.getEventById(eventId);
    if (eventEntity == null) {
      // same error as not found: we don't want to give the information that the event exist
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);
    }

    UpdateContext updateContext = new UpdateContext(toghUserEntity, timezoneOffset, factoryService, eventEntity);
    EventOperationResult eventOperationResult = new EventOperationResult(eventEntity);
    if (!slabEventList.isEmpty())
      eventOperationResult.add(eventService.updateEvent(eventEntity, RestEventController.getListSlab(slabEventList), updateContext));

    // now vote
    EventController eventController;
    // we may have 2 players at the same time. We do not want to create in double everything
    synchronized (EventController.getMutex(eventId)) {
      eventEntity = eventService.getAllowedEventById(toghUserEntity, eventId);
      if (eventEntity == null)
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);

      // check if user is a participant of the event
      eventController = new EventController(eventEntity, factoryService, factoryRepository, factorySerializer);
      eventOperationResult = eventService.gameSynchronizeTruthOrLie(eventEntity, gameId, toghUserEntity.getId(), toghUserEntity);

      // player is the same as the user who is connected
      eventOperationResult.add(eventService.gameTruthOrLieVote(eventEntity, gameId, voteId, toghUserEntity.getId(), toghUserEntity));
    }

    // produce the result
    EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, toghUserEntity, SerializerOptions.ContextAccess.EVENTACCESS);
    SerializerOptions serializerOptions = new SerializerOptions(toghUserEntity,
        eventController,
        timezoneOffset,
        SerializerOptions.ContextAccess.EVENTACCESS,
        eventAccessGrantor);


    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_EVENT_ID, eventOperationResult.getEventId());
    payload.put(RestJsonConstants.CST_LOG_EVENTS, eventOperationResult.getEventsJson());
    List<Map<String, Object>> listEntities = new ArrayList<>();
    for (BaseEntity baseEntity : eventOperationResult.listChildEntities) {
      if (baseEntity == null)
        continue;
      BaseSerializer serializer = factorySerializer.getFromEntity(baseEntity);

      listEntities.add(serializer.getMap(baseEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
    }
    payload.put(RestJsonConstants.CST_LIST_CHILD_ENTITIES, listEntities);

    payload.put(RestJsonConstants.CST_EVENT, eventService.getMap(eventEntity, toghUserEntity, timezoneOffset, false));

    return payload;
  }

  @CrossOrigin
  @PostMapping("/api/event/game/unvalidatetruthorlie")
  public Map<String, Object> unvalidateTruthOrLie(@RequestBody Map<String, Object> gameData,
                                                  @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
    ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
    if (toghUserEntity == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
    }
    Long eventId = ToolCast.getLong(gameData, PARAM_EVENT_ID, null);
    Long gameId = ToolCast.getLong(gameData, PARAM_GAME_ID, null);
    if (eventId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "eventId not found");
    if (gameId == null)
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "gameId not found");

    // get service
    EventEntity eventEntity = eventService.getAllowedEventById(toghUserEntity, eventId);
    if (eventEntity == null) {
      // same error as not found: we don't want to give the information that the eventEntity exist
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
    }

    // we send the list of UserId, then the eventService will control each userId given, and will update the answer invitation per invitation
    EventOperationResult eventOperationResult = eventService.unvalidateTruthOrLie(eventEntity, gameId, toghUserEntity);


    Map<String, Object> payload = new HashMap<>();
    payload.put(RestJsonConstants.CST_LOG_EVENTS, eventOperationResult.listLogEvents);

    return payload;
  }
}
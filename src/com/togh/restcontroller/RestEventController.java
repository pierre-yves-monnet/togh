/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.EventResult;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.EventService.InvitationStatus;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.FactoryService;
import com.togh.service.event.EventController;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestEventControler */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
public class RestEventController {

    @Autowired
    private FactoryService factoryService;

    
    @CrossOrigin
    @GetMapping("/api/event/list")
      public Map<String, Object> events(@RequestParam( RestJsonConstants.CST_PARAM_FILTER_EVENTS ) String filterEvents,
              @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
              @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);

        if (toghUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
        }
        Map<String, Object> payload = new HashMap<>();
       completePayloadListEvents(payload, toghUser, filterEvents,timezoneOffset);

        return payload;

    }
    @CrossOrigin
    @GetMapping("/api/event")
    public Map<String, Object> event(@RequestParam("id") Long eventId,
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
            @RequestHeader(RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser  = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        EventEntity event = factoryService.getEventService().getAllowedEventById(toghUser, eventId);
        if (event == null)
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);

        Map<String, Object> payload = new HashMap<>();
        payload.put( RestJsonConstants.CST_EVENT, event.getMap( EventController.getInstance( event, factoryService).getTypeAccess(toghUser), timezoneOffset));

        return payload;

    }

    /**
     * Create a new event
     * 
     * @param connectionStamp
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/event/create", produces = "application/json")
    @ResponseBody
    public Map<String, Object> createEvent(
            @Param( RestJsonConstants.CST_PARAM_NAME ) String eventName,
            @Param("getlist") Boolean getList,
            @Param( RestJsonConstants.CST_PARAM_FILTER_EVENTS ) String filterEvents,
            @RequestBody Map<String, Object> postData,
            @RequestHeader(RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {
        Map<String, Object> payload = new HashMap<>();
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
        }
        Long timezoneOffset             = RestTool.getLong(postData, "timezoneoffset", 0L);

        if (eventName ==null || eventName.trim().length()==0)
            eventName="New event";
        EventOperationResult eventOperationResult = factoryService.getEventService().createEvent(toghUser, eventName );
        if (Boolean.TRUE.equals(getList)) {
            completePayloadListEvents(payload, toghUser, filterEvents, timezoneOffset);
        }
        payload.put( RestJsonConstants.CST_EVENTID, eventOperationResult.getEventId() );
        payload.put( RestJsonConstants.CST_LISTLOGEVENTS, eventOperationResult.getEventsJson());
        payload.put( RestJsonConstants.CST_CHILDENTITY, eventOperationResult.listChildEntity);

        return payload;

    }


    /**
     * Invite 
     *   /invite?
     *   /invitation?
     *   {
     *      var param = {
            eventid : <eventid>,
            email : '',
            userid: <thoguserId>,
            message : '',
            role: <ORGANIZER|PARTICIPANT|OBSERVER>
        }
     *   payload
     *   {
     *      "eventid" :<eventId>,
     *      "useremail" :"email",
     *      "userid" : <userId>,
     *      "role" : "ORGANIZER|PARTICIPANT|OBSERVER"
     *   }
     * @param eventId
     * @param connectionStamp
     * @return
     */
    
    @CrossOrigin
    @PostMapping("/api/event/invitation")
    public Map<String, Object> invite(@RequestBody Map<String, Object> inviteData, @RequestHeader(RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
        }
        Long eventId = RestTool.getLong(inviteData, "eventid", null);
        List<Long> listUsersId= RestTool.getListLong(inviteData, "listUsersid", null);
        String userInvitedEmail = RestTool.getString(inviteData, "email", null);
        String message = RestTool.getString(inviteData, "message", null);
        String role = RestTool.getString(inviteData, "role", null);
        Long timezoneOffset             = RestTool.getLong(inviteData, "timezoneoffset", 0L);

        ParticipantRoleEnum roleEnum;
        try {
            roleEnum = ParticipantRoleEnum.valueOf(role);
        }
        catch(Exception e) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Incorrect role["+role+"]");
           
        }
        if (eventId == null)
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Event not found");

        // get service        
        EventService eventService = factoryService.getEventService();
        EventEntity event = eventService.getAllowedEventById(toghUser, eventId);
        if (event==null) {
            // same error as not found: we don't want to give the information that the event exist
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Event not found");
        }
                
        // we send the list of UserId, then the eventService will control each userId given, and will update the answer invitation per invitation
        InvitationResult invitationResult = eventService.invite( event, toghUser, listUsersId, userInvitedEmail, roleEnum, message );
        
        Map<String, Object> payload = new HashMap<>();
        List<Map<String,Object>> listParticipants = new ArrayList<>();
        payload.put("participants", listParticipants);
        for (ParticipantEntity participant : invitationResult.newParticipants)
            listParticipants.add( participant.getMap( ContextAccess.PUBLICACCESS, timezoneOffset ));
        payload.put( RestJsonConstants.CST_STATUS, invitationResult.status.toString());
        payload.put( RestJsonConstants.CST_OKMESSAGE, invitationResult.okMessage.toString());
        payload.put( RestJsonConstants.CST_ERRORMESSAGE, invitationResult.errorMessage.toString());

        payload.put( RestJsonConstants.CST_ISINVITATIONSENT, invitationResult.status == InvitationStatus.INVITATIONSENT);
        return payload;
    }
    
    @CrossOrigin
    @PostMapping("/api/event/update")
    public Map<String, Object> update(@RequestBody Map<String, Object> updateMap, @RequestHeader(RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
        }
        Long eventId                            = RestTool.getLong(updateMap, "eventid", null);
        Long timezoneOffset                     = RestTool.getLong(updateMap, "timezoneoffset", 0L);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> slabEventList = RestTool.getList(updateMap, "listslab", new ArrayList<>() );

        EventEntity event = factoryService.getEventService().getEventById( eventId);
        if (event==null) {
            // same error as not found: we don't want to give the information that the event exist
            throw new ResponseStatusException( HttpStatus.NOT_FOUND,  RestHttpConstant.CST_HTTPCODE_EVENTNOTFOUND);
        }

        UpdateContext updateContext  = new UpdateContext();
        updateContext.toghUser = toghUser;
        updateContext.timezoneOffset = timezoneOffset;
        updateContext.eventService = factoryService.getEventService();
        EventOperationResult eventOperationResult = factoryService.getEventService().updateEvent( event, slabEventList, updateContext);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put( RestJsonConstants.CST_EVENTID, eventOperationResult.getEventId());
        payload.put( RestJsonConstants.CST_LISTLOGEVENTS, eventOperationResult.getEventsJson());
        
        ContextAccess contextAccess = EventController.getInstance(  event, factoryService).getTypeAccess(toghUser);
        List<Map<String,Object>> listEntity = new ArrayList<>();
        for(BaseEntity entity : eventOperationResult.listChildEntity) {
            listEntity.add( entity.getMap(contextAccess, timezoneOffset));
        }
        payload.put( RestJsonConstants.CST_CHILDENTITY, listEntity);
        
        payload.put( RestJsonConstants.CST_CHILDENTITYID, eventOperationResult.listChildEntityId);
        payload.put( RestJsonConstants.CST_EVENT, eventOperationResult.eventEntity ==null? null : eventOperationResult.eventEntity.getMap(contextAccess, timezoneOffset) );
        payload.put( RestJsonConstants.CST_STATUS, eventOperationResult.isError() ? RestJsonConstants.CST_STATUS_V_ERROR : RestJsonConstants.CST_STATUS_V_OK);

        return payload;
    }
    
    /**
     * Get the list of events and populate the payload with it
     * @param payload
     * @param toghUser
     * @param filterEvents
     */
    private void completePayloadListEvents(Map<String,Object> payload, ToghUserEntity toghUser, String filterEvents,Long timezoneOffset) { 
        List<Map<String,Object>> listEventsMap= new ArrayList<>();
        EventResult eventResult = factoryService.getEventService().getEvents(toghUser, filterEvents);
    if (LogEventFactory.isError( eventResult.listLogEvent)) {
        payload.put( RestJsonConstants.CST_LISTLOGEVENTS, LogEventFactory.getJson(eventResult.listLogEvent));
    } else {
        for (EventEntity event : eventResult.listEvents) {
            EventController eventController = new EventController( event, factoryService);
            listEventsMap.add( event.getHeaderMap( eventController.getTypeAccess(toghUser), timezoneOffset));
        }
        payload.put( RestJsonConstants.CST_LISTEVENTS, listEventsMap);
    }
    }
}

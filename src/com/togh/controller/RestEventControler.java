package com.togh.controller;
/* -------------------------------------------------------------------- */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.ToghUserEntity;
import com.togh.service.EventService;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.EventService.InvitationStatus;
import com.togh.service.FactoryService;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestEventControler */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
public class RestEventControler {

    @Autowired
    private FactoryService factoryService;

    
    @CrossOrigin
    @GetMapping("/api/event/list")
      public Map<String, Object> events(@RequestParam("filterEvents") String filterEvents, @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);

        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        Map<String, Object> payload = new HashMap<>();
        List<Map<String,Object>> listEventsMap= new ArrayList<>();
        for (EventEntity event : factoryService.getEventService().getEvents(userId, filterEvents)) {
            listEventsMap.add( event.getHeaderMap( event.getTypeAccess(userId)));
        }

        payload.put("events", listEventsMap);
        // EventService eventService = serciceAccessor.getEventService();
        return payload;

    }
    @CrossOrigin
    @GetMapping("/api/event")
    public Map<String, Object> event(@RequestParam("id") Long eventId, @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);
        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        EventEntity event = factoryService.getEventService().getEventById(userId, eventId);
        if (event == null)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "event not found");

        Map<String, Object> payload = new HashMap<>();
        payload.put("event", event.getMap( event.getTypeAccess(userId)));

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
    public Map<String, Object> createEvent(@RequestHeader("Authorization") String connectionStamp) {
        Map<String, Object> payload = new HashMap<>();
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");
        }
        ToghUserEntity toghUser = factoryService.getToghUserService().getUserFromId( userId );
        
        EventEntity event = factoryService.getEventService().createEvent(toghUser);
        payload.put("eventid", event.getId());
        payload.put("events", factoryService.getEventService().getEvents(userId, null));

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
    public Map<String, Object> invite(@RequestBody Map<String, Object> inviteData, @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);
        if (userId == null) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not connected");
        }
        Long eventId = RestTool.getLong(inviteData, "eventid", null);
        Long inviteduserid= RestTool.getLong(inviteData, "inviteduserid", null);
        String userInvitedEmail = RestTool.getString(inviteData, "email", null);
        String message = RestTool.getString(inviteData, "message", null);
        String role = RestTool.getString(inviteData, "role", null);
        ParticipantRoleEnum roleEnum;
        try {
            roleEnum = ParticipantRoleEnum.valueOf(role);
        }
        catch(Exception e) {
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Incorrect role["+role+"]");
           
        }
        if (eventId == null)
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Event not found");

        EventService eventService = factoryService.getEventService();
        EventEntity event = eventService.getEventById(userId, eventId);
        if (event==null) {
            // same error as not found: we don't want to give the information that the event exist
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Event not found");
        }
 
                
        InvitationResult invitationResult = eventService.invite( event, userId, inviteduserid, userInvitedEmail, roleEnum, message );
        
        Map<String,Object> resultMap = new HashMap<>();
        if ( invitationResult.participant!=null)
            resultMap.putAll( invitationResult.participant.getMap( ContextAccess.PUBLICACCESS ));
        resultMap.put("status", invitationResult.status.toString());
        resultMap.put("isinvitationsend", invitationResult.status == InvitationStatus.INVITATIONSENT);
        return resultMap;
    }
}

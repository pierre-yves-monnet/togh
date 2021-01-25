package com.togh.controller;
/* -------------------------------------------------------------------- */

/*                                                                      */
/* Login */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.service.EventService;
import com.togh.service.EventService.InvitationResult;
import com.togh.service.EventService.InvitationStatus;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.ToghUserService;

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
        payload.put("events", factoryService.getEventService().getEvents(userId, filterEvents));
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
        payload.put("event", event);

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
     *   /invite?id=<eventid>&useremail=<userEmail>&role=<ORGANIZER|PARTICIPANT|OBSERVER>
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
    @PostMapping("/api/event/invite")
    public Map<String, Object> invite(@RequestBody Map<String, Object> inviteData, @RequestParam @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);
        if (userId == null) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not connected");
        }
        Long eventId = RestTool.getLong(inviteData, "eventid", null);
        Long userInvitedId = RestTool.getLong(inviteData, "userid", null);
        String userInvitedEmail = RestTool.getString(inviteData, "useremail", null);
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
     
        InvitationResult invitationResult = eventService.invite( event, userId, userInvitedId, userInvitedEmail, roleEnum );
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("status", invitationResult.status.toString());
        return resultMap;
    }
}

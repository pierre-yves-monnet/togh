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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.service.EventService;
import com.togh.service.LoginService;
import com.togh.service.ToghUserService;

@RestController
public class RestEventControler {

    @Autowired
    private LoginService loginService;
    @Autowired
    private ToghUserService userService;

    @Autowired
    private EventService eventService;

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Event */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    @CrossOrigin
    @GetMapping("/api/event/list")
    public Map<String, Object> events(@RequestParam("filterEvents") String filterEvents, @RequestHeader("Authorization") String connectionStamp) {
        Long userId = loginService.isConnected(connectionStamp);

        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        Map<String, Object> payload = new HashMap<>();
        payload.put("events", eventService.getEvents(userId, filterEvents));
        // EventService eventService = serciceAccessor.getEventService();
        return payload;

    }
    @CrossOrigin
    @GetMapping("/api/event")
    public Map<String, Object> event(@RequestParam("id") Long eventId, @RequestHeader("Authorization") String connectionStamp) {
        Long userId = loginService.isConnected(connectionStamp);
        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        EventEntity event = eventService.getEventsById(userId, eventId);
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
        Long userId = loginService.isConnected(connectionStamp);
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");
        }
        ToghUserEntity toghUser = userService.getUserFromId( userId );
        
        EventEntity event = eventService.createEvent(toghUser);
        payload.put("eventid", event.getId());
        return payload;

    }
   

}

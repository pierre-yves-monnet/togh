package com.together.controller;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.together.entity.EventEntity;
import com.together.service.EventService;
import com.together.service.LoginService;

@RestController
public class EventControler {

    @Autowired
    private LoginService loginService;

    @Autowired
    private EventService eventService;

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Event */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    @GetMapping("/event/list")
    public Map<String, Object> events(@RequestParam("filterEvents") String filterEvents, @CookieValue(value = "togh") String connectionStamp) {
        Long userId = loginService.isConnected(connectionStamp);

        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("events", eventService.getEvents(userId, filterEvents));
        // EventService eventService = serciceAccessor.getEventService();
        return payload;

    }

    @GetMapping("/event")
    public Map<String, Object> event(@RequestParam("id") Long eventId, @CookieValue(value = "togh") String connectionStamp) {
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
    @PostMapping(value = "/event/create", produces = "application/json")
    @ResponseBody
    public Map<String, Object> createEvent(@CookieValue(value = "togh") String connectionStamp) {
        Map<String, Object> payload = new HashMap<>();
        Long userId = loginService.isConnected(connectionStamp);
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");
        }

        EventEntity event = eventService.createEvent(userId);
        payload.put("eventid", event.getId());
        return payload;

    }

}

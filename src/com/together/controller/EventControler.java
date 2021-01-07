package com.together.controller;
/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login */
/*                                                                      */
/* -------------------------------------------------------------------- */


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

   
    @GetMapping("/events")
    public List<EventEntity> events( @RequestParam("filterEvents") String filterEvents, @CookieValue(value="togh") String connectionStamp) {
        Long userId= loginService.isConnected(connectionStamp);

        if ( userId==null)
            return null;
        
        // EventService eventService = serciceAccessor.getEventService();
        return eventService.getEvents(userId, filterEvents);

    }

    
    @GetMapping("/event")
    public EventEntity event( @RequestParam("id") Long eventId, @CookieValue(value="togh") String connectionStamp) {
        Long userId= loginService.isConnected(connectionStamp);
        if ( userId==null)
            return null;//better throw an unauthorized exception
        
        // EventService eventService = serciceAccessor.getEventService();
        return eventService.getEventsById( userId, eventId);

    }
    
    
    @GetMapping("/newevent")
    public EventEntity newevent( @CookieValue(value="togh") String connectionStamp) {
        Long userId= loginService.isConnected(connectionStamp);
        if ( userId==null)
            return null;//better throw an unauthorized exception
        // EventService eventService = serciceAccessor.getEventService();
        EventEntity event = eventService.createEvent(12L);

        return event;

    }

}

package com.together.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.together.entity.EventEntity;
import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.LoginService.LoginStatus;

@RestController
public class ApplicationControler {
	
    @Autowired
    private LoginService loginService;

    @Autowired
    private EventService eventService;
    
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Login */
    /*                                                                      */
    /* -------------------------------------------------------------------- */


    @PostMapping(value = "/login",produces = "application/json")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        // LoginService loginService = serciceAccessor.getLoginService();
        LoginStatus loginStatus = loginService.connectWithEmail(userData.get("email"), userData.get("password"));
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return loginStatus.getMap();
    }
    
    @GetMapping(value = "/logout",produces = "application/json")
    public String logout( @CookieValue(value="togh") String connectionStamp) {
        // LoginService loginService = serciceAccessor.getLoginService();
        loginService.disconnectUser(connectionStamp);
        return "{}";
    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Event */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

   
    @GetMapping("/events")
    public List<EventEntity> events( @RequestParam("filterEvents") String filterEvents, @CookieValue(value="togh") String connectionStamp) {
        Long userId= isConnected( connectionStamp);
        if ( userId==null)
            return null;
        
        // EventService eventService = serciceAccessor.getEventService();
        return eventService.getEvents(userId, filterEvents);

    }

    
    @GetMapping("/event")
    public EventEntity event( @RequestParam("id") Long eventId, @CookieValue(value="togh") String connectionStamp) {
        Long userId= isConnected( connectionStamp);
        if ( userId==null)
            return null;//better throw an unauthorized exception
        
        // EventService eventService = serciceAccessor.getEventService();
        return eventService.getEventsById( userId, eventId);

    }
    
    
    @GetMapping("/newevent")
    public EventEntity newevent() {
        // EventService eventService = serciceAccessor.getEventService();
        EventEntity event = eventService.createEvent(12L);

        return event;

    }

    
    
    /**
     * Is the user is connected ?
     * @param connectionStamp
     * @return userId or null if not connected
     */
    private Long isConnected(String connectionStamp) {
        // LoginService loginService = serciceAccessor.getLoginService();
        return loginService.isConnected(connectionStamp);

    }
}

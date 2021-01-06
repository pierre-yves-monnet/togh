package com.together.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.together.data.entity.EventEntity;
import com.together.data.entity.base.BaseEntity;
import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.LoginService.LoginStatus;
import com.together.service.accessor.MemoryServiceAccessor;
import com.together.service.accessor.ServiceAccessor;

@RestController
public class ApplicationControler {
    // @Autowired
    private LoginService loginService;

    // @Autowired
    private EventService eventService;
    ServiceAccessor serviceAccessor = new MemoryServiceAccessor();
    
    public ApplicationControler() {
            loginService = serviceAccessor.getLoginService();
            eventService = serviceAccessor.getEventService();
    }
    
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Login */
    /*                                                                      */
    /* -------------------------------------------------------------------- */


    @PostMapping(value = "/login",produces = "application/json")
    @ResponseBody
    public String login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = loginService.connectWithEmail(userData.get("email"), userData.get("password"));
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return JSONValue.toJSONString(loginStatus.getMap());
    }
    @GetMapping(value = "/logout",produces = "application/json")
    public String logout( @CookieValue(value="togh") String connectionStamp) {
        loginService.disconnectUser(connectionStamp);
        return "{}";
    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Event */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

   
    @GetMapping("/events")
    public Map<String,Object> events( @RequestParam("filterEvents") String filterEvents, @CookieValue(value="togh") String connectionStamp) {
        Long userId= isConnected( connectionStamp);
        if ( userId==null)
            return new HashMap<>();
        
        List<EventEntity> listEvents = eventService.getEvents(userId, filterEvents);
        Map<String,Object> result = new HashMap<>();
        result.put("events", BaseEntity.getListForJson(listEvents));
        return result;
    }

    
    @GetMapping("/event")
    public String event( @RequestParam("id") Long eventId, @CookieValue(value="togh") String connectionStamp) {
        Long userId= isConnected( connectionStamp);
        if ( userId==null)
            return "{}";
        
        EventEntity event = eventService.getEventsById( userId, eventId);
        Map<String,Object> result = new HashMap<>();
        result.put("event", event.getMapForJson());
        return JSONValue.toJSONString( result );

    }
    
    
    @GetMapping("/newevent")
    public EventEntity newevent() {
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

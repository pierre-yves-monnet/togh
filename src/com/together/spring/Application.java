package com.together.spring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.together.service.accessor.SpringServiceAccessor;

/* ******************************************************************************** */
/*                                                                                  */
/*
 * SprintApplication
 * Main front end for all REST call
 */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@SpringBootApplication
@RestController
// @ E n a b  l e J p a  R e p  ositories(basePackages="com.together.repository.spring")
@ComponentScan("com.together.repository.spring")
public class Application extends SpringBootServletInitializer {

    ServiceAccessor serciceAccessor = new MemoryServiceAccessor();

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

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
    public String login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginService loginService = serciceAccessor.getLoginService();
        LoginStatus loginStatus = loginService.connectWithEmail(userData.get("email"), userData.get("password"));
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return JSONValue.toJSONString(loginStatus.getMap());
    }
    @GetMapping(value = "/logout",produces = "application/json")
    public String logout( @CookieValue(value="togh") String connectionStamp) {
        LoginService loginService = serciceAccessor.getLoginService();
        loginService.disconnectUser(connectionStamp);
        return "{}";
    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Event */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

   
    @GetMapping("/events")
    public String events( @RequestParam("filterEvents") String filterEvents, @CookieValue(value="togh") String connectionStamp) {
        Long userId= isConnected( connectionStamp);
        if ( userId==null)
            return "{}";
        
        EventService eventService = serciceAccessor.getEventService();
        List<EventEntity> listEvents = eventService.getEvents(userId, filterEvents);
        Map<String,Object> result = new HashMap<>();
        result.put("events", BaseEntity.getListForJson(listEvents));
        return JSONValue.toJSONString( result );

    }

    
    @GetMapping("/event")
    public String event( @RequestParam("id") Long eventId, @CookieValue(value="togh") String connectionStamp) {
        Long userId= isConnected( connectionStamp);
        if ( userId==null)
            return "{}";
        
        EventService eventService = serciceAccessor.getEventService();
        EventEntity event = eventService.getEventsById( userId, eventId);
        Map<String,Object> result = new HashMap<>();
        result.put("event", event.getMapForJson());
        return JSONValue.toJSONString( result );

    }
    
    
    @GetMapping("/newevent")
    public String newevent() {
        EventService eventService = serciceAccessor.getEventService();
        EventEntity event = eventService.createEvent(12L);

        return JSONValue.toJSONString( event.getMapForJson());

    }

    
    
    /**
     * Is the user is connected ?
     * @param connectionStamp
     * @return userId or null if not connected
     */
    private Long isConnected(String connectionStamp) {
        LoginService loginService = serciceAccessor.getLoginService();
        return loginService.isConnected(connectionStamp);

    }
}

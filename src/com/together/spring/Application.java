package com.together.spring;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.together.data.entity.EventEntity;
import com.together.data.entity.base.BaseEntity;
import com.together.service.EventService;
import com.together.service.MemoryServiceAccessor;
import com.together.service.SpringServiceAccessor;


/* ******************************************************************************** */
/*                                                                                  */
/*    SprintApplication
 * 
 *  Main front end for all REST call                                                */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@SpringBootApplication
@RestController
// @ E n a b  l e J p a  R e p  ositories(basePackages="com.together.repository.spring")
@ComponentScan("com.together.repository.spring")
public class Application extends SpringBootServletInitializer {

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
    
    @GetMapping("/events2")
    public String events2() {
        
        return "yes";
    }
    
    
    @GetMapping("/events")
    public String events() {
        EventService eventService = getEventService();
        List<EventEntity> listEvents = eventService.getMyEvents(12L);
        
        return BaseEntity.getListJson( listEvents );
        
    }
    @GetMapping("/newevent")
    public String newevent() {
        EventService eventService = getEventService();
        EventEntity event = eventService.createEvent(12L);
        
        return event.getJson();
        
    }
    
    
    private EventService getEventService() {
        return MemoryServiceAccessor.getInstance().getEventService();
    }
    
}

package com.together.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.together.repository.EventRepository;
import com.together.repository.spring.EventSpringRepository;

public class SpringServiceAccessor {
    
    @Autowired
    private EventRepository eventSpringRepository;

  
    private final static SpringServiceAccessor springServiceAccessor = new SpringServiceAccessor();
    public static SpringServiceAccessor getInstance() {
        return springServiceAccessor;
    }
    
    /**
     * Return the event Service using the SprintRepository
     * @return
     */
    public EventService getEventService( ) {
        EventService eventService = new EventService();
        eventService.setEventRepository(eventSpringRepository);
        return eventService;
    }

}

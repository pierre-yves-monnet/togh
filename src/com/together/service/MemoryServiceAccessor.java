package com.together.service;

import com.together.repository.mem.EventMemRepository;

public class MemoryServiceAccessor {


    private EventMemRepository eventMemRepository = new EventMemRepository();
    
    private final static MemoryServiceAccessor memoryServiceAccessor = new MemoryServiceAccessor();
    public static MemoryServiceAccessor getInstance() {
        return memoryServiceAccessor;
    }
    
    /**
     * Return the event Service using the SprintRepository
     * @return
     */
    public EventService getEventService( ) {
        EventService eventService = new EventService();
       eventService.setEventRepository( eventMemRepository );
        return eventService;
    }
}

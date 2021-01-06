package com.together.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.together.data.entity.EventEntity;
import com.together.repository.EventRepository;
import com.together.repository.spring.EventSpringRepository;

@Service
public class EventService extends ToghService {

    
    /*
    @ A u towired
    private EventSpringRepository eventRepository;
*/
    private EventRepository eventRepository;
   
    
    public void setEventRepository( EventRepository eventRepository ) {
        this.eventRepository = eventRepository;
    }
    
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    public EventEntity createEvent( long userId) {
        EventEntity event = new EventEntity(userId, "");
        eventRepository.save(event);
        return event;
        
    }
   public List<EventEntity> getEvents(long userId, String filterEvents) {
       return eventRepository.getEvents( userId,filterEvents );
    }
    
    public EventEntity getEventsById( long userId, long eventId) {
        EventEntity event = eventRepository.getEventById( eventId );
        if (isAllowedUser( userId, event ))
            return event;
        return null; // not allowed !
        
    }
   
    /**
     * User must be the author, or a partipant, or should be invited
     * @param userId
     * @param event
     * @return
     */
    private boolean isAllowedUser( long userId, EventEntity event) {
        return true;
    }
    
}

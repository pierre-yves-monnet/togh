package com.together.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.together.data.entity.EventEntity;
import com.together.repository.EventRepository;
import com.together.repository.spring.EventSpringRepository;

@Service
public class EventService {

    
    private EventRepository eventRepository;

    
    // must not be called
    public EventService() {
    }
    
    
    protected void setEventRepository( EventRepository eventRepository ) {
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
    public List<com.together.data.entity.EventEntity> getMyEvents(Long userId) {
        return eventRepository.getMyEvents( userId );
    }
}

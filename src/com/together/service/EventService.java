package com.together.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.together.entity.EventEntity;
import com.together.entity.EventEntity.StatusEventEnum;
import com.together.entity.EventEntity.TypeEventEnum;
import com.together.repository.EventRepository;

@Service
public class EventService {

    

    @Autowired
    private EventRepository eventRepository;

   
    /*
    public void setEventRepository( EventRepository eventRepository ) {
        this.eventRepository = eventRepository;
    }
    */
    
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    public EventEntity createEvent( long userId) {
        EventEntity event = new EventEntity();
        event.setAuthorId(userId);
        event.setName("New event");
        event.setDatecreation( LocalDateTime.now( ZoneOffset.UTC ));
        event.setStatusEvent(StatusEventEnum.INPREPAR);
        event.setTypeEvent(TypeEventEnum.LIMITED);
        eventRepository.save(event);
        
        return event;
        
    }
   public List<EventEntity> getEvents(long userId, String filterEvents) {

       return eventRepository.findEventsUser( userId );
       
    }
    
    public EventEntity getEventsById( long userId, long eventId) {
        EventEntity event =  eventRepository.findByEventId( eventId );
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

package com.togh.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.controller.RestEventControler;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.event.EventControler;
import com.togh.repository.EventRepository;

@Service
public class EventService {

    

    @Autowired
    private EventRepository eventRepository;


    
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    public EventEntity createEvent( ToghUserEntity user) {
        EventEntity event = new EventEntity();
        event.setAuthor(user);
        event.setName("New event");
        event.setDatecreation( LocalDateTime.now( ZoneOffset.UTC ));
        event.setStatusEvent(StatusEventEnum.INPREPAR);
        event.setTypeEvent(TypeEventEnum.LIMITED);
        EventControler eventControler = new EventControler();
        eventControler.completeConsistant( event );
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
        
        // check consistant now
        EventControler eventControler = new EventControler();
        eventControler.completeConsistant( event );
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

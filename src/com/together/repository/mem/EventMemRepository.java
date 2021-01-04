package com.together.repository.mem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.together.data.entity.EventEntity;
import com.together.data.entity.ParticipantEntity;
import com.together.repository.EventRepository;

public class EventMemRepository implements EventRepository {

    private Map<Long,EventEntity> mapEventEntity = new HashMap<>(); 
    
    public EventMemRepository() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

        mapEventEntity.put(1L, getSimulateEvent(1L, "Christmas","Let's organize the Christmas Evening",  LocalDateTime.parse("2020-12-25 20:00:00",formatter)));
        mapEventEntity.put(2L, getSimulateEvent(1L, "New eve", "Meet for the new eve",  LocalDateTime.parse("2020-12-31 20:00:00",formatter)));
        mapEventEntity.put(3L, getSimulateEvent(3L, "Party in January","We miss the January event, so let's meet now",  LocalDateTime.parse("2021-01-12 10:20:00",formatter)));
    }
    @Override
    public List<EventEntity> getEvents(long userId, String filterEvent) {
        List<EventEntity> listEvent = new ArrayList<>();
        for (EventEntity event : mapEventEntity.values())
        {
            if (event.getAuthorId().equals( userId)) {
                listEvent.add( event );
            }
        }
        return listEvent;
    }

    @Override
    public EventEntity getEventById(long eventId) {
        for (EventEntity event : mapEventEntity.values())
        {
            if (event.getId().equals( eventId)) {
                return event;
            }
        }
        return null;
    }
    
    
    @Override
    public void save(EventEntity event) {
        mapEventEntity.put( event.getId(), event);
        
    }

    @Override
    public List<EventEntity> getInvolvedEvents(long userId) {
        // let search all event where I'm the author, or I have involved in
        List<EventEntity> listEvent = new ArrayList<>();
        for (EventEntity event : mapEventEntity.values())
        {
            if (event.getAuthorId().equals( userId)) {
                listEvent.add( event );
            } else
            {
                for (ParticipantEntity participant : event.getPartipants()) {
                    if (participant.getEndUser().getId().equals( userId ))
                        listEvent.add( event );
                }
            }
        }
        return listEvent;
        
    }

    @Override
    public List<EventEntity> getInvolvedActifEvents(long userId) {
        // let search all event where I'm the author, or I have involved in
        List<EventEntity> listEvent = new ArrayList<>();
        for (EventEntity event : mapEventEntity.values())
        {
            if (! event.isActif())
                continue;
            
            if (event.getAuthorId().equals( userId)) {
                listEvent.add( event );
            } else
            {
                for (ParticipantEntity participant : event.getPartipants()) {
                    if (participant.getEndUser().getId().equals( userId ))
                        listEvent.add( event );
                }
            }
        }
        return listEvent;
    }

    
    private EventEntity getSimulateEvent(long authorId, String eventName,String description, LocalDateTime dateEvent) {
        EventEntity eventEntity = new EventEntity(authorId, eventName );
        eventEntity.setDescription(description);

        return eventEntity;
        
    }
  
 
}

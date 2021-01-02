package com.together.repository.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.together.data.entity.EventEntity;
import com.together.data.entity.ParticipantEntity;
import com.together.repository.EventRepository;

public class EventMemRepository implements EventRepository {

    private Map<Long,EventEntity> mapEventEntity = new HashMap<>(); 
    
    
    @Override
    public List<EventEntity> getMyEvents(Long userId) {
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
    public void save(EventEntity event) {
        mapEventEntity.put( event.getId(), event);
        
    }

    @Override
    public List<EventEntity> getInvolvedEvents(Long userId) {
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
    public List<EventEntity> getInvolvedActifEvents(Long userId) {
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

}

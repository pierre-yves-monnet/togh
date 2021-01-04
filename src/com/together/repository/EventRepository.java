package com.together.repository;

import java.util.List;

import com.together.data.entity.EventEntity;

public interface EventRepository {

    public List<EventEntity> getEvents(long userId, String filterEvent);
    
    
    public EventEntity getEventById(long eventId);
    
    /**
     * Get any event where I'm involved, as a Author or a Participants
     * @param userId
     * @return
     */
    public List<EventEntity> getInvolvedEvents(long userId);
    
    /**
     * Get any actif event where I'm involved, as a Author or a Participants
     * @param userId
     * @return
     */
    public List<EventEntity> getInvolvedActifEvents(long userId);

    
    
    public void save(EventEntity event);

}

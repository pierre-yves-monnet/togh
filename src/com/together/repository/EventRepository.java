package com.together.repository;

import java.util.List;

import com.together.data.entity.EventEntity;

public interface EventRepository {

    public List<EventEntity> getMyEvents(Long userId);
    
    /**
     * Get any event where I'm involved, as a Author or a Participants
     * @param userId
     * @return
     */
    public List<EventEntity> getInvolvedEvents(Long userId);
    
    /**
     * Get any actif event where I'm involved, as a Author or a Participants
     * @param userId
     * @return
     */
    public List<EventEntity> getInvolvedActifEvents(Long userId);

    public void save(EventEntity event);

}

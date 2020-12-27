package com.together.repository;

import java.util.List;

import com.together.data.entity.EventEntity;

public interface EventRepository {

    public List<EventEntity> getEvents(Long userId);
    
    public void save(EventEntity event);

}

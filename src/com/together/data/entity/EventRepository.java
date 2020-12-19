package com.together.data.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component("EventRepository")
public interface  EventRepository extends CrudRepository<EventEntity, Long> {
    
    List<EventEntity> getByUsers(Long userId);

}

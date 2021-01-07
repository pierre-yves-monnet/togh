package com.together.repository;

import java.util.List;

import com.together.entity.EventEntity;


/*
 * This is the EventRepository, Spring implementation.
 * In Spring, the implementation is a interface, and should extends the CrudRepository. Then the service method use the @autowired to generate the class
 */
// extends EventRepository, CrudRepository<EventEntity, Long> JPARepository
public interface EventRepository extends UserEntityRepository<EventEntity>  {
    // public EventEntity getEventById( Long id );
    
    
    // public List<EventEntity> getEvents( Long userId );
}

package com.together.repository.spring;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.together.data.entity.EventEntity;


/*
 * This is the EventRepository, Spring implementation.
 * In Spring, the implementation is a interface, and should extends the CrudRepository. Then the service method use the @autowired to generate the class
 */
// extends EventRepository, CrudRepository<EventEntity, Long> JPARepository
@Component
public interface EventSpringRepository extends CrudRepository<EventEntity, Long>  {
    
 
    public List<EventEntity> getMyEvents(Long userId);

    List<EventEntity> getByUsers(Long userId);

    
   
    

}

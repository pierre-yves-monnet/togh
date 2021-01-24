package com.togh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.togh.entity.EventEntity;


/*
 * This is the EventRepository, Spring implementation.
 * In Spring, the implementation is a interface, and should extends the CrudRepository. Then the service method use the @autowired to generate the class
 */
// extends EventRepository, CrudRepository<EventEntity, Long> JPARepository
public interface EventRepository extends JpaRepository<EventEntity, Long>  {
    
    @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
    EventEntity findByEventId( @Param("id") Long id );

    EventEntity findByName( @Param("name") String name );
    
    
    @Query("SELECT e FROM EventEntity e WHERE e.authorId = :userid")
    List<EventEntity> findEventsUser( @Param("userid") Long userId );
}

package com.togh.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;


/*
 * This is the EventRepository, Spring implementation.
 * In Spring, the implementation is a interface, and should extends the CrudRepository. Then the service method use the @autowired to generate the class
 */
// extends EventRepository, CrudRepository<EventEntity, Long> JPARepository
public interface EventRepository extends JpaRepository<EventEntity, Long>  {
    
    @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
    EventEntity findByEventId( @Param("id") Long id );

    EventEntity findByName( @Param("name") String name );
    
    
    @Query("SELECT e FROM EventEntity e, ToghUserEntity t WHERE e.author = t and t.id = :userid order by e.datecreation")
    List<EventEntity> findMyEventsUser( @Param("userid") Long userId );

    @Query("SELECT e FROM EventEntity e,  ToghUserEntity t join e.participantList p where p.user = t and t.id = :userid order by e.datecreation")
    List<EventEntity> findEventsUser( @Param("userid") Long userId );

    @Query("SELECT count(e) FROM EventEntity e, ToghUserEntity t "
            + "JOIN e.participantList p "
            + "WHERE p.user = t and t.id = :userid and p.role = :role and e.datecreation > :datecreationevent")    
    Long countLastEventsUser( @Param("userid") Long userId, @Param("role") ParticipantRoleEnum role, @Param("datecreationevent") LocalDateTime dateCreationEvent );

}

package com.togh.repository;

import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/*
 * This is the EventRepository, Spring implementation.
 * In Spring, the implementation is an interface, and should extend the CrudRepository. Then the service method use the @autowired to generate the class
 */
// extends EventRepository, CrudRepository<EventEntity, Long> JPARepository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

  @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
  EventEntity findByEventId(@Param("id") Long id);

  EventEntity findByName(@Param("name") String name);

  @Query("SELECT e FROM EventEntity e, ToghUserEntity t join e.participantList p where p.user = t and t.id = :userid and e.statusEvent not in ('CLOSED','CANCELLED') order by e.dateCreation")
  List<EventEntity> findInProgressEventsUser(@Param("userid") Long userId);

  @Query("SELECT e FROM EventEntity e, ToghUserEntity t WHERE e.author = t and t.id = :userid order by e.dateCreation desc")
  List<EventEntity> findMyInProgressEventsUser(@Param("userid") Long userId);

  @Query("SELECT e FROM EventEntity e, ToghUserEntity t join e.participantList p where p.user = t and t.id = :userid order by e.dateCreation")
  List<EventEntity> findEventsUser(@Param("userid") Long userId);


  @Query("SELECT e FROM EventEntity e,  ToghUserEntity t join e.participantList p where p.user = t and p.status= :statusParticipant and t.id = :userid order by e.dateCreation")
  List<EventEntity> findEventsUserByStatusParticipant(@Param("userid") Long userId, @Param("statusParticipant") ParticipantEntity.StatusEnum statusParticipant);

  @Query("SELECT count(e) FROM EventEntity e, ToghUserEntity t "
      + "JOIN e.participantList p "
      + "WHERE p.user = t and t.id = :userid and p.role = :role and e.dateCreation > :dateCreationEvent")
  Long countLastEventsUser(@Param("userid") Long userId, @Param("role") ParticipantRoleEnum role, @Param("dateCreationEvent") LocalDateTime dateCreationEvent);

  /**
   * Search all past event OR, if the event is just modified in the last X time, then it survived
   *
   * @param dateLimit       if the event is created before this date, it is considered as old (except for the dateGrace mechanism)
   * @param dateGrace       if modification is AFTER this date, it is not considered as an Old event
   * @param notEqualsStatus status must not be equals to this status
   * @return the list of old event
   */
  @Query("SELECT e FROM EventEntity e "
      + "WHERE ("
      + " (e.datePolicy = 'ONEDATE' and e.dateEvent < :dateLimit)"
      + " or (e.datePolicy = 'PERIOD' and e.dateEndEvent < :dateLimit)"
      + ") and e.dateModification < :dateGrace "
      + " and e.statusEvent != :status")
  List<EventEntity> findOldEvents(@Param("dateLimit") LocalDateTime dateLimit, @Param("dateGrace") LocalDateTime dateGrace, @Param("status") StatusEventEnum notEqualsStatus, Pageable pageable);


  @Query("SELECT e FROM EventEntity e join e.participantList p "
      + "WHERE p.id = :participantId")
  EventEntity findByParticipant(@Param("participantId") Long participantId);

  /**
   * Search all past event OR, if the event is just modified in the last X time, then it survived
   *
   * @param datePurge time under when
   * @param status    status used to search event to purge
   * @param pageable  pageable information
   * @return a list of entity to purge
   */
  @Query("SELECT e FROM EventEntity e "
      + "WHERE e.dateModification < :datePurge "
      + " and e.statusEvent = :status ")
  List<EventEntity> findEventsToPurge(@Param("datePurge") LocalDateTime datePurge, @Param("status") StatusEventEnum status, Pageable pageable);

}

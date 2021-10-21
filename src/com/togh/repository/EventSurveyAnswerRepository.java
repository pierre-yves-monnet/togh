/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventSurveyAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/* ******************************************************************************** */
/*                                                                                  */
/* EventSurveyRepository */
/*                                                                                  */
/* Control what's happen on an event. Pilot all operations */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventSurveyAnswerRepository extends JpaRepository<EventSurveyAnswerEntity, Long> {

    public EventSurveyAnswerEntity findById(long id);

    @Query("SELECT e FROM EventSurveyEntity s join s.answerlist e join e.whoId t " +
            " WHERE s.id = :surveyId " +
            " and t.id = :whoId")
    List<EventSurveyAnswerEntity> findBySurveyAndWhoId(@Param("surveyId") Long surveyId, @Param("whoId") Long whoId);

}

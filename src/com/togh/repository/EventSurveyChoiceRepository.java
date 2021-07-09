/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.togh.entity.EventSurveyChoiceEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* EventSurveyChoiceRepository */
/*                                                                                  */
/* Control what's happen on an event. Pilot all operations */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventSurveyChoiceRepository extends JpaRepository<EventSurveyChoiceEntity, Long> {

    public EventSurveyChoiceEntity findById(long id);

}

/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.togh.entity.EventSurveyAnswerEntity;


/* ******************************************************************************** */
/*                                                                                  */
/*  EventSurveyRepository                                                             */
/*                                                                                  */
/*  Control what's happen on an event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventSurveyAnswerRepository extends JpaRepository<EventSurveyAnswerEntity, Long>  {

    public EventSurveyAnswerEntity findById(long id);

}
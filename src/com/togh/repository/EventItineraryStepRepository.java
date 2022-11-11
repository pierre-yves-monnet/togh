/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventItineraryStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventItineraryStepRepository */
/*                                                                                  */
/* Control what's happen on an event. Pilot all operations */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventItineraryStepRepository extends JpaRepository<EventItineraryStepEntity, Long> {

  EventItineraryStepEntity findById(long id);

}

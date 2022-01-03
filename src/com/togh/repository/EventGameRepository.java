/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project                                                                     */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventGameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventGameRepository                                                              */
/*                                                                                  */
/* Save a game                                                                      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventGameRepository extends JpaRepository<EventGameEntity, Long> {

    EventGameEntity findById(long id);

}


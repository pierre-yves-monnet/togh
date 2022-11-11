/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventExpenseRepository */
/*                                                                                  */
/* Search expense */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventExpenseRepository extends JpaRepository<EventExpenseEntity, Long> {

  EventExpenseEntity findById(long id);
}

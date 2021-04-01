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

import com.togh.entity.EventExpenseEntity;


/* ******************************************************************************** */
/*                                                                                  */
/*  EventExpenseRepository                                                          */
/*                                                                                  */
/*  Search expense                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventExpenseRepository extends JpaRepository<EventExpenseEntity, Long>  {

    public EventExpenseEntity findById(long id);
}

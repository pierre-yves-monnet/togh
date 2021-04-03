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

import com.togh.entity.EventShoppingListEntity;


/* ******************************************************************************** */
/*                                                                                  */
/*  EventShoppingListRepository                                                             */
/*                                                                                  */
/*  Control what's happen on an event. Pilot all operations                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventShoppingListRepository extends JpaRepository<EventShoppingListEntity, Long>  {

    public EventShoppingListEntity findById(long id);

}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventGroupChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventGroupChatRepository */
/*                                                                                  */
/* Search Group Chat */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventGroupChatRepository extends JpaRepository<EventGroupChatEntity, Long> {

  EventGroupChatEntity findById(long id);
}

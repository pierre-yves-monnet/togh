/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.repository;

import com.togh.entity.EventChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* EventChatEntity */
/*                                                                                  */
/* Chat message */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventChatRepository extends JpaRepository<EventChatEntity, Long> {

    public EventChatEntity findById(long id);
}

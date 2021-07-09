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

import com.togh.entity.EventGroupChatEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* EventGroupChatRepository */
/*                                                                                  */
/* Search Group Chat */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public interface EventGroupChatRepository extends JpaRepository<EventGroupChatEntity, Long> {

    public EventGroupChatEntity findById(long id);
}

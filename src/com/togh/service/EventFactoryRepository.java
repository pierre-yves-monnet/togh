/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.togh.repository.EventChatRepository;
import com.togh.repository.EventGroupChatRepository;
import com.togh.repository.EventItineraryStepRepository;
import com.togh.repository.EventRepository;
import com.togh.repository.EventShoppingListRepository;
import com.togh.repository.EventTaskRepository;

/* ******************************************************************************** */
/*                                                                                  */
/* Accces all repository, */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class EventFactoryRepository {
    
    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public EventItineraryStepRepository eventItineraryStepRepository; 
    
    @Autowired
    public EventTaskRepository eventTaskRepository;
    
 
    @Autowired
    public EventChatRepository eventChatRepository;

    @Autowired
    public EventGroupChatRepository eventGroupChatRepository;

    @Autowired
    public EventShoppingListRepository eventShoppingListRepository;



}

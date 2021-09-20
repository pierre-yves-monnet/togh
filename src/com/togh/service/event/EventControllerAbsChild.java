/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.entity.EventEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventFactoryRepository;
import com.togh.service.EventService;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

/* ******************************************************************************** */
/*                                                                                  */
/* EventControllerIntChild */
/*                                                                                  */
/* All controller child implement this class.
 * The manage one Event, but does not manage a child entity. Then, all method must  */
/* pass the ChildEntity                                                             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public abstract class EventControllerAbsChild {

    private final EventController eventController;
    
    private EventEntity eventEntity;
    
    private int maxEntity=100;
 
    
    protected static final LogEvent eventEntityNotFoundToRemove = new LogEvent(EventControllerAbsChild.class.getName(), 1, Level.INFO, "Entity not found to remove", "This Entity can't be found, already removed");

    
    protected EventControllerAbsChild( EventController eventController, EventEntity eventEntity) {
        this.eventController = eventController;
        this.eventEntity = eventEntity;
    }

    /**
     * Create a new ChildEntity. Object is created, not saved in the database.
     * @param updateContext TODO
     * @param slabOperation TODO
     * @param eventOperationResult
     * @param event
     * @return entity created
     */
    public abstract BaseEntity createEntity( UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult);
   
    /**
     * add the entity in the database
     * @param childEntity Entity to save
     * @param slabOperation TODO
     * @param eventOperationResult. LogEvent may be updated in case of error
     * @return the baseEntiy, which may be has modified (persistenceid is updated)
     */
    public abstract BaseEntity addEntity( BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult);
    
    /**
     * Get the entity by it's id 
     * @param entityId
     * @return
     */
    public abstract BaseEntity getEntity( long entityId );
    
    /**
     * Save the entity transported by the controller
     * @param slabOperation TODO
     * @param eventOperationResult. LogEvent may be updated in case of error
     * @return the baseEntiy, which may be has modified (persistenceid is updated)
     */
    public abstract BaseEntity updateEntity( BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult);

    /**
     * Remove the given entity
     * @param entity
     * @return
     */
    public abstract void removeEntity( BaseEntity childEntity, EventOperationResult eventOperationResult );
    
    /**
     * The controller return the type limit acceptable.
     * By default, the maxEntity is not knoz, and had to be calculated outside.
     * So, before any save call, the setLimitNumber has to be called
     * 
     * @return
     */
    public abstract LimitReach getLimitReach();

    
    
    public void setMaxEntity(int maxEntity) {
        this.maxEntity = maxEntity;
    }
    public int getMaxEntity() {
        return maxEntity;        
    }
    
 
    
    /* ******************************************************************************** */
    /*                                                                                  */
    /* getter */
    /*                                                                                  */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    public EventEntity getEventEntity() {
        return eventEntity;
    }
    
    
    
    public EventController getEventController() {
        return eventController;
    }
    
    public EventService getEventService() {
        return eventController.getEventService();
    }
    
    public EventFactoryRepository getFactoryRepository() {
        return eventController.getFactoryRepository();
    }


    

}
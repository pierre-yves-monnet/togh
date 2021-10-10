/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.entity.EventEntity;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

/* ******************************************************************************** */
/*                                                                                  */
/* EventControllerTask, */
/*                                                                                  */
/* Decompose the EventController class */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventControllerTask extends EventControllerAbsChild {


    protected EventControllerTask(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.TASKLIST;

    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return getEventEntity().getTaskList().size() >= getMaxEntity();
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventTaskEntity());

    }

    /**
     * Save the entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        eventOperationResult.reachTheLimit = getEventEntity().getTaskList().size() >= getMaxEntity();
        if (eventOperationResult.reachTheLimit)
            return null;
        getFactoryRepository().eventTaskRepository.save((EventTaskEntity) childEntity);
        return childEntity;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventTaskRepository.delete((EventTaskEntity) childEntity);
        getEventEntity().removeTask((EventTaskEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());

    }
  
    @Override
    public BaseEntity getEntity( long entityId ) {
        return getFactoryRepository().eventTaskRepository.findById( entityId );
    }
  
    /*
     * Add a entity in the event Entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventTaskRepository.save((EventTaskEntity) childEntity);
        getEventEntity().addTask((EventTaskEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
        return childEntity;
    }
}

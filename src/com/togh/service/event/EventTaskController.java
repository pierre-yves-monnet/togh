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

public class EventTaskController extends EventAbsChildController {


  protected EventTaskController(EventController eventController, EventEntity eventEntity) {
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
  public BaseEntity getEntity(long entityId) {
    return getFactoryRepository().eventTaskRepository.findById(entityId);
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

  /**
   * Database may return a constraint error, because 2 threads try to do the same operation at the same time.
   * So, the server has to deal with that. One solution is to retrieve the current record saved in the database, and return it
   *
   * @param childEntity          child Entity to insert
   * @param slabOperation        slab operation in progress
   * @param eventOperationResult eventOperationResult
   * @return the correct entity, which may be the existing entity in the database
   */
  @Override
  public BaseEntity manageConstraint(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
    return null;
  }


}

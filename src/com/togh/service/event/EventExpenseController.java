/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

/* ******************************************************************************** */
/*                                                                                  */
/* EventControllerExpense, */
/*                                                                                  */
/* Manage expense */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import com.togh.entity.EventEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.SubscriptionService;

public class EventExpenseController extends EventAbsChildController {

  protected EventExpenseController(EventController eventController, EventEntity eventEntity) {
    super(eventController, eventEntity);
  }

  @Override
  public boolean isAtLimit(EventService.UpdateContext updateContext) {
    return false;
  }

  @Override
  public EventEntityPlan createEntity(EventService.UpdateContext updateContext, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
  }

  @Override
  public BaseEntity addEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
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
  public BaseEntity manageConstraint(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
  }

  @Override
  public BaseEntity getEntity(long entityId) {
    return null; // not yet necessary
  }

  @Override
  public BaseEntity updateEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return childEntity; // not yet mandatory
  }

  @Override
  public void removeEntity(BaseEntity childEntity, EventService.EventOperationResult eventOperationResult) {
    // not managed at this moment
  }

  @Override
  public SubscriptionService.LimitReach getLimitReach() {
    return null;
  }


}

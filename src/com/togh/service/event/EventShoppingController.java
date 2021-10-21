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
import com.togh.entity.EventShoppingListEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

/* ******************************************************************************** */
/*                                                                                  */
/* Controller on the shopping list */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
public class EventShoppingController extends EventAbsChildController {


    protected EventShoppingController(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);

    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.SHOPPING;
    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return getEventEntity().getShoppingList().size() >= getMaxEntity();
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventShoppingListEntity());
    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return getFactoryRepository().eventShoppingListRepository.findById(entityId);
    }

    @Override
    public BaseEntity updateEntity(BaseEntity shoppingEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventShoppingListRepository.save((EventShoppingListEntity) shoppingEntity);
        return shoppingEntity;
    }

    public void removeEntity(BaseEntity shoppingEntity, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventShoppingListRepository.delete((EventShoppingListEntity) shoppingEntity);
        getEventEntity().removeShoppingList((EventShoppingListEntity) shoppingEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
    }

    @Override
    public BaseEntity addEntity(BaseEntity shoppingEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventShoppingListRepository.save((EventShoppingListEntity) shoppingEntity);
        getEventEntity().addShoppingList((EventShoppingListEntity) shoppingEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
        return shoppingEntity;

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

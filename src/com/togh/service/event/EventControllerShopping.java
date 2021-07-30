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
public class EventControllerShopping extends EventControllerAbsChild {

  
    protected EventControllerShopping(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);

    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.SHOPPING;
    }

    @Override
    public BaseEntity createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventShoppingListEntity();
    }

    @Override
    public BaseEntity getEntity( long entityId ) {
        return getFactoryRepository().eventShoppingListRepository.findById( entityId );
    }
    
    @Override
    public BaseEntity updateEntity(BaseEntity shoppingEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        eventOperationResult.reachTheLimit = getEventEntity().getShoppingList().size() >= getMaxEntity();
        if (eventOperationResult.reachTheLimit)
            return null;
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

}

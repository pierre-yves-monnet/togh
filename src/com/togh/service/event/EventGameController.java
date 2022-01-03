/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project                                                                     */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.entity.EventEntity;
import com.togh.entity.EventGameEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.SubscriptionService;

public class EventGameController extends EventAbsChildController {


    protected EventGameController(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public boolean isAtLimit(EventService.UpdateContext updateContext) {
        return getEventEntity().getGameList().size() >= getMaxEntity();
    }

    @Override
    public EventEntityPlan createEntity(EventService.UpdateContext updateContext, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        EventGameEntity gameEntity = new EventGameEntity();
        // there is only one type as this moment: so set it by default
        gameEntity.setTypeGame(EventGameEntity.TypeGameEnum.SECRETSANTAS);
        gameEntity.setAdminShowList(false);
        return new EventEntityPlan(gameEntity);
    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return getFactoryRepository().eventGameRepository.findById(entityId);
    }

    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGameRepository.save((EventGameEntity) childEntity);
        return childEntity;
    }

    @Override
    public BaseEntity addEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGameRepository.save((EventGameEntity) childEntity);
        getEventEntity().addGame((EventGameEntity) childEntity);
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
    public BaseEntity manageConstraint(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        return null;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventService.EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGameRepository.delete((EventGameEntity) childEntity);
        getEventEntity().removeGame((EventGameEntity) childEntity);
    }

    @Override
    public SubscriptionService.LimitReach getLimitReach() {
        return SubscriptionService.LimitReach.GAME;
    }

    /**
     * Some game are based on the participants list, giving for each participant an another participant (for a gift, to find hime, to play with him...)
     *
     * @return the EventGameParticipantController
     */
    public EventGameParticipantController getEventPartipantController(EventGameEntity gameEntity) {
        if (gameEntity.getTypeGame() == EventGameEntity.TypeGameEnum.SECRETSANTAS)
            return new EventGameParticipantController(getEventController(), getEventEntity(), gameEntity);
        return null;
    }

}
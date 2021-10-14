package com.togh.service.event;

import com.togh.entity.EventEntity;
import com.togh.entity.EventSurveyAnswerEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

public class EventSurveyAnswerController extends EventAbsChildController {


    EventSurveyController eventSurveyController;

    protected EventSurveyAnswerController(EventController eventController, EventSurveyController eventSurveyController, EventEntity eventEntity) {
        super(eventController, eventEntity);
        this.eventSurveyController = eventSurveyController;
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.SURVEYCHOICE;
    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return false;
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventSurveyAnswerEntity());
    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return null;
    }

    /**
     * Save the entity
     * Entity is then saved, and can be modified (persistenceId is created)
     */
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slab, EventOperationResult eventOperationResult) {
        // no limitation control on the answer list
        BaseEntity surveyEntity = getEventController().localise(getEventEntity(), slab.localisation);
        if (!(surveyEntity instanceof EventSurveyEntity))
            return null;

        childEntity = eventSurveyController.addSurveyAnswser((EventSurveyEntity) surveyEntity, (EventSurveyAnswerEntity) childEntity);
        return childEntity;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        // not supported at this moment
    }

    /*
     * Add an entity in the event.
     * Entity is then saved, and can be modified (persistenceId is created)
     * @param childEntity Entity to add
     * @param slab Operation at the origin
     * @param eventOperationResult save the result of the operation
     * @return the entity modified
     */
    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slab, EventOperationResult eventOperationResult) {
        getFactoryRepository().surveyAnswerRepository.save((EventSurveyAnswerEntity) childEntity);
        BaseEntity surveyEntity = getEventController().localise(getEventEntity(), slab.localisation);
        if (!(surveyEntity instanceof EventSurveyEntity))
            return null;

        childEntity = eventSurveyController.addSurveyAnswser((EventSurveyEntity) surveyEntity, (EventSurveyAnswerEntity) childEntity);
        return childEntity;
    }


}
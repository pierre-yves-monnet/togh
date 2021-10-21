package com.togh.service.event;


import com.togh.entity.EventEntity;
import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

public class EventSurveyChoiceController extends EventAbsChildController {


    EventSurveyController eventSurveyController;

    protected EventSurveyChoiceController(EventController eventController, EventSurveyController eventSurveyController, EventEntity eventEntity) {
        super(eventController, eventEntity);
        this.eventSurveyController = eventSurveyController;
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.CHATGROUP;
    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return false;
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventSurveyChoiceEntity());

    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return null;
    }

    /**
     * Save the entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slab, EventOperationResult eventOperationResult) {
        BaseEntity surveyEntity = getEventController().localise(getEventEntity(), slab.localisation);
        if (!(surveyEntity instanceof EventSurveyEntity)) {
            return null;
        }
        if (((EventSurveyEntity) surveyEntity).getChoicelist().size() >= getMaxEntity())
            eventOperationResult.reachTheLimit = true;
        else {
            childEntity = eventSurveyController.addSurveyChoice((EventSurveyEntity) surveyEntity, (EventSurveyChoiceEntity) childEntity);
        }
        return childEntity;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        // not supported at this moment
    }

    /*
     * Add a entity in the event Entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slab, EventOperationResult eventOperationResult) {
        getFactoryRepository().surveyChoiceRepository.save((EventSurveyChoiceEntity) childEntity);
        BaseEntity surveyEntity = getEventController().localise(getEventEntity(), slab.localisation);
        if (!(surveyEntity instanceof EventSurveyEntity))
            return null;

        childEntity = eventSurveyController.addSurveyChoice((EventSurveyEntity) surveyEntity, (EventSurveyChoiceEntity) childEntity);
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
        return childEntity;
    }

}

package com.togh.service.event;


import com.togh.entity.EventEntity;
import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

public class eventControllerSurveyChoiceList extends EventControllerAbsChild {

  
    EventControllerSurvey eventControllerSurvey;

    protected eventControllerSurveyChoiceList(EventController eventController, EventControllerSurvey eventControllerSurvey, EventEntity eventEntity) {
        super(eventController, eventEntity);
        this.eventControllerSurvey = eventControllerSurvey;
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.CHATGROUP;

    }

    @Override
    public BaseEntity createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventSurveyChoiceEntity();

    }

    @Override
    public BaseEntity getEntity( long entityId ) {
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
            childEntity = eventControllerSurvey.addSurveyChoice((EventSurveyEntity) surveyEntity, (EventSurveyChoiceEntity) childEntity);
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
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        return null; // only by the
    }
}

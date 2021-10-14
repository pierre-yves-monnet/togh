package com.togh.service.event;

import com.togh.engine.logevent.LogEvent;
import com.togh.entity.EventEntity;
import com.togh.entity.EventSurveyAnswerEntity;
import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventSurveyController extends EventAbsChildController {


    protected EventSurveyController(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return getEventEntity().getSurveyList().size() >= getMaxEntity();
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventSurveyEntity());
    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return getFactoryRepository().eventSurveyRepository.findById(entityId);
    }

    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventSurveyRepository.save((EventSurveyEntity) childEntity);
        return childEntity;
    }

    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventSurveyRepository.save((EventSurveyEntity) childEntity);
        getEventEntity().addSurvey((EventSurveyEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
        return childEntity;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventSurveyRepository.delete((EventSurveyEntity) childEntity);
        getEventEntity().removeSurvey((EventSurveyEntity) childEntity);
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.SURVEY;
    }

    public EventSurveyChoiceEntity addSurveyChoice(EventSurveyEntity surveyEntity, EventSurveyChoiceEntity surveyChoice) {
        getFactoryRepository().surveyChoiceRepository.save(surveyChoice);
        List<EventSurveyChoiceEntity> choicelist = surveyEntity.getChoicelist();
        choicelist.add(surveyChoice);
        surveyEntity.setChoicelist(choicelist);
        getFactoryRepository().eventSurveyRepository.save(surveyEntity);
        return surveyChoice;
    }

    public EventSurveyAnswerEntity addSurveyAnswser(EventSurveyEntity surveyEntity, EventSurveyAnswerEntity surveyAnswerEntity) {
        getFactoryRepository().surveyAnswerRepository.save(surveyAnswerEntity);
        List<EventSurveyAnswerEntity> answerlist = surveyEntity.getAnswerlist();
        answerlist.add(surveyAnswerEntity);
        surveyEntity.setAnswerlist(answerlist);
        getFactoryRepository().eventSurveyRepository.save(surveyEntity);
        // do not need to save the evententity
        return surveyAnswerEntity;
    }

    public List<LogEvent> removeSurveyChoice(EventSurveyEntity surveyEntity, Long choiceId) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        Optional<EventSurveyChoiceEntity> choice = getFactoryRepository().surveyChoiceRepository.findById(choiceId);
        if (choice.isPresent()) {
            getFactoryRepository().surveyChoiceRepository.delete(choice.get());
            List<EventSurveyChoiceEntity> choicelist = surveyEntity.getChoicelist();
            choicelist.remove(choice.get());
            surveyEntity.setChoicelist(choicelist);
        } else {
            listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find choiceId[" + choiceId + "]"));
        }

        return listLogEvent;
    }


}

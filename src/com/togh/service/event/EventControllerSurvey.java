package com.togh.service.event;

import com.togh.engine.logevent.LogEvent;
import com.togh.entity.EventEntity;
import com.togh.entity.EventSurveyAnswerEntity;
import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.repository.EventRepository;
import com.togh.repository.EventSurveyAnswerRepository;
import com.togh.repository.EventSurveyChoiceRepository;
import com.togh.repository.EventSurveyRepository;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventControllerSurvey extends EventControllerAbsChild {

    @Autowired
    private EventSurveyRepository eventSurveyRepository;

    @Autowired
    private EventSurveyAnswerRepository surveyAnswerRepository;

    @Autowired
    private EventSurveyChoiceRepository surveyChoiceRepository;

    @Autowired
    private EventRepository eventRepository;

    protected EventControllerSurvey(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public BaseEntity createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventSurveyEntity();
    }

    @Override
    public BaseEntity getEntity( long entityId ) {
        return eventSurveyRepository.findById( entityId );
    }
    
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        eventOperationResult.reachTheLimit = getEventEntity().getSurveyList().size() >= getMaxEntity();
        if (!eventOperationResult.reachTheLimit)
            eventSurveyRepository.save((EventSurveyEntity) childEntity);
        return childEntity;
    }

    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        eventSurveyRepository.save((EventSurveyEntity) childEntity);
        getEventEntity().addSurvey((EventSurveyEntity) childEntity);
        eventRepository.save(getEventEntity());
        return childEntity;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        eventSurveyRepository.delete((EventSurveyEntity) childEntity);
        getEventEntity().removeSurvey((EventSurveyEntity) childEntity);
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.SURVEY;
    }

    public EventSurveyChoiceEntity addSurveyChoice(EventSurveyEntity surveyEntity, EventSurveyChoiceEntity surveyChoice) {
        surveyChoiceRepository.save(surveyChoice);
        List<EventSurveyChoiceEntity> choicelist = surveyEntity.getChoicelist();
        choicelist.add(surveyChoice);
        surveyEntity.setChoicelist(choicelist);
        eventSurveyRepository.save(surveyEntity);
        return surveyChoice;
    }

    public EventSurveyAnswerEntity addSurveyAnswser(EventSurveyEntity surveyEntity, EventSurveyAnswerEntity surveyAnswerEntity) {
        surveyAnswerRepository.save(surveyAnswerEntity);
        List<EventSurveyAnswerEntity> answerlist = surveyEntity.getAnswerlist();
        answerlist.add(surveyAnswerEntity);
        surveyEntity.setAnswerlist(answerlist);
        eventSurveyRepository.save(surveyEntity);
        // do not need to save the evententity
        return surveyAnswerEntity;
    }

    public List<LogEvent> removeSurveyChoice(EventSurveyEntity surveyEntity, Long choiceId) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        Optional<EventSurveyChoiceEntity> choice = surveyChoiceRepository.findById(choiceId);
        if (choice.isPresent()) {
            surveyChoiceRepository.delete(choice.get());
            List<EventSurveyChoiceEntity> choicelist = surveyEntity.getChoicelist();
            choicelist.remove(choice.get());
            surveyEntity.setChoicelist(choicelist);
        } else {
            listLogEvent.add(new LogEvent(eventEntityNotFoundToRemove, "Can't find choiceId[" + choiceId + "]"));
        }

        return listLogEvent;
    }
}

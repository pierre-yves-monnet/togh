package com.togh.service.event;

import com.togh.entity.EventEntity;
import com.togh.entity.EventSurveyAnswerEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

import java.util.List;
import java.util.stream.Collectors;

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
   * Save the Entity, and can be modified (persistenceId is created)
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

    BaseEntity surveyEntity = getEventController().localise(getEventEntity(), slab.localisation);
    if (!(surveyEntity instanceof EventSurveyEntity))
      return null;

    getFactoryRepository().surveyAnswerRepository.save((EventSurveyAnswerEntity) childEntity);
    childEntity = eventSurveyController.addSurveyAnswser((EventSurveyEntity) surveyEntity, (EventSurveyAnswerEntity) childEntity);
    return childEntity;
  }

  /**
   * Database may return a constraint error, because 2 threads try to do the same operation at the same time.
   * So, the server has to deal with that. One solution is to retrieve the current record saved in the database, and return it
   *
   * @param violationChild       child Entity to insert
   * @param slab                 slab operation in progress
   * @param eventOperationResult eventOperationResult
   * @return the correct entity, which may be the existing entity in the database
   */
  @Override
  public BaseEntity manageConstraint(BaseEntity violationChild, Slab slab, EventOperationResult eventOperationResult) {

    // Reload the entity from the database, and replace the entity by this one

    BaseEntity parentEntity = getEventController().localise(getEventEntity(), slab.localisation);
    if (!(parentEntity instanceof EventSurveyEntity))
      return null;

    // find in the list the entity to replace
    // 2 use case: the database entity IS NOT in the list and it must be added
    // OR the database entity IS in the list, and must not be added twice

    int indexEntity = 0;
    EventSurveyEntity surveyEntity = (EventSurveyEntity) parentEntity;


    BaseEntity databaseEntity = reloadSourceFromDatabase(parentEntity, violationChild);
    // the databaseEntity may be null: It's just added in the same transaction then!
    List<? extends BaseEntity> listChildren;
    if (violationChild instanceof EventSurveyAnswerEntity) {
      listChildren = surveyEntity.getAnswerlist();
    } else {
      // Only AnswerList has a constraint
      return null;
    }
    // We have to read again the entity from the database, and use it
    List<? extends BaseEntity> checkExistenceDatabaseEntity = listChildren.stream()
        .filter(t -> t.getId().equals(violationChild.getId()))
        .collect(Collectors.toList());

    while (indexEntity < listChildren.size()) {
      if (listChildren.get(indexEntity) == violationChild) {
        // replace it
        if (!checkExistenceDatabaseEntity.isEmpty())
          ((List<EventSurveyAnswerEntity>) listChildren).set(indexEntity, (EventSurveyAnswerEntity) databaseEntity);
        else
          listChildren.remove(indexEntity);
        break;
      }
      indexEntity++;
    }
    return databaseEntity;
  }

  /**
   * Reload the entity saved in the database. This record is the cause of the constraint, and has to be used
   *
   * @param violationChild
   * @return same object but the one in the database
   */
  private BaseEntity reloadSourceFromDatabase(BaseEntity parentEntity, BaseEntity violationChild) {
    EventSurveyAnswerEntity violationChildSurveyAnswer = (EventSurveyAnswerEntity) violationChild;
    ToghUserEntity whoId = violationChildSurveyAnswer.getWhoId();
    // Retrieve the parent

    if (whoId != null) {
      List<? extends BaseEntity> listDatabase = null;
      if (violationChild instanceof EventSurveyAnswerEntity)
        listDatabase
            = eventSurveyController.getFactoryRepository().surveyAnswerRepository.findBySurveyAndWhoId(parentEntity.getId(), whoId.getId());
      if (listDatabase != null && listDatabase.size() == 1)
        return listDatabase.get(0);
    }
    return null;
  }

}
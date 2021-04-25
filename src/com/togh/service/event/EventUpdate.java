/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.tool.JpaTool;
import com.togh.entity.EventEntity;
import com.togh.entity.EventItineraryStepEntity;
import com.togh.entity.EventShoppingListEntity;
import com.togh.entity.EventSurveyAnswerEntity;
import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.UserEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;


/* ******************************************************************************** */
/*                                                                                  */
/* EventUpdate, */
/*                                                                                  */
/* Manage update of event, from a SlabRecord */
/* A SlabRecord is a simple 'operation' to update part of the event, like the name */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
public class EventUpdate {

    private Logger logger = Logger.getLogger(EventUpdate.class.getName());
    private static final String LOG_HEADER = EventUpdate.class.getSimpleName() + ": ";

    private static final LogEvent eventInvalidUpdateOperation = new LogEvent(EventUpdate.class.getName(), 1, Level.APPLICATIONERROR, "Invalid operation", "This operation failed", "Operation can't be done", "Check error");
    private static final LogEvent eventCantLocalise = new LogEvent(EventUpdate.class.getName(), 2, Level.ERROR, "Can't localise", "A localisation can't be found, maybe the item is deleted by an another user?", "Operation can't be done", "Refresh your event");
    private static final LogEvent eventBadLocalisationEntity = new LogEvent(EventUpdate.class.getName(), 3, Level.ERROR, "Entity found is not the one expected", "An entity with a special type is search, and an another one if found.", "Operation can't be executed", "Check the localisation and the entity found");
    private static final LogEvent eventAlreadyDeleted = new LogEvent(EventUpdate.class.getName(), 4, Level.INFO, "Entity already deleted", "The entity is already deleted");
    

    EventController eventController;

    public enum SlabOperation {
        UPDATE, ADD, REMOVE
    }

    public static class Slab {

        public SlabOperation operation;
        public String attributName;
        public Object attributValue;
        public String localisation;
        public BaseEntity baseEntity = null;

        public Slab(Map<String, Object> record) {
            operation = SlabOperation.valueOf((String) record.get("operation"));
            attributName = (String) record.get("name");
            attributValue = record.get("value");
            localisation = (String) record.get("localisation");
        }

        public Slab(SlabOperation operation, String attributName, String attributValue, BaseEntity baseEntity) {
            this.operation = operation;
            this.attributName = attributName;
            this.attributValue = attributValue;
            this.baseEntity = baseEntity;
        }

        public Long getAttributValueLong() {
            try {
                return Long.parseLong(attributValue.toString());
            } catch (Exception e) {
                return null;
            }
        }
    }

    protected EventUpdate(EventController eventController) {
        this.eventController = eventController;
    }

    public EventOperationResult update(List<Slab> listSlab, UpdateContext updateContext) {
        EventEntity event = this.eventController.getEvent();
        EventOperationResult eventOperationResult = new EventOperationResult();
        for (Slab slab : listSlab) {
            try {
                if (SlabOperation.UPDATE.equals(slab.operation)) {
                    updateOperation(event, slab, updateContext, eventOperationResult);
                } else if (SlabOperation.ADD.equals(slab.operation)) {
                    addOperation(event, slab, updateContext, eventOperationResult);
                } else if (SlabOperation.REMOVE.equals(slab.operation)) {
                    removeOperation(event, slab, eventOperationResult);
                }
            } catch (Exception e) {
                eventOperationResult.addLogEvent(new LogEvent(eventInvalidUpdateOperation, e, slab.operation + ":" + slab.attributName));
            }
        }
        if (!listSlab.isEmpty())
            event.touch();

        return eventOperationResult;
    }

    private void addOperation(EventEntity event, Slab slab, UpdateContext updateContext, EventOperationResult eventOperationResult) {
        BaseEntity child = null;

        if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_TASKLIST)) {
            child = new EventTaskEntity();
        } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_ITINERARYSTEPLIST)) {
            child = new EventItineraryStepEntity();
        } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_SHOPPINGLIST)) {
            child = new EventShoppingListEntity();
        } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_SURVEYLIST)) {
            child = new EventSurveyEntity();
        } else if (slab.attributName.equals(EventSurveyEntity.CST_SLABOPERATION_CHOICELIST)) {
            child = new EventSurveyChoiceEntity();
        } else if (slab.attributName.equals(EventSurveyEntity.CST_SLABOPERATION_ANSWERLIST)) {
            child = new EventSurveyAnswerEntity();
        }
        
        
        
        
        if (child != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> valueDefault = (Map<String, Object>) slab.attributValue;
            for (Entry<String, Object> entrySlab : valueDefault.entrySet()) {
                JpaTool.updateEntityOperation(child, entrySlab.getKey(), entrySlab.getValue(), updateContext, eventOperationResult);
            }
            // save it now
            if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_TASKLIST)) {
                child = eventController.getEventService().addTask(event, (EventTaskEntity) child);
            } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_ITINERARYSTEPLIST)) {
                child = eventController.getEventService().addItineraryStep(event, (EventItineraryStepEntity) child);
            } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_SHOPPINGLIST)) {
                child = eventController.getEventService().addShoppingList(event, (EventShoppingListEntity) child);
            } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_SURVEYLIST)) {
                child = eventController.getEventService().addSurvey(event, (EventSurveyEntity) child);
            } else if (slab.attributName.equals(EventSurveyEntity.CST_SLABOPERATION_CHOICELIST)) {
                BaseEntity surveyEntity = localise(event, slab.localisation);
                if (surveyEntity instanceof EventSurveyEntity)
                    child = eventController.getEventService().addSurveyChoice(event, (EventSurveyEntity) surveyEntity,  (EventSurveyChoiceEntity) child);
        } else if (slab.attributName.equals(EventSurveyEntity.CST_SLABOPERATION_ANSWERLIST)) {
            BaseEntity surveyEntity = localise(event, slab.localisation);
            if (surveyEntity instanceof EventSurveyEntity)
                child = eventController.getEventService().addSurveyAnswser(event, (EventSurveyEntity) surveyEntity,  (EventSurveyAnswerEntity) child);
        }
            if (child!=null)
                eventOperationResult.listChildEntity.add(child);
        }
    }

    /**
     * Remove operation
     * 
     * @param event
     * @param slab
     * @param eventOperationResult
     */
    private void removeOperation(EventEntity event, Slab slab, EventOperationResult eventOperationResult) {

        if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_TASKLIST)) {
            eventOperationResult.listChildEntityId.add(slab.getAttributValueLong());
            eventOperationResult.addLogEvents(eventController.getEventService().removeTask(event, slab.getAttributValueLong()));
        }
        else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_ITINERARYSTEPLIST)) {
            eventOperationResult.listChildEntityId.add(slab.getAttributValueLong());
            eventOperationResult.addLogEvents(eventController.getEventService().removeItineraryStep(event, slab.getAttributValueLong()));
        }
        else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_SHOPPINGLIST)) {
            eventOperationResult.listChildEntityId.add(slab.getAttributValueLong());
            eventOperationResult.addLogEvents(eventController.getEventService().removeShoppingList(event, slab.getAttributValueLong()));
        }
        else if (slab.attributName.equals(EventSurveyEntity.CST_SLABOPERATION_CHOICELIST)) {
            eventOperationResult.listChildEntityId.add(slab.getAttributValueLong());
            BaseEntity surveyEntity = localise(event, slab.localisation);
            if (surveyEntity instanceof EventSurveyEntity)
                eventOperationResult.addLogEvents(eventController.getEventService().removeSurveyChoice(event, (EventSurveyEntity) surveyEntity, slab.getAttributValueLong()));
            else if (surveyEntity ==null) {
                // already deleted
                eventOperationResult.addLogEvent( eventAlreadyDeleted);
            }
            else
                eventOperationResult.addLogEvent( new LogEvent(eventBadLocalisationEntity, "Can't find SurveyEntity localisation["+slab.localisation+"] found ["+(surveyEntity==null ? null : surveyEntity.getClass().getName())));

        }
    }

    /**
     * Update the event Eventity with the slab
     * 
     * @param event
     * @param slab
     * @return
     */
    private void updateOperation(EventEntity event, Slab slab, UpdateContext updateContext, EventOperationResult eventOperationResult) {
        if (slab.localisation == null || slab.localisation.isEmpty())
            JpaTool.updateEntityOperation(event, slab.attributName, slab.attributValue, updateContext, eventOperationResult);
        else {
            BaseEntity baseEntity = localise(event, slab.localisation);
            if (baseEntity != null) {
                JpaTool.updateEntityOperation(baseEntity, slab.attributName, slab.attributValue, updateContext, eventOperationResult);
            } else {
                eventOperationResult.addLogEvent(new LogEvent(eventCantLocalise, "Localisation [" + slab.localisation + "] to update [" + slab.attributName + "]"));
            }

        }
        event.touch();
    }

  

    
    
    
 

    /**
     * Localise the BaseEntity according the localisation. Localisation is a string like "/tasklist/1"
     * 
     * @param baseEntity
     * @param localisation
     * @return
     */
    @SuppressWarnings("unchecked")
    private BaseEntity localise(BaseEntity baseEntity, String localisation) {

        // source is <name>/id/ <<something else 
        if (localisation.isEmpty())
            return baseEntity;
        StringTokenizer stLocalisation = new StringTokenizer(localisation, "/");
        BaseEntity indexEntity = baseEntity;
        try {
            while (stLocalisation.hasMoreTokens()) {
                String nameEntity = stLocalisation.nextToken();

                Method method = JpaTool.searchMethodByName(indexEntity, nameEntity);
                if (method == null)
                    return null;

                // get the object
                Object getObject = method.invoke(indexEntity);
                if (getObject instanceof List) {
                    // then the idEntity take the sens
                    String idEntity = stLocalisation.hasMoreTokens() ? stLocalisation.nextToken() : null;
                    Long idEntityLong = Long.valueOf(idEntity);
                    List<BaseEntity> listChildrenEntity = (List<BaseEntity>) getObject;
                    BaseEntity childEntityById = null;
                    for (BaseEntity child : listChildrenEntity) {
                        if (child.getId().equals(idEntityLong)) {
                            childEntityById = child;
                            break;
                        }
                    }
                    if (childEntityById == null)
                        return null;
                    indexEntity = childEntityById;
                } else if (getObject instanceof BaseEntity)
                    indexEntity = (BaseEntity) getObject;
                else if (getObject == null) {
                    // time to add this object

                    indexEntity = this.eventController.getEventService().add(nameEntity, (UserEntity) indexEntity);
                    if (indexEntity == null)
                        return null;
                }

            }
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't localise item [" + localisation + "] currentIndexItem[" + indexEntity.getClass().getName() + "]");
            return null;
        }
        return indexEntity;
    }

   
}

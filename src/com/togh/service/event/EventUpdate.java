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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.tool.EngineTool;
import com.togh.entity.EventEntity;
import com.togh.entity.EventItineraryStepEntity;
import com.togh.entity.EventShoppingListEntity;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.UserEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.LoadEntityResult;
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
        }
        if (child != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> valueDefault = (Map<String, Object>) slab.attributValue;
            for (Entry<String, Object> entrySlab : valueDefault.entrySet()) {
                updateEntityOperation(child, entrySlab.getKey(), entrySlab.getValue(), updateContext, eventOperationResult);
            }
            // save it now
            if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_TASKLIST)) {
                child = eventController.getEventService().addTask(event, (EventTaskEntity) child);
            } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_ITINERARYSTEPLIST)) {
                child = eventController.getEventService().addItineraryStep(event, (EventItineraryStepEntity) child);
            } else if (slab.attributName.equals(EventEntity.CST_SLABOPERATION_SHOPPINGLIST)) {
                child = eventController.getEventService().addShoppingList(event, (EventShoppingListEntity) child);
            }
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
            updateEntityOperation(event, slab.attributName, slab.attributValue, updateContext, eventOperationResult);
        else {
            BaseEntity baseEntity = localise(event, slab.localisation);
            if (baseEntity != null) {
                updateEntityOperation(baseEntity, slab.attributName, slab.attributValue, updateContext, eventOperationResult);
            } else {
                eventOperationResult.addLogEvent(new LogEvent(eventCantLocalise, "Localisation [" + slab.localisation + "] to update [" + slab.attributName + "]"));
            }

        }
        event.touch();
    }

    /**
     * update an entity
     * 
     * @param event
     * @param slab
     * @return
     */
    @SuppressWarnings("unchecked")
    private void updateEntityOperation(BaseEntity baseEntity, String attributName, Object attributValue, UpdateContext updateContext, EventOperationResult eventOperationResult) {
        Object value = null;
        Method methodAttribut = searchMethodByName(baseEntity, attributName);
        if (methodAttribut == null) {
            eventOperationResult.addLogEvent(new LogEvent(eventInvalidUpdateOperation, attributName + " <="
                    + (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue)));
            return;
        }
        String jpaAttributName = methodAttribut.getName().substring(3);
        // first letter is a lower case
        jpaAttributName = jpaAttributName.substring(0, 1).toLowerCase() + jpaAttributName.substring(1);
        try {
            if (attributValue != null) {

                @SuppressWarnings("rawtypes")
                Class returnType = methodAttribut.getReturnType();
                if (returnType.equals(Double.class)) {
                    value = Double.valueOf(attributValue.toString());

                } else if (returnType.equals(BigDecimal.class)) {
                    value = getBigDecimalFromString(attributValue.toString());

                } else if (returnType.equals(Long.class)) {
                    value = Long.valueOf(attributValue.toString());

                } else if (returnType.equals(LocalDateTime.class)) {
                    value = EngineTool.stringToDateTime(attributValue.toString());
                } else if (returnType.equals(LocalDate.class)) {
                    long timeZoneOffset = updateContext.timeZoneOffset;
                    if (baseEntity.isAbsoluteLocalDate(attributName))
                        timeZoneOffset = 0;
                    value = EngineTool.stringToDate(attributValue.toString(), timeZoneOffset);

                } else if (returnType.equals(String.class)) {
                    value = attributValue.toString();

                } else if (returnType.isEnum()) {
                    value = Enum.valueOf(returnType, attributValue.toString());

                } else if (isClassBaseEntity(returnType)) {
                    LoadEntityResult loadResult = this.eventController.getEventService().loadEntity(returnType, Long.valueOf(attributValue.toString()));
                    value = loadResult.entity;
                    eventOperationResult.listLogEvents.addAll(loadResult.listLogEvents);

                } else
                    value = attributValue;

            }

            PropertyUtils.setSimpleProperty(baseEntity, jpaAttributName, value);
            baseEntity.touch();

        } catch (Exception e) {
            eventOperationResult.listLogEvents.add(new LogEvent(eventInvalidUpdateOperation, e, attributName
                    + " (JPA=" + jpaAttributName + ")"
                    + " <="
                    + (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue)));
        }
    }

    /**
     * IsClassEntity
     */
    private boolean isClassBaseEntity(Class<?> classToStudy) {
        while (classToStudy != null) {
            if (classToStudy.equals(BaseEntity.class))
                return true;
            classToStudy = classToStudy.getSuperclass();
        }
        return false;
    }

    /**
     * value may be a currency, like $33.344. So, remove all non numric expression.
     * 
     * @param valueSt
     * @return
     * @throws Exception
     */
    private BigDecimal getBigDecimalFromString(String valueSt) throws Exception {
        // french is 2 334,44 ===> One comma only
        // americain is 2,334.44 ==> One comma and one point. 
        // so we detect first the format
        int countComma = 0;
        int countDot = 0;
        for (int i = 0; i < valueSt.length(); i++) {
            if (valueSt.charAt(i) == ',')
                countComma++;
            if (valueSt.charAt(i) == '.')
                countDot++;
        }
        NumberFormat numberFormat = null;
        if ((countComma > 0 && countDot > 0) || countDot == 1) {
            // americain format, remove ,
            numberFormat = NumberFormat.getNumberInstance(Locale.US);
        } else {
            numberFormat = NumberFormat.getNumberInstance(Locale.FRANCE);
        }
        StringBuilder valueExpurged = new StringBuilder();
        for (int i = 0; i < valueSt.length(); i++) {
            if (valueSt.charAt(i) >= '0' && valueSt.charAt(i) <= '9')
                valueExpurged.append(valueSt.charAt(i));
            if (valueSt.charAt(i) == '.' || valueSt.charAt(i) == ',')
                valueExpurged.append(valueSt.charAt(i));
        }

        return new BigDecimal(numberFormat.parse(valueExpurged.toString()).toString());
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

                Method method = searchMethodByName(indexEntity, nameEntity);
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

    private Method searchMethodByName(BaseEntity baseEntity, String attributName) {
        String methodName = "get" + attributName;

        for (Method method : baseEntity.getClass().getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName))
                return method;
        }
        return null;
    }
}

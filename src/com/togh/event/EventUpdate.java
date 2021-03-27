/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.event;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.tool.EngineTool;
import com.togh.entity.EventEntity;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.repository.EventTaskRepository;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.LoadEntityResult;
import com.togh.service.EventService;
import com.togh.service.FactoryService;

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
    private static final String logHeader = EventUpdate.class.getSimpleName() + ": ";

    private static final LogEvent eventInvalidUpdateOperation = new LogEvent(EventUpdate.class.getName(), 1, Level.APPLICATIONERROR, "Invalid operation", "This operation failed", "Operation can't be done", "Check error");
    private static final LogEvent eventCantLocalise = new LogEvent(EventUpdate.class.getName(), 2, Level.ERROR, "Can't localise", "A localisation can't be found, maybe the item is deleted by an another user?", "Operation can't be done", "Refresh your event");
    EventController eventController;

    private enum SlabOperation {
        UPDATE, ADD, REMOVE
    }

    private class Slab {

        public SlabOperation operation;
        public String attributName;
        public Object attributValue;
        public String localisation;
        public String typedata;

        protected Slab(Map<String, Object> record) {
            operation = SlabOperation.valueOf((String) record.get("operation"));
            attributName = (String) record.get("name");
            attributValue = record.get("value");
            localisation = (String) record.get("localisation");
            typedata = (String) record.get("typedata");
        }
        public Long getAttributValueLong() {
            try {
                return Long.parseLong( attributValue.toString());
            }catch (Exception e) {
                return null;
            }
        }
    }

    protected EventUpdate(EventController eventController) {
        this.eventController = eventController;
    }

    public EventOperationResult update(List<Map<String, Object>> listSlab) {
        EventEntity event = this.eventController.getEvent();
        EventOperationResult eventOperationResult = new EventOperationResult();
        for (Map<String, Object> recordSlab : listSlab) {
            try {
                Slab slab = new Slab(recordSlab);
                if (SlabOperation.UPDATE.equals(slab.operation)) {
                    updateOperation(event, slab, eventOperationResult);
                } else if (SlabOperation.ADD.equals(slab.operation)) {
                    addOperation(event, slab, eventOperationResult);
                } else if (SlabOperation.REMOVE.equals(slab.operation)) {
                    removeOperation(event, slab, eventOperationResult);
                }
            } catch (Exception e) {
                eventOperationResult.listLogEvents.add(new LogEvent(eventInvalidUpdateOperation, e, recordSlab.get("operation") + ":" + recordSlab.get("name")));
            }
        }
        if (!listSlab.isEmpty())
            event.touch();

        return eventOperationResult;
    }

    private void addOperation(EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        BaseEntity child = null;

        if (slab.attributName.equals("tasklist")) {
            child = eventController.getEventService().addTask(event);
        }
        if (child != null) {
            Map<String, Object> valueDefault = (Map<String, Object>) slab.attributValue;
            for (Entry<String, Object> entrySlab : valueDefault.entrySet()) {
                updateEntityOperation(child, entrySlab.getKey(), entrySlab.getValue(), eventOperationResult);
            }
            eventOperationResult.listChildEntity.add(child);
        }
    }
    
    /**
     * Remove operation
     * @param event
     * @param slab
     * @param eventOperationResult
     */
    private void removeOperation(EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        
        if (slab.attributName.equals("tasklist")) {
            eventOperationResult.listChildEntityId.add(slab.getAttributValueLong());
            eventOperationResult.listLogEvents.addAll( eventController.getEventService().removeTask(event, slab.getAttributValueLong()));
        }
       
    }
    /**
     * Update the event Eventity with the slab
     * 
     * @param event
     * @param slab
     * @return
     */
    private void updateOperation(EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        if (slab.localisation== null || slab.localisation.isEmpty())
            updateEntityOperation(event, slab.attributName, slab.attributValue, eventOperationResult);
        else {
            BaseEntity baseEntity = localise(event, slab.localisation);
            if (baseEntity != null) {
                updateEntityOperation(baseEntity, slab.attributName, slab.attributValue, eventOperationResult);
            } else {
                eventOperationResult.listLogEvents.add(new LogEvent(eventCantLocalise, "Localisation [" + slab.localisation + "] to update [" + slab.attributName + "]"));
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
    private void updateEntityOperation(BaseEntity baseEntity, String attributName, Object attributValue, EventOperationResult eventOperationResult) {

        Object value = null;
        Method methodAttribut = searchMethodByName(baseEntity, attributName);
        if (methodAttribut == null) {
            eventOperationResult.listLogEvents.add(new LogEvent(eventInvalidUpdateOperation, attributName + " <="
                    + (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue)));
            return;
        }
        String jpaAttributName = methodAttribut.getName().substring(3);
        // first letter is a lower case
        jpaAttributName = jpaAttributName.substring(0, 1).toLowerCase() + jpaAttributName.substring(1);

        if (attributValue != null) {

            Class returnType = methodAttribut.getReturnType();
            if (returnType.equals(Double.class)) {
                value = Double.valueOf(attributValue.toString());
            } else if (returnType.equals(Long.class)) {
                value = Long.valueOf(attributValue.toString());
            } else if (returnType.equals(LocalDateTime.class)) {
                value = EngineTool.stringToDate(attributValue.toString());;
            } else if (returnType.equals(String.class)) {
                value = attributValue.toString();
            } else if (returnType.isEnum()) {
                value = Enum.valueOf(returnType, attributValue.toString());
            } else if (isClassBaseEntity(returnType)) {
                LoadEntityResult loadResult = this.eventController.getEventService().loadEntity(returnType, Long.valueOf(attributValue.toString()));
                value=loadResult.entity;
                eventOperationResult.listLogEvents.addAll( loadResult.listLogEvents);
            } else
                value = attributValue;
        }

        try {
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
    private boolean isClassBaseEntity( Class classToStudy) {
        while (classToStudy != null) { 
            if (classToStudy.equals(BaseEntity.class))
                    return true;
            classToStudy =classToStudy.getSuperclass();
        }
        return false;
    }
    /**
     * Localise the BaseEntity according the localisation. Localisation is a string like "/tasklist/1"
     * @param baseEntity
     * @param localisation
     * @return
     */
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
                else
                    return null;
            }
        } catch (Exception e) {
            logger.severe(logHeader + "Can't localise item [" + localisation + "] currentIndexItem[" + indexEntity.getClass().getName() + "]");
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

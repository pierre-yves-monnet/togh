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
    private final static String logHeader = EventUpdate.class.getSimpleName() + ": ";

    @Autowired
    private EventTaskRepository eventTaskRepository;

    private static final LogEvent eventInvalidUpdateOperation = new LogEvent(EventUpdate.class.getName(), 1, Level.APPLICATIONERROR, "Invalid operation", "This operation failed", "Operation can't be done", "Check error");
    EventController eventController;

    private enum SlabOperation {
        UPDATE, ADD
    }

    private class Slab {

        public SlabOperation operation;
        public String attributName;
        public Object attributValue;
        public String localisation;


        protected Slab(Map<String, Object> record) {
            operation = SlabOperation.valueOf((String) record.get("operation"));
            attributName = (String) record.get("name");
            attributValue = record.get("value");
            localisation = (String) record.get("localisation");
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
        return;
    }

    /**
     * Update the event Eventity with the slab
     * 
     * @param event
     * @param slab
     * @return
     */
    private void updateOperation(EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        if (slab.localisation.isEmpty())
            updateEntityOperation(event, slab.attributName, slab.attributValue, eventOperationResult);
        else {
            BaseEntity baseEntity = localise(event, slab.localisation);
            if (baseEntity != null) {
                updateEntityOperation(baseEntity, slab.attributName, slab.attributValue, eventOperationResult);
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
        if (attributValue != null) {
            Method methodAttribut = searchMethodByName(baseEntity, attributName);
            if (methodAttribut == null) {
                eventOperationResult.listLogEvents.add(new LogEvent(eventInvalidUpdateOperation, attributName + " <="
                        + (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue)));
                return;
            }

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
            } else {
                value = attributValue;
            }
        }

        /*
         * if (slab.attributValue !=null && "datePolicyEnum".equals( slab.typedata) ) {
         * slab.attributValue = DatePolicyEnum.valueOf( slab.attributValue.toString());
         * }
         */
        try {
            PropertyUtils.setSimpleProperty(baseEntity, attributName, value);
            baseEntity.touch();

        } catch (Exception e) {
            eventOperationResult.listLogEvents.add(new LogEvent(eventInvalidUpdateOperation, e, attributName + " <="
                    + (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue)));
        }
    }

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

                BaseEntity childIdentify = null;
                // get the object
                Object getObject = method.invoke(indexEntity);
                if (getObject instanceof List) {
                    // then the idEntity take the sens
                    String idEntity = stLocalisation.hasMoreTokens() ? stLocalisation.nextToken() : null;
                    Long idEntityLong = Long.valueOf(idEntity);
                    List<BaseEntity> listChildrenEntity = (List<BaseEntity>) getObject;
                    BaseEntity childEntityById = null;
                    for (BaseEntity child : listChildrenEntity) {
                        if (child.getId() == idEntityLong) {
                            childEntityById = child;
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
        String methodName = "get" + attributName.substring(0, 1).toUpperCase() + attributName.substring(1);
        Method methodAttribut = null;
        for (Method method : baseEntity.getClass().getMethods()) {
            if (method.getName().equals(methodName))
                return method;
        }
        return null;
    }
}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.engine.tool.JpaTool;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(EventUpdate.class.getName());
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

        public Slab(SlabOperation operation, String attributName, Object attributValue, BaseEntity baseEntity) {
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
        logger.info(LOG_HEADER, "Start update from listSlab size["+listSlab.size()+"]");
        EventEntity eventEntity = this.eventController.getEvent();
        EventOperationResult eventOperationResult = new EventOperationResult(eventEntity);
        listSlab.stream().forEach((slab) -> {
            try {
                if (SlabOperation.UPDATE.equals(slab.operation)) {
                    updateOperation(eventEntity, slab, updateContext, eventOperationResult);
                } else if (SlabOperation.ADD.equals(slab.operation)) {
                    addOperation(eventEntity, slab, updateContext, eventOperationResult);
                } else if (SlabOperation.REMOVE.equals(slab.operation)) {
                    removeOperation(eventEntity, slab, eventOperationResult);
                }
            } catch (Exception e) {
                eventOperationResult.addLogEvent(new LogEvent(eventInvalidUpdateOperation, e, slab.operation + ":" + slab.attributName));
            }
        });

        if (!listSlab.isEmpty())
            eventEntity.touch();

        return eventOperationResult;
    }

    private void addOperation(EventEntity event, Slab slab, UpdateContext updateContext, EventOperationResult eventOperationResult) {

        EventControllerAbsChild eventChildController = eventController.getEventControllerFromSlabOperation(slab);
        if (eventController == null)
            return;

        BaseEntity child = eventChildController.createEntity(updateContext, slab, eventOperationResult);
        if (child == null)
            return;

        LimitReach limitReach = eventChildController.getLimitReach();


        // Check if the subscription allow to add this entity
        int maxEntity = updateContext.factoryService.getSubscriptionService().getMaximumEntityPerEvent(this.eventController.getEvent().getSubscriptionEvent(), child);
        eventChildController.setMaxEntity(maxEntity);

        @SuppressWarnings("unchecked")
        Map<String, Object> valueDefault = (Map<String, Object>) slab.attributValue;
        for (Entry<String, Object> entrySlab : valueDefault.entrySet()) {
            eventOperationResult.addLogEvents(JpaTool.updateEntityOperation(child, entrySlab.getKey(), entrySlab.getValue(), updateContext));
        }
        // an error? Stop now.
        if (LogEventFactory.isError(eventOperationResult.listLogEvents))
            return;

        // save it now
        eventChildController.addEntity(child, slab, eventOperationResult);

        if (eventOperationResult.reachTheLimit) {
            // We reach the limit per the subscription
            eventOperationResult.limitSubscription = true;
            // get the ownser user
            ToghUserEntity ownerUser = this.eventController.getOwner();
            if (limitReach != null)
                updateContext.factoryService.getSubscriptionService().registerTouchLimitSubscription(ownerUser, limitReach);
            child = null;
        }

        if (child != null)
            eventOperationResult.listChildEntity.add(child);

    }

    /**
     * Remove operation
     * 
     * @param event
     * @param slab
     * @param eventOperationResult
     */
    private void removeOperation(EventEntity event, Slab slab, EventOperationResult eventOperationResult) {
        EventControllerAbsChild eventChildController = eventController.getEventControllerFromSlabOperation(slab);
        if (eventController == null)
            return;

        eventOperationResult.listChildEntityId.add(slab.getAttributValueLong());
        BaseEntity baseEntity = eventChildController.getEntity(slab.getAttributValueLong());
        if (baseEntity == null) {
            eventOperationResult.listLogEvents.add(new LogEvent(EventControllerAbsChild.eventEntityNotFoundToRemove, "Can't find taskId " + slab.getAttributValueLong()));
        } else {
            eventChildController.removeEntity(baseEntity, eventOperationResult);
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
            eventOperationResult.addLogEvents(JpaTool.updateEntityOperation(slab.baseEntity==null ? event : slab.baseEntity, slab.attributName, slab.attributValue, updateContext));
        else {
            BaseEntity baseEntity = eventController.localise(event, slab.localisation);
            if (baseEntity != null) {
                eventOperationResult.addLogEvents(JpaTool.updateEntityOperation(baseEntity, slab.attributName, slab.attributValue, updateContext));
            } else {
                eventOperationResult.addLogEvent(new LogEvent(eventCantLocalise, "Localisation [" + slab.localisation + "] to update [" + slab.attributName + "]"));
            }

        }
        event.touch();
    }

}

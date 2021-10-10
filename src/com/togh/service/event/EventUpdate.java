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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    EventController eventController;

    public enum SlabOperation {
        UPDATE, ADD, REMOVE
    }

    protected EventUpdate(EventController eventController) {
        this.eventController = eventController;
    }

    public EventOperationResult update(List<Slab> listSlab, UpdateContext updateContext) {
        EventEntity eventEntity = this.eventController.getEvent();
        logger.info(LOG_HEADER + "EventId[" + eventEntity.getId() + "] Start update from listSlab " + getSummary(listSlab));

        EventOperationResult eventOperationResult = new EventOperationResult(eventEntity);
        listSlab.stream().forEach((slab) -> {
            try {
                switch (slab.operation) {
                    case UPDATE:
                        updateOperation(eventEntity, slab, updateContext, eventOperationResult);
                        break;
                    case ADD:
                        addOperation(slab, updateContext, eventOperationResult);
                        break;
                    case REMOVE:
                        removeOperation(slab, eventOperationResult);
                        break;
                }
            } catch (Exception e) {
                eventOperationResult.addLogEvent(new LogEvent(eventInvalidUpdateOperation, e, "EventId[" + eventEntity.getId() + " " + getSummary(Arrays.asList(slab))));
            }
        });

        if (!listSlab.isEmpty())
            eventEntity.touch();

        return eventOperationResult;
    }

    private void addOperation(Slab slab, UpdateContext updateContext, EventOperationResult eventOperationResult) {

        EventControllerAbsChild eventChildController = eventController.getEventControllerFromSlabOperation(slab);
        if (eventChildController == null)
            return;


        LimitReach limitReach = eventChildController.getLimitReach();


        // Check if the subscription allow to add this entity
        int maxEntity = updateContext.getFactoryService()
                .getSubscriptionService()
                .getMaximumEntityPerEvent(this.eventController.getEvent().getSubscriptionEvent(),
                        eventChildController.getClass());

        eventChildController.setMaxEntity(maxEntity);
        eventOperationResult.reachTheLimit = eventChildController.isAtLimit(updateContext);

        if (eventOperationResult.reachTheLimit) {
            // We reach the limit per the subscription
            eventOperationResult.limitSubscription = true;
            // get the owner user
            ToghUserEntity ownerUser = this.eventController.getOwner();
            if (limitReach != null)
                updateContext.getFactoryService().getSubscriptionService().registerTouchLimitSubscription(ownerUser, limitReach);
            return;
        }


        EventEntityPlan entityPlan = eventChildController.createEntity(updateContext, slab, eventOperationResult);
        if (entityPlan.isEmpty())
            return;

        @SuppressWarnings("unchecked")
        Map<String, Object> valueDefault = (Map<String, Object>) slab.attributValue;
        for (Entry<String, Object> entrySlab : valueDefault.entrySet()) {
            // apply the default value only on the LAST entry
            eventOperationResult.addLogEvents(JpaTool.updateEntityOperation(entityPlan.child, entrySlab.getKey(), entrySlab.getValue(), updateContext));
        }
        // an error? Stop now.
        if (LogEventFactory.isError(eventOperationResult.listLogEvents))
            return;


        // save it now. The eventOperationResult can return a ReachTheLimit
        for (BaseEntity child : entityPlan.additionalEntity) {
            eventChildController.addEntity(child, slab, eventOperationResult);
        }
        eventChildController.addEntity(entityPlan.child, slab, eventOperationResult);

        eventOperationResult.listChildEntity.add(entityPlan.getEntityToAttach());

    }

    /**
     * Remove operation
     *
     * @param slab
     * @param eventOperationResult
     */
    private void removeOperation(Slab slab, EventOperationResult eventOperationResult) {
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
            eventOperationResult.addLogEvents(JpaTool.updateEntityOperation(slab.baseEntity == null ? event : slab.baseEntity, slab.attributName, slab.attributValue, updateContext));
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

    public static class Slab {

        public SlabOperation operation;
        public String attributName;
        public Object attributValue;
        public String localisation;
        public BaseEntity baseEntity = null;

        public Slab(Map<String, Object> recordSlab) {
            operation = SlabOperation.valueOf((String) recordSlab.get("operation"));
            attributName = (String) recordSlab.get("name");
            attributValue = recordSlab.get("value");
            localisation = (String) recordSlab.get("localisation");
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

    /**
     * Do a Summary on Slab to update
     *
     * @param listSlab list Of Slab to get the summary
     * @return the summary
     */
    private String getSummary(List<Slab> listSlab) {
        return "Size:" + listSlab.size()
                + " - ["
                + listSlab.stream()
                .map(t -> {
                    return
                            t.operation
                                    + " "
                                    + t.localisation + "." + t.attributName + ": ["
                                    + (t.attributValue == null ? null :
                                    t.attributValue.toString().length() > 20 ? t.attributValue.toString().substring(0, 20) + "..."
                                            : t.attributValue.toString()
                                            + "]");
                })
                .collect(Collectors.joining(", "))
                + "]";
    }
}

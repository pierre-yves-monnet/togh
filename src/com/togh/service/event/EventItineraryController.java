/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.entity.EventEntity;
import com.togh.entity.EventItineraryStepEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;
import com.togh.service.event.EventUpdate.SlabOperation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* ******************************************************************************** */
/*                                                                                  */
/* EventControllerItinerary, */
/*                                                                                  */
/* Decompose the EventController class */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventItineraryController extends EventAbsChildController {

    public static final String CST_JSON_DATE_STEP = "dateStep";

    protected EventItineraryController(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return getEventEntity().getItineraryStepList().size() >= getMaxEntity();
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventItineraryStepEntity());
    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return getFactoryRepository().eventItineraryStepRepository.findById(entityId);
    }

    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventItineraryStepRepository.save((EventItineraryStepEntity) childEntity);
        return childEntity;
    }

    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventItineraryStepRepository.save((EventItineraryStepEntity) childEntity);
        getEventEntity().addItineraryStep((EventItineraryStepEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
        return childEntity;
    }

    /**
     * Database may return a constraint error, because 2 threads try to do the same operation at the same time.
     * So, the server has to deal with that. One solution is to retrieve the current record saved in the database, and return it
     *
     * @param childEntity          child Entity to insert
     * @param slabOperation        slab operation in progress
     * @param eventOperationResult eventOperationResult
     * @return the correct entity, which may be the existing entity in the database
     */
    @Override
    public BaseEntity manageConstraint(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        return null;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventItineraryStepRepository.delete((EventItineraryStepEntity) childEntity);
        getEventEntity().removeItineraryStep((EventItineraryStepEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.ITINERARY;
    }

    /**
     * @return
     */
    public List<Slab> checkItinerary() {
        List<Slab> listSlab = new ArrayList<>();
        LocalDateTime dateBegin = getEventEntity().getDateStartEvent() == null ? getEventEntity().getDateEvent() : getEventEntity().getDateStartEvent();
        LocalDateTime dateEnd = getEventEntity().getDateEndEvent() == null ? getEventEntity().getDateEvent() : getEventEntity().getDateEndEvent();

        for (EventItineraryStepEntity itineraryStep : getEventEntity().getItineraryStepList()) {
            if (itineraryStep.getDateStep() == null) {
                if (dateBegin != null) {
                    listSlab.add(new Slab(SlabOperation.UPDATE, CST_JSON_DATE_STEP, dateBegin.toLocalDate(), itineraryStep));
                }
            } else {
                if (dateBegin != null && itineraryStep.getDateStep().compareTo(dateBegin.toLocalDate()) < 0)
                    listSlab.add(new Slab(SlabOperation.UPDATE, CST_JSON_DATE_STEP, dateBegin.toLocalDate(), itineraryStep));
                if (dateEnd != null && itineraryStep.getDateStep().compareTo(dateEnd.toLocalDate()) > 0)
                    listSlab.add(new Slab(SlabOperation.UPDATE, CST_JSON_DATE_STEP, dateEnd.toLocalDate(), itineraryStep));
            }
        }
        return listSlab;
    }

}

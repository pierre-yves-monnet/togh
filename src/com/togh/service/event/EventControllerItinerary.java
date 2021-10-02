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

public class EventControllerItinerary extends EventControllerAbsChild {

    protected EventControllerItinerary(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public BaseEntity createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventItineraryStepEntity();
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
        eventOperationResult.reachTheLimit = getEventEntity().getItineraryStepList().size() >= getMaxEntity();
        if (eventOperationResult.reachTheLimit)
            return null;
        getFactoryRepository().eventItineraryStepRepository.save((EventItineraryStepEntity) childEntity);
        getEventEntity().addItineraryStep((EventItineraryStepEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
        return childEntity;
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
     * 
     * @return
     */
    public List<Slab> checkItinerary() {
        List<Slab> listSlab = new ArrayList<>();
        LocalDateTime dateBegin = getEventEntity().getDateStartEvent() == null ? getEventEntity().getDateEvent() : getEventEntity().getDateStartEvent();
        LocalDateTime dateEnd = getEventEntity().getDateEndEvent() == null ? getEventEntity().getDateEvent() : getEventEntity().getDateEndEvent();

        for (EventItineraryStepEntity itineraryStep : getEventEntity().getItineraryStepList()) {
            if (itineraryStep.getDateStep() == null) {
                if (dateBegin != null) {
                    listSlab.add(new Slab(SlabOperation.UPDATE, "dateStep", dateBegin.toLocalDate(), itineraryStep));
                }
            } else {
                if (dateBegin != null && itineraryStep.getDateStep().compareTo(dateBegin.toLocalDate()) < 0)
                    listSlab.add(new Slab(SlabOperation.UPDATE, "dateStep", dateBegin.toLocalDate(), itineraryStep));
                if (dateEnd != null && itineraryStep.getDateStep().compareTo(dateBegin.toLocalDate()) > 0)
                    listSlab.add(new Slab(SlabOperation.UPDATE, "dateStep", dateBegin.toLocalDate(), itineraryStep));
            }
        }
        return listSlab;
    }

}

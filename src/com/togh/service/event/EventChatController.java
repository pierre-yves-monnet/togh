package com.togh.service.event;

import com.togh.entity.EventChatEntity;
import com.togh.entity.EventEntity;
import com.togh.entity.EventGroupChatEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

public class EventChatController extends EventAbsChildController {

    private final EventGroupChatController eventControllerGroupChat;

    protected EventChatController(EventController eventController, EventGroupChatController eventControllerGroupChat, EventEntity eventEntity) {
        super(eventController, eventEntity);
        this.eventControllerGroupChat = eventControllerGroupChat;
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
    public EventEntityPlan createEntity(UpdateContext updateContext,
                                        Slab slabOperation, EventOperationResult eventOperationResult) {
        EventChatEntity eventChatEntity = new EventChatEntity();
        eventChatEntity.setWhoId(updateContext.getToghUser());
        // chat is attached to a GroupChat
        EventGroupChatEntity groupChatEntity = eventControllerGroupChat.getGroupChat(slabOperation);
        eventControllerGroupChat.addChatInGroup(groupChatEntity, eventChatEntity);

        EventEntityPlan eventEntityPlan = new EventEntityPlan(eventChatEntity);
        eventEntityPlan.additionalEntity.add(groupChatEntity);
        return eventEntityPlan;
    }

    public BaseEntity getEntity(long entityId) {
        return null; // not implemented
    }

    /**
     * Save the entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slab, EventOperationResult eventOperationResult) {
        EventGroupChatEntity groupChatEntity = eventControllerGroupChat.getGroupChat(slab);
        return eventControllerGroupChat.addChatInGroup(groupChatEntity, (EventChatEntity) childEntity);

    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        // not supported at this moment
    }

    /*
     * Add a entity in the event Entity
     * childEntity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        return null; // only by the
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


}

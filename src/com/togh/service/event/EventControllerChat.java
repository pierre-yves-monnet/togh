package com.togh.service.event;

import com.togh.entity.EventChatEntity;
import com.togh.entity.EventEntity;
import com.togh.entity.EventGroupChatEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

public class EventControllerChat extends EventControllerAbsChild {

    EventControllerGroupChat eventControllerGroupChat;

    protected EventControllerChat(EventController eventController, EventControllerGroupChat eventControllerGroupChat, EventEntity eventEntity) {
        super(eventController, eventEntity);
        this.eventControllerGroupChat = eventControllerGroupChat;
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.SURVEYCHOICE;

    }

    @Override
    public BaseEntity createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        EventChatEntity eventChatEntity= new EventChatEntity();
        eventChatEntity.setWhoId( updateContext.toghUser);
        return eventChatEntity;

    }

    public  BaseEntity getEntity( long entityId ) 
    {
       return null; // not implemented 
    }

    /**
     * Save the entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slab, EventOperationResult eventOperationResult) {
        EventGroupChatEntity groupChatEntity = eventControllerGroupChat.getGroupChat( slab);
       return eventControllerGroupChat.addChatInGroup( groupChatEntity, (EventChatEntity) childEntity);
    
    }


    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        // not supported at this moment
    }

    /*
     * Add a entity in the event Entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        return null; // only by the
    }
}

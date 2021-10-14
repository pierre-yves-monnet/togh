package com.togh.service.event;

import com.togh.entity.EventChatEntity;
import com.togh.entity.EventEntity;
import com.togh.entity.EventGroupChatEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.SubscriptionService.LimitReach;
import com.togh.service.event.EventUpdate.Slab;

public class EventGroupChatController extends EventAbsChildController {

    private final static String GENERAL_CHAT = "GENERAL";

    protected EventGroupChatController(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public LimitReach getLimitReach() {
        return LimitReach.CHATGROUP;

    }

    @Override
    public boolean isAtLimit(UpdateContext updateContext) {
        return false;
    }

    @Override
    public EventEntityPlan createEntity(UpdateContext updateContext, Slab slabOperation, EventOperationResult eventOperationResult) {
        return new EventEntityPlan(new EventGroupChatEntity());

    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return getFactoryRepository().eventGroupChatRepository.findById(entityId);
    }


    /**
     * Save the entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        eventOperationResult.reachTheLimit = getEventEntity().getGroupChatList().size() >= getMaxEntity();
        if (eventOperationResult.reachTheLimit)
            return null;
        getFactoryRepository().eventGroupChatRepository.save((EventGroupChatEntity) childEntity);
        return childEntity;
    }

    @Override
    public void removeEntity(BaseEntity childEntity, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGroupChatRepository.delete((EventGroupChatEntity) childEntity);
        getEventEntity().removeGroupChat((EventGroupChatEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
    }

    /*
     * Add a entity in the event Entity
     * Entity is then saved, and can be modified (persistenceid is created)
     */
    @Override
    public BaseEntity addEntity(BaseEntity childEntity, Slab slabOperation, EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGroupChatRepository.save((EventGroupChatEntity) childEntity);

        getEventEntity().addGroupChat((EventGroupChatEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());

        return childEntity;
    }

    public EventGroupChatEntity getGroupChat(Slab slab) {
        // there is only one

        if (getEventEntity().getGroupChatList().isEmpty()) {
            EventGroupChatEntity generalChat = new EventGroupChatEntity();
            generalChat.setName(GENERAL_CHAT);
            getFactoryRepository().eventGroupChatRepository.save(generalChat);
            getEventEntity().addGroupChat(generalChat);
            getFactoryRepository().eventRepository.save(getEventEntity());
            return generalChat;
        }
        return getEventEntity().getGroupChatList().get(0);

    }

    public EventChatEntity addChatInGroup(EventGroupChatEntity groupChatEntity, EventChatEntity chatEntity) {
        getFactoryRepository().eventChatRepository.save( chatEntity );
       
        groupChatEntity.addChat(chatEntity);
        if (groupChatEntity.getListChat().size() > getMaxEntity())
            groupChatEntity.getListChat().remove(0);
        getFactoryRepository().eventGroupChatRepository.save( groupChatEntity );
        
        return chatEntity;
    }

}

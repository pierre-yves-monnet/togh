package com.togh.service.event;

import com.togh.entity.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * A crateEntity may result to create multiple Entity
 * Example with the Chat, a GroupChat must be created, and the Chat it attached not to the parent, but to the GroupChat
 */
public class EventEntityPlan {
    /**
     * All additional Entity to create BEFORE the child, in the correct order
     */
    public final List<BaseEntity> additionalEntity = new ArrayList<>();
    /**
     * The entity created
     */
    public BaseEntity child;

    public EventEntityPlan(BaseEntity child) {
        this.child = child;
    }

    public boolean isEmpty() {
        return child == null;
    }

    /**
     * This method may be overload
     */
    public BaseEntity getEntityToAttach() {
        if (additionalEntity.isEmpty())
            return child;
        else
            return additionalEntity.get(0);
    }

}

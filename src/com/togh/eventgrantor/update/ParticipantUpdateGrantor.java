/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.eventgrantor.update;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventGrantor                                                                     */
/*                                                                                  */
/*  Check operation           */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import com.togh.entity.ParticipantEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.event.EventController;
import com.togh.service.event.EventUpdate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ParticipantUpdateGrantor implements BaseUpdateGrantor {

    /**
     * Give the Entity on which the grantor works
     *
     * @return the entityClass
     */
    @Override
    public Class getEntityClass() {
        return ParticipantEntity.class;
    }

    /**
     * Return true if this operation is allowed on this Slab for the user in the context
     *
     * @param baseEntity    Entity to modify
     * @param slab          Slab operation
     * @param updateContext Update context, contains user who wants to realize the operation
     */
    @Override
    public boolean isOperationAllowed(BaseEntity baseEntity, EventUpdate.Slab slab, EventService.UpdateContext updateContext) {
        if (!updateContext.getEventController().isActiveParticipant(updateContext.getToghUser()))
            return false;

        if (slab.operation == EventUpdate.SlabOperation.UPDATE) {
            if (!updateContext.getEventController().isOrganizer(updateContext.getToghUser()))
                return true;
            // I can change only information on myself
            ParticipantEntity participantEntity = (ParticipantEntity) baseEntity;
            return participantEntity.getUser().getId().equals(updateContext.getToghUser().getId());
        }
        return true;
    }

    @Override
    public List<String> getFieldsReadOnly(ToghUserEntity toghUser, EventController eventController) {
        return Collections.EMPTY_LIST;
    }

}

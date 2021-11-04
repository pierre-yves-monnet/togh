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

import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.event.EventController;
import com.togh.service.event.EventUpdate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventUpdateGrantor implements BaseUpdateGrantor {

    public String[] updateGrantOrganizer = {"name", "statusEvent", "description", "datePolicy", "timeEvent", "durationEvent", "typeEvent", "dateEndEvent", "dateStartEvent"};

    @Override
    public Class getEntityClass() {
        return EventEntity.class;
    }

    /**
     * Return true if this operation is allowed on this Slab for the user in the context
     */
    public boolean isOperationAllowed(BaseEntity baseEntity, EventUpdate.Slab slab, EventService.UpdateContext updateContext) {
        if (!updateContext.getEventController().isActiveParticipant(updateContext.getToghUser()))
            return false;

        if (slab.operation == EventUpdate.SlabOperation.UPDATE) {
            if (Arrays.stream(updateGrantOrganizer).anyMatch(t -> t.equalsIgnoreCase(slab.attributName))) {
                if (!updateContext.getEventController().isOrganizer(updateContext.getToghUser()))
                    return false;
            }

            return true;
        }
        return true;
    }

    public List<String> getFieldsReadOnly(ToghUserEntity toghUser, EventController eventController) {
        return Arrays.stream(updateGrantOrganizer)
                .filter(t -> !eventController.isOrganizer(toghUser))
                .collect(Collectors.toList());
    }
}

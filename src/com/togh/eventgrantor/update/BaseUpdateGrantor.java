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
/*  EntitySerialization                                                                     */
/*                                                                                  */
/*  To serialize an Entity, the controller must implement this interface             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.event.EventController;
import com.togh.service.event.EventUpdate;

import java.util.List;

public interface BaseUpdateGrantor {

    /**
     * Give the Entity on which the grantor works
     *
     * @return
     */
    Class<?> getEntityClass();

    /**
     * Return true if this operation is allowed on this Slab for the user in the context
     */
    boolean isOperationAllowed(BaseEntity baseEntity, EventUpdate.Slab slab, EventService.UpdateContext updateContext);

    /**
     * Return, for the user, the list of Readonly field, to send to the interface
     *
     * @param toghUser        user to check
     * @param eventController event controller
     * @return list of string to move to ReadOnly
     */
    List<String> getFieldsReadOnly(ToghUserEntity toghUser, EventController eventController);

}

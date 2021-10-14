/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;

import java.util.HashMap;
import java.util.Map;



/* ******************************************************************************** */
/*                                                                                  */
/*  EntitySerialization                                                                     */
/*                                                                                  */
/*  To serialize an Entity, the controller must implement this interface             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public abstract class BaseSerializer {
    static final String CST_JSONOUT_NAME = "name";
    static final String CST_JSONOUT_ID = "id";
    static final String CST_JSONOUT_AUTHORID = "authorid";


    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    public abstract Class getEntityClass();

    /**
     * @param entity            entity to serialize
     * @param contextAccess     context to produce the map
     * @param timeZoneOffset    time Off set of the browser to calculate local date
     * @param factorySerializer the factory has to be pass. Not possible to aAutowired it: we have a loop dependency else
     * @return the map which contains the serialized object
     */
    public abstract Map<String, Object> getMap(BaseEntity entity,
                                               ToghUserEntity.ContextAccess contextAccess,
                                               Long timeZoneOffset,
                                               FactorySerializer factorySerializer);

    /**
     * getBaseMap. Each entity depend of UserEntity. So, this is the basic map
     *
     * @param baseEntity     Base entity to serialize
     * @param contextAccess  context to produce the correct information
     * @param timeZoneOffset time zone offset to calculate date
     * @return
     */
    protected Map<String, Object> getBasicMap(BaseEntity baseEntity, ToghUserEntity.ContextAccess contextAccess, Long timeZoneOffset) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(CST_JSONOUT_ID, baseEntity.getId());
        resultMap.put(CST_JSONOUT_NAME, baseEntity.getName());
        return resultMap;
    }

}

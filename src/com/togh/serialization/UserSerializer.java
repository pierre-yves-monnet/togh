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
import com.togh.entity.base.UserEntity;

import java.util.Map;



/* ******************************************************************************** */
/*                                                                                  */
/*  EntitySerialization                                                                     */
/*                                                                                  */
/*  To serialize an Entity, the controller must implement this interface             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public abstract class UserSerializer extends BaseSerializer {
    /**
     * getBaseMap. Each entity depend of UserEntity. So, this is the basic map
     *
     * @param userEntity
     * @param contextAccess
     * @param timezoneOffset
     * @return
     */
    protected Map<String, Object> getBasicMap(UserEntity userEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset) {
        Map<String, Object> resultMap = super.getBasicMap(userEntity, contextAccess, timezoneOffset);
        if (contextAccess == ToghUserEntity.ContextAccess.ADMIN)
            resultMap.put(CST_JSONOUT_AUTHORID, userEntity.getAuthorId());
        return resultMap;
    }

}

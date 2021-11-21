/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.base.BaseEntity;
import com.togh.entity.base.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Map;



/* ******************************************************************************** */
/*                                                                                  */
/*  EntitySerialization                                                                     */
/*                                                                                  */
/*  To serialize an Entity, the controller must implement this interface             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Component
public abstract class UserSerializer extends BaseSerializer {
    /**
     * getBaseMap. Each entity depend on UserEntity. So, this is the basic map
     *
     * @param baseEntity        Base entity to serialize
     * @param serializerOptions Serialize options
     * @return
     */
    @Override
    protected Map<String, Object> getBasicMap(BaseEntity baseEntity, SerializerOptions serializerOptions) {
        Map<String, Object> resultMap = super.getBasicMap(baseEntity, serializerOptions);
        if (serializerOptions.getContextAccess().equals(SerializerOptions.ContextAccess.ADMIN))
            resultMap.put(JSON_AUTHORID, ((UserEntity) baseEntity).getAuthorId());
        return resultMap;
    }

}

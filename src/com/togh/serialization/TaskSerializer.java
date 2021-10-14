/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.EventTaskEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class TaskSerializer extends BaseSerializer {

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventTaskEntity.class;
    }

    /**
     * GetMap - implement EntitySerialization
     *
     * @param userEntity
     * @param contextAccess
     * @param timezoneOffset
     * @param factorySerializer
     * @return
     */
    @Override
    public Map<String, Object> getMap(BaseEntity userEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset, FactorySerializer factorySerializer) {
        EventTaskEntity eventTaskEntity = (EventTaskEntity) userEntity;
        Map<String, Object> resultMap = getBasicMap(eventTaskEntity, contextAccess, timezoneOffset);


        resultMap.put("status", eventTaskEntity.getStatus() == null ? null : eventTaskEntity.getStatus().toString());
        resultMap.put("datestarttask", EngineTool.dateToString(eventTaskEntity.getDateStartTask()));
        resultMap.put("dateendtask", EngineTool.dateToString(eventTaskEntity.getDateEndTask()));
        resultMap.put("description", eventTaskEntity.getDescription());

        // we just return the ID here
        resultMap.put("whoid", eventTaskEntity.getWhoId() == null ? null : eventTaskEntity.getWhoId().getId());

        return resultMap;
    }
}

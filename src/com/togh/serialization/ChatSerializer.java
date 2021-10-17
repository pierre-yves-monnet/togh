/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventChatEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ChatSerializer extends BaseSerializer {


    public static final String JSON_OUT_MESSAGE = "message";

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventChatEntity.class;
    }

    /**
     * GetMap - implement EntitySerialization
     *
     * @param baseEntity        userEntity
     * @param contextAccess     contextAccess to know what information has to be produce
     * @param timezoneOffset    time offset of the browser
     * @param factorySerializer
     * @return a serialisation map
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset, FactorySerializer factorySerializer) {
        EventChatEntity chatEntity = (EventChatEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(chatEntity, contextAccess, timezoneOffset);

        resultMap.put(JSON_OUT_MESSAGE, chatEntity.getMessage());

        // we just return the ID here
        resultMap.put("whoid", chatEntity.getWhoId() == null ? null : chatEntity.getWhoId().getId());

        return resultMap;
    }
}
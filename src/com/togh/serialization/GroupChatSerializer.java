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
import com.togh.entity.EventGroupChatEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class GroupChatSerializer extends BaseSerializer {


    public static final String JSON_CHATLIST = "chatlist";

    @Override
    public Class getEntityClass() {
        return EventGroupChatEntity.class;
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
        EventGroupChatEntity groupChatEntity = (EventGroupChatEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(groupChatEntity, contextAccess, timezoneOffset);
        resultMap.put("description", groupChatEntity.getDescription());

        List<Map<String, Object>> listChatMap = new ArrayList<>();
        for (EventChatEntity chatEntity : groupChatEntity.getListChat()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(chatEntity);
            listChatMap.add(serializer.getMap(chatEntity, contextAccess, timezoneOffset, factorySerializer));
        }
        resultMap.put(JSON_CHATLIST, listChatMap);

        return resultMap;
    }
}

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
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
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
     * @param baseEntity           Entity to serialize
     * @param parentEntity         Parent entity
     * @param serializerOptions    Serialization options
     * @param factorySerializer    factory to access all serializer
     * @param factoryUpdateGrantor factory to access Update Grantor
     * @return a serialisation map
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        EventGroupChatEntity groupChatEntity = (EventGroupChatEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(groupChatEntity, serializerOptions);
        resultMap.put("description", groupChatEntity.getDescription());

        List<Map<String, Object>> listChatMap = new ArrayList<>();
        for (EventChatEntity chatEntity : groupChatEntity.getListChat()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(chatEntity);
            listChatMap.add(serializer.getMap(chatEntity, parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(JSON_CHATLIST, listChatMap);

        return resultMap;
    }
}

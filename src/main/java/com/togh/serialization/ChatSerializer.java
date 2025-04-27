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
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ChatSerializer extends BaseSerializer {


  public static final String JSON_OUT_MESSAGE = "message";
  public static final String JSON_WHO_ID = "whoid";

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
   * @param baseEntity           Entity to serialize
   * @param parentEntity         Parent entity
   * @param serializerOptions    Serialization options
   * @param factorySerializer    factory to access all serializer
   * @param factoryUpdateGrantor factory to access Update Grantor
   * @return a serialisation map
   */
  @Override
  public Map<String, Object> getMap(BaseEntity baseEntity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
    EventChatEntity chatEntity = (EventChatEntity) baseEntity;
    Map<String, Object> resultMap = getBasicMap(chatEntity, serializerOptions);

    resultMap.put(JSON_OUT_MESSAGE, chatEntity.getMessage());

    // we just return the ID here
    resultMap.put(JSON_WHO_ID, chatEntity.getWhoId() == null ? null : chatEntity.getWhoId().getId());

    return resultMap;
  }
}
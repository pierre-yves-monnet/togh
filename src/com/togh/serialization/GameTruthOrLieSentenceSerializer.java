/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventGameTruthOrLieSentenceEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GameTruthOrLieSentenceSerializer extends BaseSerializer {
  public static final String JSON_SENTENCE = "sentence";
  public static final String JSON_STATUS_SENTENCE = "statusSentence";

  /**
   * The serializer serialize an Entity Class. Return the entity
   *
   * @return the entity Class this serializer handle
   */
  @Override
  public Class getEntityClass() {
    return EventGameTruthOrLieSentenceEntity.class;
  }

  /**
   * @param entity               entity to serialize
   * @param parentEntity         The parent of the entity, may be needed by the serializer. Null if the entity is the root.
   * @param serializerOptions    Options to serialize
   * @param factorySerializer    the factory has to be pass. Not possible to aAutowired it: we have a loop dependency else
   * @param factoryUpdateGrantor Factory to access the grantor
   * @return the map which contains the serialized object
   */
  @Override
  public Map<String, Object> getMap(BaseEntity entity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
    EventGameTruthOrLieSentenceEntity truthOrLieSentenceEntity = (EventGameTruthOrLieSentenceEntity) entity;

    Map<String, Object> sentenceMap = getBasicMap(truthOrLieSentenceEntity, serializerOptions);

    sentenceMap.put(JSON_SENTENCE, truthOrLieSentenceEntity.getSentence());
    sentenceMap.put(JSON_STATUS_SENTENCE, getEnumValue(truthOrLieSentenceEntity.getStatusSentence(), null));


    return sentenceMap;
  }
}

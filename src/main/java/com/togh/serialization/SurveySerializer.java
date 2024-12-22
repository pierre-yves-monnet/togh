/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventSurveyAnswerEntity;
import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.EventSurveyEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class SurveySerializer extends BaseSerializer {

  /**
   * The serializer serialize an Entity Class. Return the entity
   *
   * @return the entity Class this serializer handle
   */
  @Override
  public Class getEntityClass() {
    return EventSurveyEntity.class;
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
    EventSurveyEntity eventSurveyEntity = (EventSurveyEntity) baseEntity;
    Map<String, Object> resultMap = getBasicMap(eventSurveyEntity, serializerOptions);

    resultMap.put("status", eventSurveyEntity.getStatus() == null ? null : eventSurveyEntity.getStatus().toString());
    resultMap.put("description", eventSurveyEntity.getDescription());

    List<Map<String, Object>> listChoiceMap = new ArrayList<>();

    if (eventSurveyEntity.getChoicelist() != null) {
      for (EventSurveyChoiceEntity choiceEntity : eventSurveyEntity.getChoicelist()) {
        BaseSerializer baseSerializer = factorySerializer.getFromEntity(choiceEntity);
        listChoiceMap.add(baseSerializer.getMap(choiceEntity, parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
      }
    }
    resultMap.put(EventSurveyChoiceEntity.CST_SLABOPERATION_CHOICELIST, listChoiceMap);

    List<Map<String, Object>> listAnswerMap = new ArrayList<>();
    if (eventSurveyEntity.getAnswerlist() != null) {
      for (EventSurveyAnswerEntity answerEntity : eventSurveyEntity.getAnswerlist()) {
        BaseSerializer baseSerializer = factorySerializer.getFromEntity(answerEntity);
        listAnswerMap.add(baseSerializer.getMap(answerEntity, parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
      }
    }
    resultMap.put(EventSurveyAnswerEntity.CST_SLABOPERATION_ANSWERLIST, listAnswerMap);


    return resultMap;
  }
}

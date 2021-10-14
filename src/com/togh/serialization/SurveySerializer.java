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
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
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
     * @param baseEntity        userEntity
     * @param contextAccess     contextAccess to know what information has to be produce
     * @param timezoneOffset    time offset of the browser
     * @param factorySerializer
     * @return a serialisation map
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset, FactorySerializer factorySerializer) {
        EventSurveyEntity eventSurveyEntity = (EventSurveyEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(eventSurveyEntity, contextAccess, timezoneOffset);

        resultMap.put("status", eventSurveyEntity.getStatus() == null ? null : eventSurveyEntity.getStatus().toString());
        resultMap.put("description", eventSurveyEntity.getDescription());

        List<Map<String, Object>> listChoiceMap = new ArrayList<>();

        if (eventSurveyEntity.getChoicelist() != null) {
            for (EventSurveyChoiceEntity choiceEntity : eventSurveyEntity.getChoicelist()) {
                BaseSerializer baseSerializer = factorySerializer.getFromEntity(choiceEntity);
                listChoiceMap.add(baseSerializer.getMap(choiceEntity, contextAccess, timezoneOffset, factorySerializer));
            }
        }
        resultMap.put(EventSurveyChoiceEntity.CST_SLABOPERATION_CHOICELIST, listChoiceMap);

        List<Map<String, Object>> listAnswerMap = new ArrayList<>();
        if (eventSurveyEntity.getAnswerlist() != null) {
            for (EventSurveyAnswerEntity answerEntity : eventSurveyEntity.getAnswerlist()) {
                BaseSerializer baseSerializer = factorySerializer.getFromEntity(answerEntity);
                listAnswerMap.add(baseSerializer.getMap(answerEntity, contextAccess, timezoneOffset, factorySerializer));
            }
        }
        resultMap.put(EventSurveyAnswerEntity.CST_SLABOPERATION_ANSWERLIST, listAnswerMap);


        return resultMap;
    }
}

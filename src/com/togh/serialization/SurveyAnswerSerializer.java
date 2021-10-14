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
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class SurveyAnswerSerializer extends BaseSerializer {
    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventSurveyAnswerEntity.class;
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
        EventSurveyAnswerEntity surveyAnswerEntity = (EventSurveyAnswerEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(surveyAnswerEntity, contextAccess, timezoneOffset);

        resultMap.put("decision", surveyAnswerEntity.getDecision() == null ? new HashMap<>() : surveyAnswerEntity.getDecision());

        // we just return the ID here
        resultMap.put("whoid", surveyAnswerEntity.getWhoId() == null ? null : surveyAnswerEntity.getWhoId().getId());

        return resultMap;
    }
}

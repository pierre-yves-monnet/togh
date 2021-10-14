/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventSurveyChoiceEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SurveyChoiceListSerializer extends BaseSerializer {


    @Override
    public Class getEntityClass() {
        return EventSurveyChoiceEntity.class;
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
        EventSurveyChoiceEntity surveyChoiceEntity = (EventSurveyChoiceEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(surveyChoiceEntity, contextAccess, timezoneOffset);

        resultMap.put("code", surveyChoiceEntity.getCode());
        resultMap.put("proptext", surveyChoiceEntity.getProptext());
        return resultMap;
    }
}

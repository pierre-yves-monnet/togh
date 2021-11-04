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
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
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
     * @param baseEntity           Entity to serialize
     * @param serializerOptions    Serialization options
     * @param factorySerializer    factory to access all serializer
     * @param factoryUpdateGrantor factory to access Update Grantor
     * @return a serialisation map
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        EventSurveyChoiceEntity surveyChoiceEntity = (EventSurveyChoiceEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(surveyChoiceEntity, serializerOptions);

        resultMap.put("code", surveyChoiceEntity.getCode());
        resultMap.put("proptext", surveyChoiceEntity.getProptext());
        return resultMap;
    }
}

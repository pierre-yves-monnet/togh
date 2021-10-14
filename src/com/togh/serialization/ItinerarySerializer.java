/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.EventItineraryStepEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ItinerarySerializer extends BaseSerializer {

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventItineraryStepEntity.class;
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
        EventItineraryStepEntity itineraryStepEntity = (EventItineraryStepEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(itineraryStepEntity, contextAccess, timezoneOffset);

        resultMap.put("dateStep", EngineTool.dateToString(itineraryStepEntity.getDateStep()));
        resultMap.put("rownumber", itineraryStepEntity.getRownumber());
        resultMap.put("category", itineraryStepEntity.getCategory() == null ? null : itineraryStepEntity.getCategory().toString());
        resultMap.put("visitTime", itineraryStepEntity.getVisitTime());
        resultMap.put("durationTime", itineraryStepEntity.getDurationTime());
        resultMap.put("description", itineraryStepEntity.getDescription());
        resultMap.put("geoaddress", itineraryStepEntity.getGeoaddress());
        resultMap.put("geolat", itineraryStepEntity.getGeolat());
        resultMap.put("geolng", itineraryStepEntity.getGeolng());
        resultMap.put("website", itineraryStepEntity.getWebsite());


        // Here we attached directly the expense information
        if (itineraryStepEntity.getExpense() != null) {
            BaseSerializer expenseSerializer = factorySerializer.getFromEntity(itineraryStepEntity.getExpense());
            resultMap.put("expense", expenseSerializer.getMap(itineraryStepEntity.getExpense(), contextAccess, timezoneOffset, factorySerializer));
        }


        return resultMap;
    }
}

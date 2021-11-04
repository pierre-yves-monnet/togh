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
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
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
     * @param baseEntity           Entity to serialize
     * @param serializerOptions    Serialization options
     * @param factorySerializer    factory to access all serializer
     * @param factoryUpdateGrantor factory to access Update Grantor
     * @return a serialisation map
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        EventItineraryStepEntity itineraryStepEntity = (EventItineraryStepEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(itineraryStepEntity, serializerOptions);

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
            resultMap.put("expense", expenseSerializer.getMap(itineraryStepEntity.getExpense(), serializerOptions, factorySerializer, factoryUpdateGrantor));
        }


        return resultMap;
    }
}

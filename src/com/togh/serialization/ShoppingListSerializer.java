/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventShoppingListEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ShoppingListSerializer extends BaseSerializer {


    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventShoppingListEntity.class;
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
        EventShoppingListEntity shoppingListEntity = (EventShoppingListEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(shoppingListEntity, serializerOptions);

        resultMap.put("status", shoppingListEntity.getStatus() == null ? null : shoppingListEntity.getStatus().toString());
        resultMap.put("description", shoppingListEntity.getDescription());

        // we just return the ID here
        resultMap.put("whoid", shoppingListEntity.getWhoId() == null ? null : shoppingListEntity.getWhoId().getId());

        // Here we attached directly the expense information
        if (shoppingListEntity.getExpense() != null) {
            BaseSerializer expenseSerializer = factorySerializer.getFromEntity(shoppingListEntity.getExpense());
            resultMap.put("expense", expenseSerializer.getMap(shoppingListEntity.getExpense(), parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }


        return resultMap;
    }
}

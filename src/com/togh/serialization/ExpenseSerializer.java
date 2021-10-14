/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventExpenseEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ExpenseSerializer extends BaseSerializer {
    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventExpenseEntity.class;
    }

    /**
     * GetMap - implement EntitySerialization
     *
     * @param baseEntity
     * @param contextAccess
     * @param timezoneOffset
     * @param factorySerializer
     * @return
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset, FactorySerializer factorySerializer) {
        EventExpenseEntity expenseEntity = (EventExpenseEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(expenseEntity, contextAccess, timezoneOffset);

        resultMap.put("budget", expenseEntity.getBudget());
        resultMap.put("cost", expenseEntity.getCost());
        return resultMap;
    }
}

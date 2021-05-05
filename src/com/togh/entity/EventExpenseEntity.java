/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.Data;
/* ******************************************************************************** */
/*                                                                                  */
/*  EventExpenseEntity,                                                             */
/*                                                                                  */
/*  Manage an expense. An expense can be attached in different position :           */ 
/*   ItineraryStep, ShoppingList, in direct                                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTEXPENSE")
public @Data class EventExpenseEntity  extends UserEntity {

    @Column(name = "budget")
    private BigDecimal  budget;
    
    
    @Column(name = "cost")
    private BigDecimal  cost;
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        
        resultMap.put("budget",budget);
        resultMap.put("cost", cost);
        return resultMap;
    }
}

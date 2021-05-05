/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;


import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventShoppingList                                                                      */
/*                                                                                  */
/*  Manage shopping list                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTSHOPPINGLIST")
@EqualsAndHashCode(callSuper=true)
public @Data class EventShoppingListEntity extends UserEntity {

    public enum ShoppingStatusEnum {
        TODO, DONE, CANCEL
    }
    @Column(name = "status", length=10, nullable=false)
    @org.hibernate.annotations.ColumnDefault("'TODO'")
    @Enumerated(EnumType.STRING)    
    private ShoppingStatusEnum status;

    
    
    // name is part of the baseEntity
    @Column( name="description", length=400)
    private String description;
   
    // User attached to this task (maybe an external user, why not ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "whoid")
    private ToghUserEntity whoId;

    // Expense attached to this task 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expenseid")
    private EventExpenseEntity expense;

    @Override
    public boolean acceptExpense() {
        return true;
    }
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        

        resultMap.put("status",status==null ? null : status.toString());
        resultMap.put("description", description);

        // we just return the ID here
        resultMap.put("whoid",whoId==null ? null :  whoId.getId());
        // Here we attached directly the expense information
        resultMap.put("expense", expense==null ? null : expense.getMap(contextAccess, timezoneOffset));
 
        return resultMap;
    }

}

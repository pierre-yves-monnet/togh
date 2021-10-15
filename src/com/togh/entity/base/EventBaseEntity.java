/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity.base;

import com.togh.entity.EventExpenseEntity;

import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventBaseEntity,                                                                */
/*                                                                                  */
/*  An event constains a lot of entity: task, survey, itinerary...                  */
/*  All theses entity must derived from this class, to implement generic method on  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@MappedSuperclass
@Inheritance
public abstract class EventBaseEntity extends UserEntity {


    /*
     * if the entity accept expense, it has to override this two methods
     */
    public boolean acceptExpense() {
        return false;
    }

    public void setExpense(EventExpenseEntity expense) {
        return;
    }
}

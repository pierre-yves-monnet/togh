/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;


import com.togh.entity.base.EventBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

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
@EqualsAndHashCode(callSuper = true)
public @Data
class EventShoppingListEntity extends EventBaseEntity {

  public static final String CST_SLABOPERATION_SHOPPINGLIST = "shoppinglist";
  @Column(name = "status", length = 10, nullable = false)
  @org.hibernate.annotations.ColumnDefault("'TODO'")
  @Enumerated(EnumType.STRING)
  private ShoppingStatusEnum status;
  // name is part of the baseEntity
  @Column(name = "description", length = 400)
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

  public enum ShoppingStatusEnum {
    TODO, DONE, CANCEL
  }

}

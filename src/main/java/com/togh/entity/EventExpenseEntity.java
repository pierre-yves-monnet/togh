/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
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
@EqualsAndHashCode(callSuper = false)
public @Data
class EventExpenseEntity extends UserEntity {

  public static final String CST_ENTITY_NAME = "expense";

  @Column(name = "budget")
  private BigDecimal budget;


  @Column(name = "cost")
  private BigDecimal cost;

}

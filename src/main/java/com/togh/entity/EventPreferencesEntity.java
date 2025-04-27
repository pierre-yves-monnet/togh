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

/* ******************************************************************************** */
/*                                                                                  */
/*  EventPreferences,                                                                      */
/*                                                                                  */
/*  Save preferences                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTPREFS")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventPreferencesEntity extends UserEntity {

  public static final String CST_SLABOPERATION_PREFERENCES = "preferences";
  public static final String CST_ENTITY_NAME = "preferences";

  @Column(name = "currencycode", length = 10, nullable = false)
  private String currencyCode;

  @Column(name = "accesschat")
  private Boolean accessChat;

  @Column(name = "accessitinerary")
  private Boolean accessItinerary;

  @Column(name = "accesstasks")
  private Boolean accessTasks;

  @Column(name = "accessbring")
  private Boolean accessBring;

  @Column(name = "accesssurveys")
  private Boolean accessSurveys;

  @Column(name = "accesslocalisation")
  private Boolean accessLocalisation;

  @Column(name = "accessgames")
  private Boolean accessGames;

  @Column(name = "accessphotos")
  private Boolean accessPhotos;

  @Column(name = "accessexpenses")
  private Boolean accessExpenses;

  @Column(name = "accessbudget")
  private Boolean accessBudget;
}

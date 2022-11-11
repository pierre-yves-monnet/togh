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
import java.time.LocalDate;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventItineraryStep,                                                                      */
/*                                                                                  */
/*  Manage task in a event                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTITINERARYSTEP")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventItineraryStepEntity extends EventBaseEntity {

  public static final String CST_SLABOPERATION_ITINERARYSTEPLIST = "itinerarysteplist";
  @Column(name = "category", length = 15)
  @Enumerated(EnumType.STRING)
  private CategoryEnum category;
  // name is part of the baseEntity
  @Column(name = "description", length = 400)
  private String description;
  @Column(name = "datestep", nullable = false)
  private LocalDate dateStep;
  @Column(name = "rownumber", nullable = false)
  private Integer rownumber;
  // format is HH:MM
  @Column(name = "visittime", length = 5)
  private String visitTime;
  @Column(name = "durationtime", length = 5)
  private String durationTime;
  @Column(name = "geoaddress", length = 300)
  private String geoaddress;
  @Column(name = "geolat")
  private Double geolat;
  @Column(name = "geolng")
  private Double geolng;
  @Column(name = "website", length = 300)
  private String website;
  // Expense attached to this task
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "expenseid")
  private EventExpenseEntity expense;

  @Override
  public boolean acceptExpense() {
    return true;
  }

  /**
   * The dateStep is manipulates by the interface, not by the user.
   */
  @Override
  public boolean isAbsoluteLocalDate(String attributName) {
    return ("dateStep".equalsIgnoreCase(attributName));
  }


  public enum CategoryEnum {
    POI, BEGIN, END, SHOPPING, AIRPORT, BUS, TRAIN, BOAT, NIGHT, VISIT, RESTAURANT, ENTERTAINMENT
  }


}

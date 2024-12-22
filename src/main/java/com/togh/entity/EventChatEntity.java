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

import javax.persistence.*;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventChat,                                                                      */
/*                                                                                  */
/*  Save a discussion                                                               */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTCHAT")
@EqualsAndHashCode(callSuper = true)

public @Data
class EventChatEntity extends UserEntity {

  public static final String CST_SLABOPERATION_CHAT = "chat";

  // User attached to this task (maybe an external user, why not ?
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "whoid")
  private ToghUserEntity whoId;

  @Column(name = "message", length = 400)
  private String message;


}

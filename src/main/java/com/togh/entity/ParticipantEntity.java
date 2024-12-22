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

import javax.persistence.*;

/* ******************************************************************************** */
/*                                                                                  */
/* Participant to an event */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTPARTICIPANT")
public @Data
class ParticipantEntity extends UserEntity {

  @Column(name = "role", length = 15, nullable = false)
  @Enumerated(EnumType.STRING)
  private ParticipantRoleEnum role;
  // User attached to this participant
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "userid")
  private ToghUserEntity user;
  @Column(name = "status", length = 10, nullable = false)
  @Enumerated(EnumType.STRING)
  private StatusEnum status;


  @Column(name = "partof", length = 10)
  @Enumerated(EnumType.STRING)
  private PartOfEnum partOf;


  @Column(name = "numberOfParticipants")
  private Integer numberOfParticipants;

  public ParticipantRoleEnum getRole() {
    return role;
  }

  public void setRole(ParticipantRoleEnum role) {
    this.role = role;
  }

  public ToghUserEntity getUser() {
    return user;
  }

  public void setUser(ToghUserEntity endUser) {
    this.user = endUser;
  }

  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }


  public enum ParticipantRoleEnum {
    OWNER, ORGANIZER, PARTICIPANT, OBSERVER, WAITCONFIR, EXTERNAL
  }

  public enum StatusEnum {INVITED, ACTIF, LEFT}

  public enum PartOfEnum {
    NO, PARTOF, DONTKNOW
  }
}

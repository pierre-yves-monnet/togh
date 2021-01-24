package com.togh.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.togh.entity.base.UserEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* Participant to an event */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "PARTICIPANT")
public class ParticipantEntity extends UserEntity {

   
    public static enum ParticipantRoleEnum {
        ORGANIZER, PARTICIPANT, OBSERVER
    }

    @Column(name = "role", length = 10, nullable = false)
    private ParticipantRoleEnum role;

    /*
     * public enum LIFESTATUS { INVITED, ACTIF, REJECTED }
     * @Column(name = "lifestatus", length=10, nullable = false )
     * public LIFESTATUS getLifeStatus() {
     * return LIFESTATUS.valueOf(getString("lifestatus"));
     * }
     * public void setLifeStatus( LIFESTATUS lifestatus) {
     * set( "lifestatus", lifestatus==null ? LIFESTATUS.INVITED.toString() : lifestatus.toString());
     * }
     */

    // attachment to the Event is done at the event level.
    // @ M anyToOne
    // @ J oinColumn(name="event_id")
    // p r ivate EventEntity event;

    // @ManyToOne
    // @JoinColumn(name = "eventid")
    // private EventEntity event;

    // User attached to this participant
    // @ O n e ToOne( fetch = FetchType.LAZY)    
    // private ToghUserEntity user;

    @Column(name = "userid")
    private Long userId;

    public enum StatusEnum {
        INVITED, ACTIF, LEFT
    }

    @Column(name = "status", length = 10, nullable = false)
    private StatusEnum status;

    public ParticipantRoleEnum getRole() {
        return role;
    }

    public void setRole(ParticipantRoleEnum role) {
        this.role = role;
    }

    /*
     * public ToghUserEntity getUser() {
     * return user;
     * }
     * public void setUser(ToghUserEntity endUser ) {
     * this.user=endUser;
     * }
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public StatusEnum getStatus() {
        return status;
    }

    
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    /*
     * public EventEntity getEvent() {
     * return event;
     * }
     * public void setEvent(EventEntity event) {
     * this.event = event;
     * }
     */
}

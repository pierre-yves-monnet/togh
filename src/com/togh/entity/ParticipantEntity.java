/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* Participant to an event */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTPARTICIPANT")
public class ParticipantEntity extends UserEntity {

    public enum ParticipantRoleEnum {
        OWNER, ORGANIZER, PARTICIPANT, OBSERVER, WAITCONFIR, EXTERNAL
    }

    @Column(name = "role", length=15, nullable = false)
    @Enumerated(EnumType.STRING)    
    private ParticipantRoleEnum role;

    // User attached to this participant
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private ToghUserEntity user;

    public enum StatusEnum {  INVITED, ACTIF, LEFT }

    @Column(name = "status", length=10, nullable = false)
    @Enumerated(EnumType.STRING)    
    private StatusEnum status;

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

   
    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * @return
     */
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("role", role==null ? null : role.toString());
        resultMap.put("user", user.getMap(contextAccess, timezoneOffset ));
        resultMap.put("id", getId());
        resultMap.put("status", status==null ? null : status.toString());

        return resultMap;
    }
}

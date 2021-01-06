package com.together.data.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.together.data.entity.EventEntity.SCOPEEVENT;
import com.together.data.entity.base.UserEntity;

/* ******************************************************************************** */
/*                                                                                  */
/*     Participant to an event                                                      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "PARTICIPANT")
public class ParticipantEntity extends UserEntity {
    
    public ParticipantEntity(Long authorId, String name) {
        super(authorId, name);
    }
    
    public enum ROLEPARTIPANT { ORGANISER, PARTICIPANT, OBSERVER }
    @Column(name = "role", length=10, nullable = false )
    public ROLEPARTIPANT getRole() {
        return ROLEPARTIPANT.valueOf(getString("role"));
    }
    public void setRole( ROLEPARTIPANT role) {
        set( "role", role==null ? ROLEPARTIPANT.OBSERVER.toString() : role.toString());
    }

    public enum LIFESTATUS { INVITED, ACTIF, REJECTED }
    @Column(name = "lifestatus", length=10, nullable = false )
    public LIFESTATUS getLifeStatus() {
        return LIFESTATUS.valueOf(getString("lifestatus"));
    }
    public void setLifeStatus( LIFESTATUS lifestatus) {
        set( "lifestatus", lifestatus==null ? LIFESTATUS.INVITED.toString() : lifestatus.toString());
    }

    // attachment to the Event is done at the event level.
    @ManyToOne
    private EndUserEntity endUser;
    public EndUserEntity getEndUser() {
        return endUser;
    }
    public void getEndUser(EndUserEntity endUser ) {
        this.endUser = endUser;
    }
    
    

    
}

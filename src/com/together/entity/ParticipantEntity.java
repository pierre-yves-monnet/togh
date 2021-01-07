package com.together.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.together.entity.base.UserEntity;
import com.together.entity.enumerations.ParticipantRoleEnum;

/* ******************************************************************************** */
/*                                                                                  */
/*     Participant to an event                                                      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "PARTICIPANT")
public class ParticipantEntity extends UserEntity {

    @Column(name = "role", length=10, nullable = false )
    private ParticipantRoleEnum role;

    /*public enum LIFESTATUS { INVITED, ACTIF, REJECTED }
    @Column(name = "lifestatus", length=10, nullable = false )
    public LIFESTATUS getLifeStatus() {
        return LIFESTATUS.valueOf(getString("lifestatus"));
    }
    public void setLifeStatus( LIFESTATUS lifestatus) {
        set( "lifestatus", lifestatus==null ? LIFESTATUS.INVITED.toString() : lifestatus.toString());
    }*/

    // attachment to the Event is done at the event level.
    @ManyToOne
    private EventEntity event;

	public ParticipantRoleEnum getRole() {
		return role;
	}

	public void setRole(ParticipantRoleEnum role) {
		this.role = role;
	}

	public EventEntity getEvent() {
		return event;
	}

	public void setEvent(EventEntity event) {
		this.event = event;
	}
}

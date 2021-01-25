package com.togh.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.base.UserEntity;
import com.togh.repository.EndUserRepository;


/* ******************************************************************************** */
/*                                                                                  */
/*    Event, the main class                                                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

// see https://github.com/spring-projects/spring-data-book/blob/master/jpa/src/main/java/com/oreilly/springdata/jpa/core/Customer.java

@Entity
@Table(name = "EVENTUSER")
public class EventEntity extends UserEntity {
	
    @Column(name = "dateevent")
    private LocalDateTime dateEvent;

    
    public enum TypeEventEnum {
        OPEN("OPEN"), OPENCONFIRMATION("OPENCONF"), LIMITED("LIMITED"), SECRET("SECRET");
        private String valueEnum;
        private TypeEventEnum( String value ) {
            this.valueEnum=value;
        }       
    }
    @Column( name="typeevent")
    private TypeEventEnum typeEvent;

    public enum StatusEventEnum {
        INPREPAR("INPREP"), INPROG("INPROG"), CLOSED("CLOSED"), CANCELLED("CANCEL");
        private String valueEnum;
        private StatusEventEnum( String value ) {
            this.valueEnum=value;
        }       
    }
    @Column( name="status")
    private StatusEventEnum statusEvent;
    
    @Column( name="description", length=400)
    private String description;
    
    
    public enum DatePolicyEnum {
        ONEDATE("ONEDATE"), PERIOD("PERIOD");
        private String valueEnum;
        private DatePolicyEnum( String value ) {
            this.valueEnum=value;
        }       
    }
    @Column( name="datePolicy")    
    private DatePolicyEnum datePolicy;
   
   
    public EventEntity(ToghUserEntity author, String name) {
        super(author, name);
        setTypeEvent(TypeEventEnum.LIMITED);
        setStatusEvent( StatusEventEnum.INPREPAR );

    }
    public EventEntity() { };
        
	public LocalDateTime getDateEvent() {
		return dateEvent;
	}

	public void setDateEvent(LocalDateTime dateEvent) {
		this.dateEvent = dateEvent;
	}


   
	public void setTypeEvent(TypeEventEnum typeEvent) {
		this.typeEvent = typeEvent;
	}

	   public TypeEventEnum getTypeEvent() {
	        return typeEvent;
	    }

	
	
	
	public StatusEventEnum getStatusEvent() {
		return statusEvent;
	}

	public void setStatusEvent(StatusEventEnum statusEvent) {
		this.statusEvent = statusEvent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActif() {
        return getStatusEvent().equals(StatusEventEnum.INPREPAR) || getStatusEvent().equals(StatusEventEnum.INPROG);
    }
	 
    public DatePolicyEnum getDatePolicy() {
        return datePolicy;
    }
    
    public void setDatePolicy(DatePolicyEnum datePolicy) {
        this.datePolicy = datePolicy;
    }
	

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Relation with another table                                                      */
    /*                                                                                  */
    /* ******************************************************************************** */
    
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "eventid")
    private List<ParticipantEntity> participants= new ArrayList<>();

    public List<ParticipantEntity> getParticipants() {
        return participants;
    }
    /** do not add a participant, which is a private information. So, don't take the risk to add accidentaly a participant from an another event
     * 
     * @param participant
     */
    public void addPartipant( ToghUserEntity userParticipant, ParticipantRoleEnum role, StatusEnum status ) {
        ParticipantEntity participant = new ParticipantEntity();
        participant.setUser(userParticipant);
        participant.setRole(role);
        participant.setStatus( status );
        participants.add( participant);
    }
    
    public String toString() {
        return "Event{" + super.toString() + "}";
    }
    
 

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Authorisation                                                                    */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * User must be the author, or a partipant, or should be invited
     * @param userId
     * @param event
     * @return
     */
    public boolean isAllowedUser( long userId) {
        return true;
    }
    /**
     * is this user an organizer? Some operation, like invitation, is allowed only for organizer
     * @param userId
     * @return
     */
    public boolean isOrganizer( long userId) {
        return true;
    }
    /**
     * 
     * is this user an active particpant? Active participant can modify an event (a observer is not a active participant) 
     * @param userId
     * @return
     */
    public boolean isActiveParticipant( long userId) {
        return true;
    }
    
    public ParticipantEntity getParticipant( long userId ) {
        return null;
    }

}

package com.togh.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;


/* ******************************************************************************** */
/*                                                                                  */
/*    Event, the main class                                                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

// see https://github.com/spring-projects/spring-data-book/blob/master/jpa/src/main/java/com/oreilly/springdata/jpa/core/Customer.java

@Entity
@Table(name = "EVT")
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
    public ParticipantEntity addPartipant( ToghUserEntity userParticipant, ParticipantRoleEnum role, StatusEnum status ) {
        ParticipantEntity participant = new ParticipantEntity();
        participant.setUser(userParticipant);
        participant.setRole(role);
        participant.setStatus( status );
        participants.add( participant);
        return participant;
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
     * isRegisteredParticipant. if this user is part of this event?
     * @param userId
     * @return
     */
    public boolean isAccess( long userId ) {
        if (typeEvent ==  typeEvent.OPEN)
            return true;
        return getParticipant( userId ) != null;
    }
    /**
     * User must be the author, or a partipant, or should be invited
     * @param userId
     * @param event
     * @return
     */
    public boolean isActiveParticipant( long userId) {
        ParticipantEntity participant  = getParticipant( userId );
        if (participant==null )
            return false;
        if (participant.getRole().equals(ParticipantRoleEnum.OWNER) 
                || participant.getRole().equals(ParticipantRoleEnum.ORGANIZER)
                || participant.getRole().equals(ParticipantRoleEnum.PARTICIPANT))
        {
            return participant.getStatus().equals( StatusEnum.ACTIF);
        }
        return false;
    }
    /**
     * is this user an organizer? Some operation, like invitation, is allowed only for organizer
     * @param userId
     * @return
     */
    public boolean isOrganizer( long userId) {
        ParticipantEntity participant  = getParticipant( userId );
        if (participant==null )
            return false;
        return participant.getRole().equals(ParticipantRoleEnum.OWNER) ||participant.getRole().equals(ParticipantRoleEnum.ORGANIZER);
    }
    /**
     *  get the role of this userId in the event. Return null if the user does not have any participation
     * @param userId
     * @return
     */
    public ParticipantRoleEnum getRoleEnum( long userId) {
        ParticipantEntity participant  = getParticipant( userId );
        return (participant==null ? null : participant.getRole());
    }
    
    /**
     * 
     * @param userId
     * @return
     */
    public ParticipantEntity getParticipant( long userId) {
        for (ParticipantEntity participant : participants) {
            if (participant.getUserId().equals( userId))
                return participant;
        }
        return null;
    }
    
    

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Serialization                                                                    */
    /*                                                                                  */
    /* ******************************************************************************** */

    
    public Map<String,Object> getMap( ContextAccess contextAccess) {
        Map<String,Object> resultMap = super.getMap( contextAccess );
        
        resultMap.put("dateEvent", formatDate( dateEvent));
        resultMap.put("typeEvent", typeEvent==null ? null : typeEvent.toString());
        resultMap.put("statusEvent", statusEvent==null ? null : statusEvent.toString());
        resultMap.put("description", description);
        
        if (contextAccess != ContextAccess.PUBLICACCESS) { 
            resultMap.put("datePolicy", datePolicy==null ? null : datePolicy.toString());
        }
       if (typeEvent == TypeEventEnum.OPEN || contextAccess != contextAccess.PUBLICACCESS) {
           List<Map<String,Object>> listParticipantsMap = new ArrayList();
           for (ParticipantEntity participant : participants) {
               listParticipantsMap.add( participant.getMap(contextAccess));
           }
          resultMap.put("participants", listParticipantsMap);
       }
       
        return resultMap;
    }
    
    public Map<String,Object> getHeaderMap( ContextAccess contextAccess) {
        Map<String,Object> resultMap = super.getMap( contextAccess );
        resultMap.put("name", getName());
        resultMap.put("dateEvent", formatDate( dateEvent));
        resultMap.put("typeEvent", typeEvent==null ? null : typeEvent.toString());
        resultMap.put("statusEvent", statusEvent==null ? null : statusEvent.toString());
        return resultMap;

    }
        
    /**
     * According the user, and the type of event, the ContextAccess is calculated
     * @param event
     * @return
     */
    public ContextAccess getTypeAccess( long userId ) {
        // event is public : so show onkly what you want to show to public
        if (typeEvent == TypeEventEnum.OPEN)
            return ContextAccess.PUBLICACCESS;
        // event is secret : hide all at maximum
        if (typeEvent == TypeEventEnum.SECRET)
            return ContextAccess.SECRETACCESS;

        ParticipantEntity participant = getParticipant( userId);
        if (typeEvent == TypeEventEnum.OPENCONFIRMATION) {
            // the user is not accepted : show the minimum.
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == StatusEnum.ACTIF)
                return ContextAccess.PUBLICACCESS;
            // user left, or wait for the confirmation 
            return ContextAccess.SECRETACCESS;
        }
        if (typeEvent == TypeEventEnum.LIMITED) {
            if (participant == null)
                return ContextAccess.SECRETACCESS;
            if (participant.getStatus() == StatusEnum.ACTIF)
                return ContextAccess.FRIENDACCESS;
            // user left, or wait for the confirmation 
            return ContextAccess.SECRETACCESS;
        }
        // should not be here
        return ContextAccess.SECRETACCESS;
        
    }
}

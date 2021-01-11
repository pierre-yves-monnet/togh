package com.together.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.together.entity.base.UserEntity;


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
	
    private LocalDateTime dateEvent;

    public enum TypeEventEnum {
        OPEN("OPEN"), OPENCONFIRMATION("OPENCONF"), LIMITED("LIMITED"), SECRET("SECRET");
        private String valueEnum;
        private TypeEventEnum( String value ) {
            this.valueEnum=value;
        }       
    }
    private TypeEventEnum typeEvent;

    public enum StatusEventEnum {
        INPREPAR("INPREP"), INPROG("INPROG"), CLOSED("CLOSED"), CANCELLED("CANCEL");
        private String valueEnum;
        private StatusEventEnum( String value ) {
            this.valueEnum=value;
        }       
    }
    private StatusEventEnum statusEvent;
    
    
    private String description;
    
    @OneToMany(mappedBy = "event")
    private List<ParticipantEntity> participants;

    public EventEntity(long authorId, String name) {
        super(authorId, name);
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

	public List<ParticipantEntity> getParticipants() {
		return participants;
	}

	public void setParticipants(List<ParticipantEntity> participants) {
		this.participants = participants;
	}

	public boolean isActif() {
        return getStatusEvent().equals(StatusEventEnum.INPREPAR) || getStatusEvent().equals(StatusEventEnum.INPROG);
    }
   
    public String toString() {
        return "Event{" + super.toString() + "}";
    }
    
  
}

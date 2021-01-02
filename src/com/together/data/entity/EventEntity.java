package com.together.data.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.json.simple.JSONValue;

import com.together.data.entity.base.BaseEntity;
import com.together.data.entity.base.UserEntity;


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

    public EventEntity(long authorId, String name) {
        super(authorId, name);
        setTypeEvent(TYPEEVENT.LIMITED);
        setStatusEvent( STATUSEVENT.INPREPAR );

    }

    @Column(name = "DateEvent")
    public LocalDateTime getDateEvent() {
        return getDate("dateEvent");
    }

    /**
     * Type of event : 
     * OPEN : any participant can register a new participant.
     * LIMITED: only administrator can register new partipant (default)
     * MASKED : all participant (included the creator) are masked, nobody can access their name 
     * @author Firstname Lastname
     *
     */
    public enum TYPEEVENT { OPEN, LIMITED }
    @Column(name = "typeevent", length=10, nullable = false )
    public TYPEEVENT getTypeEvent() {
        return TYPEEVENT.valueOf(getString("typeevent"));
    }
    public void setTypeEvent( TYPEEVENT typeEvent) {
        set( "typeevent", typeEvent==null ? TYPEEVENT.LIMITED.toString() : typeEvent.toString());
    }

    public enum STATUSEVENT { INPREPAR, INPROG, CLOSED, CANCELLED }
    @Column(name = "statusevent", length=10, nullable = false )
    public STATUSEVENT getStatusEvent() {
        return STATUSEVENT.valueOf(getString("statusevent"));
    }
    public void setStatusEvent( STATUSEVENT statusEvent) {
        set( "statusevent", statusEvent==null ? STATUSEVENT.INPREPAR.toString() : statusEvent.toString());
    }
    public boolean isActif() {
        return getStatusEvent().equals(STATUSEVENT.INPREPAR) || getStatusEvent().equals(STATUSEVENT.INPROG);
    }
    
    
    @Column(name = "DESCRIPTION", length=100 )
    public void setDescription(String description) {
        set("description", description,100);
    }
    public String getDescription() {
        return getString("description");
    }



    /* ******************************************************************************** */
    /*                                                                                  */
    /* Relation with another table                                                      */
    /*                                                                                  */
    /* ******************************************************************************** */
    
    @OneToMany(mappedBy = "PartipantEntity")
    private List<ParticipantEntity> participants;

    public List<ParticipantEntity> getPartipants() {
        return participants;
    }
    public void addPartipants( ParticipantEntity participant) {
        participants.add( participant);
    }
    
    
    public String toString() {
        return "Event{" + super.toString() + "}";
    }
    
  
}

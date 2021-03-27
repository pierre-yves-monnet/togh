/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.Data;

/* ******************************************************************************** */
/*                                                                                  */
/* Event, the main class */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

// see https://github.com/spring-projects/spring-data-book/blob/master/jpa/src/main/java/com/oreilly/springdata/jpa/core/Customer.java

@Entity
@Table(name = "EVT")
public @Data class EventEntity extends UserEntity {

    @Column(name = "dateevent")
    private LocalDateTime dateEvent;

    /**
     * In case of a policy Period, a Start and End event are provide.
     */
    @Column(name = "datestartevent")
    private LocalDateTime dateStartEvent;

    @Column(name = "dateendevent")
    private LocalDateTime dateEndEvent;

    public enum TypeEventEnum {
        OPEN, OPENCONF, LIMITED, SECRET
    }

    @Column(name = "typeevent", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeEventEnum typeEvent;

    public enum StatusEventEnum {
        INPREPAR, INPROG, CLOSED, CANCELLED
    }

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private StatusEventEnum statusEvent;

    @Column(name = "description", length = 400)
    private String description;

    public enum DatePolicyEnum {
        ONEDATE, PERIOD
    }

    @Column(name = "datepolicy", length = 10, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'ONEDATE'")
    @Enumerated(EnumType.STRING)
    private DatePolicyEnum datePolicy;

    @Column(name = "timeevent", length = 5)
    private String timeevent;

    @Column(name = "durationevent", length = 5)
    private String duration;

    public enum ScopeEnum {
        OPEN, OPENCONF, LIMITED, SECRET
    }

    @Column(name = "scope", length = 10)
    @Enumerated(EnumType.STRING)
    private ScopeEnum scope;

    public EventEntity(ToghUserEntity author, String name) {
        super(author, name);
        setTypeEvent(TypeEventEnum.LIMITED);
        setStatusEvent(StatusEventEnum.INPREPAR);

    }

    public EventEntity() {
    };

    /*
     * public LocalDateTime getDateEvent() {
     * return dateEvent;
     * }
     * public void setDateEvent(LocalDateTime dateEvent) {
     * this.dateEvent = dateEvent;
     * }
     * public void setTypeEvent(TypeEventEnum typeEvent) {
     * this.typeEvent = typeEvent;
     * }
     * public TypeEventEnum getTypeEvent() {
     * return typeEvent;
     * }
     * public StatusEventEnum getStatusEvent() {
     * return statusEvent;
     * }
     * public void setStatusEvent(StatusEventEnum statusEvent) {
     * this.statusEvent = statusEvent;
     * }
     * public String getDescription() {
     * return description;
     * }
     * public void setDescription(String description) {
     * this.description = description;
     * }
     * public boolean isActif() {
     * return getStatusEvent().equals(StatusEventEnum.INPREPAR) || getStatusEvent().equals(StatusEventEnum.INPROG);
     * }
     * public DatePolicyEnum getDatePolicy() {
     * return datePolicy;
     * }
     * public void setDatePolicy(DatePolicyEnum datePolicy) {
     * this.datePolicy = datePolicy;
     * }
     * public LocalDateTime getDateStartEvent() {
     * return dateStartEvent;
     * }
     * public void setDateStartEvent(LocalDateTime dateStartEvent) {
     * this.dateStartEvent = dateStartEvent;
     * }
     * public LocalDateTime getDateEndEvent() {
     * return dateEndEvent;
     * }
     * public void setDateEndEvent(LocalDateTime dateEndEvent) {
     * this.dateEndEvent = dateEndEvent;
     * }
     * public void setParticipants(List<ParticipantEntity> participants) {
     * this.participants = participants;
     * }
     * public String getTimeevent() {
     * return timeevent;
     * }
     * public void setTimeevent(String timeevent) {
     * this.timeevent = timeevent;
     * }
     * public String getDuration() {
     * return duration;
     * }
     * public void setDuration(String duration) {
     * this.duration = duration;
     * }
     * public ScopeEnum getScope() {
     * return scope;
     * }
     * public void setScope(ScopeEnum scope) {
     * this.scope = scope;
     * }
     */

    /**
     * getRealTimeUtc
     * Real time is the dateEvent (which contains the time zone) + the time (which contains a jour/mn in the day)
     */
    // getDateEventUTC
    // getDateEndEventUTC
    // getDelayToStart
    // getDelayToEnd

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Participants                                                                     */
    /*                                                                                  */
    /* ******************************************************************************** */

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "eventid")
    private List<ParticipantEntity> participants = new ArrayList<>();

    /*
     * public List<ParticipantEntity> getParticipants() {
     * return participants;
     * }
     */
    /**
     * do not add a participant, which is a private information. So, don't take the risk to add accidentaly a participant from an another event
     * 
     * @param participant
     */
    public ParticipantEntity addPartipant(ToghUserEntity userParticipant, ParticipantRoleEnum role, StatusEnum status) {
        ParticipantEntity participant = new ParticipantEntity();
        participant.setUser(userParticipant);
        participant.setRole(role);
        participant.setStatus(status);
        participants.add(participant);
        return participant;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Tasklist */
    /*                                                                                  */
    /* ******************************************************************************** */

    @Column(name = "tasklistshowdates")
    private Boolean taskListShowDates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "eventid")
    @OrderBy("id")
    private List<EventTaskEntity> taskList = new ArrayList<>();

    public EventTaskEntity addTask(EventTaskEntity onetask) {
        taskList.add(onetask);
        return onetask;
    }

   /* public List<EventTaskEntity> getTasksList() {
        return taskList;
    }
*/
    /**
     * Remove a task.
     * 
     * @param task
     * @return true if the task exist and is removed, false else
     */
    public boolean removeTask(EventTaskEntity task) {
        for (EventTaskEntity taskIterator : taskList) {
            if (taskIterator.getId().equals(task.getId())) {
                taskList.remove(taskIterator);
                return true;
            }
        }
        return false;
    }
    
    
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Serialization */
    /*                                                                                  */
    /* ******************************************************************************** */

    public Map<String, Object> getMap(ContextAccess contextAccess) {
        Map<String, Object> resultMap = super.getMap(contextAccess);

        resultMap.put("dateEvent", EngineTool.dateToString(dateEvent));
        resultMap.put("dateStartEvent", EngineTool.dateToString(dateStartEvent));
        resultMap.put("dateEndEvent", EngineTool.dateToString(dateEndEvent));
        resultMap.put("typeEvent", typeEvent == null ? null : typeEvent.toString());
        resultMap.put("statusEvent", statusEvent == null ? null : statusEvent.toString());
        resultMap.put("description", description);
        resultMap.put("tasklistshowdates", taskListShowDates);
        
        if (contextAccess != ContextAccess.PUBLICACCESS) {
            resultMap.put("datePolicy", datePolicy == null ? null : datePolicy.toString());
        }
        if (typeEvent == TypeEventEnum.OPEN || contextAccess != ContextAccess.PUBLICACCESS) {
            List<Map<String, Object>> listParticipantsMap = new ArrayList<>();
            for (ParticipantEntity participant : participants) {
                listParticipantsMap.add(participant.getMap(contextAccess));
            }
            resultMap.put("participants", listParticipantsMap);

            // get task
            List<Map<String, Object>> listTasksMap = new ArrayList<>();
            for (EventTaskEntity tasks : taskList) {
                listTasksMap.add(tasks.getMap(contextAccess));
            }
            resultMap.put("tasklist", listTasksMap);
        }

        return resultMap;

    }

    public Map<String, Object> getHeaderMap(ContextAccess contextAccess) {
        Map<String, Object> resultMap = super.getMap(contextAccess);
        resultMap.put("name", getName());
        resultMap.put("dateEvent", EngineTool.dateToString(dateEvent));
        resultMap.put("dateStartEvent", EngineTool.dateToString(dateStartEvent));
        resultMap.put("dateEndEvent", EngineTool.dateToString(dateEndEvent));
        resultMap.put("datePolicy", datePolicy.toString());
        resultMap.put("typeEvent", typeEvent == null ? null : typeEvent.toString());
        resultMap.put("statusEvent", statusEvent == null ? null : statusEvent.toString());
        return resultMap;
    }

    public String toString() {
        return "Event{" + super.toString() + "}";
    }

}

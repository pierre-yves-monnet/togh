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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/* Event, the main class */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

// see https://github.com/spring-projects/spring-data-book/blob/master/jpa/src/main/java/com/oreilly/springdata/jpa/core/Customer.java

@Entity
@Table(name = "EVT")
@EqualsAndHashCode(callSuper=true)
public @Data class EventEntity extends UserEntity {
    
    public static final String CST_SLABOPERATION_ITINERARYSTEPLIST = "itinerarysteplist";
    public static final String CST_SLABOPERATION_TASKLIST = "tasklist";
    public static final String CST_SLABOPERATION_SHOPPINGLIST = "shoppinglist";
    public static final String CST_SLABOPERATION_SURVEYLIST = "surveylist";
    
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

    
    @Column(name = "geoaddress", length=300)
    private String geoaddress;

    @Column(name = "geolat")
    private Double geolat;

    
    @Column(name = "geolng")
    private Double geolng;
    
    @Column(name = "geoinstructions", length = 400)
    private String geoinstructions;

    public EventEntity(ToghUserEntity author, String name) {
        super(author, name);
        setTypeEvent(TypeEventEnum.LIMITED);
        setStatusEvent(StatusEventEnum.INPREPAR);

    }

    public EventEntity() {
    }


    

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
    /* Participants */
    /*                                                                                  */
    /* ******************************************************************************** */

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "eventid")
    private List<ParticipantEntity> participantList = new ArrayList<>();

   
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
        participantList.add(participant);
        return participant;
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Itinerary */
    /*                                                                                  */
    /* ******************************************************************************** */

    @Column(name = "itineraryshowmap")
    @org.hibernate.annotations.ColumnDefault("'1'")
    private Boolean itineraryShowMap;

    @Column(name = "itineraryshowdetails")
    @org.hibernate.annotations.ColumnDefault("'1'")
    private Boolean itineraryShowDetails;

    @Column(name = "itineraryshowexpenses")
    @org.hibernate.annotations.ColumnDefault("'1'")
    private Boolean itineraryShowExpenses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "eventid")
    @OrderBy("rownumber")
    private List<EventItineraryStepEntity> itineraryStepList = new ArrayList<>();

    public EventItineraryStepEntity addItineraryStep(EventItineraryStepEntity oneStep) {
        itineraryStepList.add(oneStep);
        return oneStep;
    }

    /**
     * Remove a task.
     * 
     * @param task
     * @return true if the task exist and is removed, false else
     */
    public boolean removeItineraryStep(EventItineraryStepEntity oneStep) {
        for (EventItineraryStepEntity stepIterator : itineraryStepList) {
            if (stepIterator.getId().equals(oneStep.getId())) {
                itineraryStepList.remove(stepIterator);
                return true;
            }
        }
        return false;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Tasklist */
    /*                                                                                  */
    /* ******************************************************************************** */

    @Column(name = "tasklistshowdates")
    private Boolean taskListShowDates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "eventid")
    @OrderBy("id")
    private List<EventTaskEntity> taskList = new ArrayList<>();

    public EventTaskEntity addTask(EventTaskEntity onetask) {
        taskList.add(onetask);
        return onetask;
    }
   
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
    /* ShoppingList */
    /*                                                                                  */
    /* ******************************************************************************** */
    @Column(name = "shoppinglistshowdetails")
    @org.hibernate.annotations.ColumnDefault("'1'")
    private Boolean shoppingListShowDetails;

    @Column(name = "shoppinglistshowexpenses")
    @org.hibernate.annotations.ColumnDefault("'1'")
    private Boolean shoppinglistShowExpenses;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "eventid")
    @OrderBy("id")
    private List<EventShoppingListEntity> shoppingList = new ArrayList<>();

    public EventShoppingListEntity addShoppingList(EventShoppingListEntity onetask) {
        shoppingList.add(onetask);
        return onetask;
    }

    /**
     * Remove a task.
     * 
     * @param task
     * @return true if the task exist and is removed, false else
     */
    public boolean removeShoppingList(EventShoppingListEntity task) {
        for (EventShoppingListEntity shoppingListIterator : shoppingList) {
            if (shoppingListIterator.getId().equals(task.getId())) {
                shoppingList.remove(shoppingListIterator);
                return true;
            }
        }
        return false;
    }
    
    
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Survey */
    /*                                                                                  */
    /* ******************************************************************************** */

    // @Column(name = "tasklistshowdates")
    //     private Boolean surveyListShowDates;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size=100)
    @JoinColumn(name = "eventid")
    @OrderBy("id")
    private List<EventSurveyEntity> surveyList = new ArrayList<>();

    public EventSurveyEntity addSurvey(EventSurveyEntity onesurvey) {
        surveyList.add(onesurvey);
        return onesurvey;
    }

   
    /**
     * Remove a task.
     * 
     * @param task
     * @return true if the task exist and is removed, false else
     */
    public boolean removeSurvey(EventSurveyEntity task) {
        for (EventSurveyEntity surveyIterator : surveyList) {
            if (surveyIterator.getId().equals(task.getId())) {
                surveyList.remove(surveyIterator);
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

    @Override
    public Map<String, Object> getMap(ContextAccess contextAccess, Long timezoneOffset) {
        Map<String, Object> resultMap = super.getMap(contextAccess, timezoneOffset);

        resultMap.put("dateEvent", EngineTool.dateToString(dateEvent));
        resultMap.put("dateStartEvent", EngineTool.dateToString(dateStartEvent));
        resultMap.put("dateEndEvent", EngineTool.dateToString(dateEndEvent));
        resultMap.put("typeEvent", typeEvent == null ? null : typeEvent.toString());
        resultMap.put("statusEvent", statusEvent == null ? null : statusEvent.toString());
        resultMap.put("description", description);
        
        resultMap.put("tasklistshowdates",          taskListShowDates);
        
        resultMap.put("itineraryshowmap",           itineraryShowMap);
        resultMap.put("itineraryshowdetails",       itineraryShowDetails);
        resultMap.put("itineraryshowexpenses",      itineraryShowExpenses);

        resultMap.put("shoppinglistshowdetails",    shoppingListShowDetails);
        resultMap.put("shoppinglistshowexpenses",   shoppinglistShowExpenses);

        if (contextAccess != ContextAccess.PUBLICACCESS) {
            resultMap.put("datePolicy", datePolicy == null ? null : datePolicy.toString());
        }
        if (typeEvent == TypeEventEnum.OPEN || contextAccess != ContextAccess.PUBLICACCESS) {
            List<Map<String, Object>> listParticipantsMap = new ArrayList<>();
            for (ParticipantEntity participant : participantList) {
                listParticipantsMap.add(participant.getMap(contextAccess, timezoneOffset));
            }
            resultMap.put("participants", listParticipantsMap);

            // get task
            List<Map<String, Object>> listTasksMap = new ArrayList<>();
            for (EventTaskEntity tasks : taskList) {
                listTasksMap.add(tasks.getMap(contextAccess, timezoneOffset));
            }
            resultMap.put(CST_SLABOPERATION_TASKLIST, listTasksMap);
            
            // get task
            List<Map<String, Object>> listItineraryStepMap = new ArrayList<>();
            for (EventItineraryStepEntity itineraryStep : itineraryStepList) {
                listItineraryStepMap.add(itineraryStep.getMap(contextAccess, timezoneOffset));
            }
            resultMap.put(CST_SLABOPERATION_ITINERARYSTEPLIST, listItineraryStepMap);
       
            // get Shoppinglist
            List<Map<String, Object>> listShoppinglistMap = new ArrayList<>();
            for (EventShoppingListEntity shoppingListStep : shoppingList) {
                listShoppinglistMap.add(shoppingListStep.getMap(contextAccess, timezoneOffset));
            }
            resultMap.put(CST_SLABOPERATION_SHOPPINGLIST, listShoppinglistMap);
            
            // get Surveylist
            List<Map<String, Object>> listSurveylistMap = new ArrayList<>();
            for (EventSurveyEntity surveyStep : surveyList) {
                listSurveylistMap.add(surveyStep.getMap(contextAccess, timezoneOffset));
            }
            resultMap.put(CST_SLABOPERATION_SURVEYLIST, listSurveylistMap);
         
        }

        return resultMap;

    }

    
    public Map<String, Object> getHeaderMap(ContextAccess contextAccess, Long timezoneOffset) {
        Map<String, Object> resultMap = super.getMap(contextAccess, timezoneOffset);
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

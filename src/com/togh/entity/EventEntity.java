/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.ParticipantEntity.StatusEnum;
import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* ******************************************************************************** */
/*                                                                                  */
/* Event, the main class */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

// see https://github.com/spring-projects/spring-data-book/blob/master/jpa/src/main/java/com/oreilly/springdata/jpa/core/Customer.java

@Entity
@Table(name = "EVT")
@EqualsAndHashCode(callSuper = true)
public @Data class EventEntity extends UserEntity {




    @Column(name = "dateevent")
    private LocalDateTime dateEvent;

    /**
     * In case of a policy Period, a Start and End event are provide.
     * Nota: LocalDateTime in UTC timezone
     */
    @Column(name = "datestartevent")
    private LocalDateTime dateStartEvent;

    @Column(name = "dateendevent")
    private LocalDateTime dateEndEvent;

    /**
     * Date are store in UTC, and can be translated in any brower timezone.
     * But when we publish the event (by Email), we have to translate the time in a time zone:
     * for example, when the event is created by John, California, email invitation to new user should be displayed in that time zone.
     * When we send a email to an existing Togh user, we have it's prefered time zone.
     */
    @Column(name = "eventtimezone", length = 10)
    private String eventTimeZone;

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

    @Column(name = "geoaddress", length = 300)
    private String geoaddress;

    @Column(name = "geolat")
    private Double geolat;

    @Column(name = "geolng")
    private Double geolng;

    @Column(name = "geoinstructions", length = 400)
    private String geoinstructions;

    public enum SubscriptionEventEnum {
        FREE, PREMIUM, EXCELLENCE
    }

    @Column(name = "subscriptionevent", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'FREE'")
    SubscriptionEventEnum subscriptionEvent;

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
    @BatchSize(size = 100)
    @JoinColumn(name = "eventid")
    private List<ParticipantEntity> participantList = new ArrayList<>();

    /**
     * Add a participant
     *
     * @param userParticipant Add this user as a participant
     * @param role            Role of the participant
     * @param status          status
     */
    public ParticipantEntity addParticipant(ToghUserEntity userParticipant, ParticipantRoleEnum role, StatusEnum status) {
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
    @BatchSize(size = 100)
    @JoinColumn(name = "eventid")
    @OrderBy("rownumber")
    private List<EventItineraryStepEntity> itineraryStepList = new ArrayList<>();

    public EventItineraryStepEntity addItineraryStep(EventItineraryStepEntity oneStep) {
        itineraryStepList.add(oneStep);
        return oneStep;
    }

    /**
     * Remove an itinerary step.
     *
     * @param oneStep step to remove
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
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
    /* Chat */
    /*                                                                                  */
    /* ******************************************************************************** */

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "eventid")
    @OrderBy("id")
    private List<EventGroupChatEntity> groupChatList = new ArrayList<>();

    public EventGroupChatEntity addGroupChat(EventGroupChatEntity groupChatEntity) {
        groupChatList.add(groupChatEntity);
        return groupChatEntity;
    }
    public boolean removeGroupChat(EventGroupChatEntity groupChatEntity) {
        for (EventGroupChatEntity groupChatIterator : groupChatList) {
            if (groupChatIterator.getId().equals(groupChatEntity.getId())) {
                groupChatList.remove(groupChatIterator);
                return true;
            }
        }
        return false;
    }
    
    public EventChatEntity addChat(EventGroupChatEntity groupChatEntity, EventChatEntity chatEntity, int maxChatEntity) {
        groupChatEntity.addChat(chatEntity);
        return chatEntity;
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Serialization */
    /*                                                                                  */
    /* ******************************************************************************** */


    @Override
    public String toString() {
        return "Event{" + super.toString() + "}";
    }

    public static class AdditionalInformationEvent {
        /**
         * Participants will be added with a String
         */
        public boolean withParticipantsAsString = false;
    }

}

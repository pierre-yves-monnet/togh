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
import com.togh.tool.ToolCast;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
public @Data
class EventEntity extends UserEntity {


  @Column(name = "subscriptionevent", length = 10, nullable = false)
  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.ColumnDefault("'FREE'")
  SubscriptionEventEnum subscriptionEvent;
  @Column(name = "dateevent")
  private LocalDateTime dateEvent;
  /**
   * In case of a policy Period, a Start and End event are provided.
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
  @Column(name = "typeevent", length = 10, nullable = false)
  @Enumerated(EnumType.STRING)
  private TypeEventEnum typeEvent;
  @Column(name = "status", length = 10)
  @Enumerated(EnumType.STRING)
  private StatusEventEnum statusEvent;
  @Column(name = "description", length = 400)
  private String description;
  @Column(name = "datepolicy", length = 10, nullable = false)
  @org.hibernate.annotations.ColumnDefault("'ONEDATE'")
  @Enumerated(EnumType.STRING)
  private DatePolicyEnum datePolicy;

  @Column(name = "timeevent", length = 5)
  private String timeevent;
  @Column(name = "durationevent", length = 5)
  private String durationEvent;
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
  @OrderBy("id")
  private List<ParticipantEntity> participantList = new ArrayList<>();

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

  @Column(name = "tasklistshowdates")
  private Boolean taskListShowDates;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SELECT)
  @BatchSize(size = 100)
  @JoinColumn(name = "eventid")
  @OrderBy("id")
  private List<EventTaskEntity> taskList = new ArrayList<>();

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

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Itinerary */
  /*                                                                                  */
  /* ******************************************************************************** */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SELECT)
  @BatchSize(size = 100)
  @JoinColumn(name = "eventid")
  @OrderBy("id")
  private List<EventShoppingListEntity> shoppingList = new ArrayList<>();
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

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SELECT)
  @BatchSize(size = 100)
  @JoinColumn(name = "eventid")
  @OrderBy("id")
  private List<EventGroupChatEntity> groupChatList = new ArrayList<>();

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Game */
  /*                                                                                  */
  /* ******************************************************************************** */
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SELECT)
  @BatchSize(size = 100)
  @JoinColumn(name = "eventid")
  @OrderBy("id")
  private List<EventGameEntity> gameList = new ArrayList<>();

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Preferences */
  /*                                                                                  */
  /* ******************************************************************************** */
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "eventid")
  private EventPreferencesEntity preferences = null;


  public EventEntity(ToghUserEntity author, String name) {
    super(author, name);
    setTypeEvent(TypeEventEnum.LIMITED);
    setStatusEvent(StatusEventEnum.INPREPAR);

  }

  public EventEntity() {
  }

  public LocalDateTime getWhenTheEventStart() {
    if (DatePolicyEnum.ONEDATE.equals(getDatePolicy()))
      return getDateEvent();
    else if (DatePolicyEnum.PERIOD.equals(getDatePolicy()))
      return getDateStartEvent();
    return null;
  }

  public boolean isEventStarted() {
    LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
    LocalDateTime dateStart = getWhenTheEventStart();
    return dateStart != null && dateStart.isBefore(currentDate);
  }


  public Long getDurationInMinutes() {
    if (getDurationEvent() == null)
      return null;
    StringTokenizer st = new StringTokenizer(getDurationEvent(), ":");
    String hour = st.nextToken();
    String mn = st.nextToken();
    return ToolCast.getLong(hour, 0L) * 60 + ToolCast.getLong(mn, 0L);
  }

  public LocalDateTime getWhenTheEventEnd() {
    if (DatePolicyEnum.ONEDATE.equals(getDatePolicy())) {
      Long durationMinutes = getDurationInMinutes();
      if (getDateEvent() == null || durationMinutes == null)
        return null;
      return getDateEvent().minusMinutes(-getDurationInMinutes());
    } else if (DatePolicyEnum.PERIOD.equals(getDatePolicy()))
      return getDateEndEvent();
    return null;
  }

  public boolean isEventEnded() {
    LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
    LocalDateTime dateEnd = getWhenTheEventEnd();
    return dateEnd != null && dateEnd.isAfter(currentDate);
  }

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
    participant.setPartOf(ParticipantEntity.PartOfEnum.DONTKNOW);
    participant.setNumberOfParticipants(0);
    participantList.add(participant);
    return participant;
  }

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Itinerary */
  /*                                                                                  */
  /* ******************************************************************************** */
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
  /* Tasks */
  /*                                                                                  */
  /* ******************************************************************************** */
  public EventTaskEntity addTask(EventTaskEntity onetask) {
    taskList.add(onetask);
    return onetask;
  }

  /**
   * Remove a task.
   *
   * @param taskEntity task to remove
   * @return true if the task exist and is removed, false else
   */
  public boolean removeTask(EventTaskEntity taskEntity) {
    for (EventTaskEntity taskIterator : taskList) {
      if (taskIterator.getId().equals(taskEntity.getId())) {
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

  public EventShoppingListEntity addShoppingList(EventShoppingListEntity onetask) {
    shoppingList.add(onetask);
    return onetask;
  }

  /**
   * Remove a task.
   *
   * @param shoppingListEntity shopping to remove
   * @return true if the task exist and is removed, false else
   */
  public boolean removeShoppingList(EventShoppingListEntity shoppingListEntity) {
    for (EventShoppingListEntity shoppingListIterator : shoppingList) {
      if (shoppingListIterator.getId().equals(shoppingListEntity.getId())) {
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
  public EventSurveyEntity addSurvey(EventSurveyEntity onesurvey) {
    surveyList.add(onesurvey);
    return onesurvey;
  }

  /**
   * Remove a task.
   *
   * @param surveyEntity Survey Entity to remove
   * @return true if the task exist and is removed, false else
   */
  public boolean removeSurvey(EventSurveyEntity surveyEntity) {
    for (EventSurveyEntity surveyIterator : surveyList) {
      if (surveyIterator.getId().equals(surveyEntity.getId())) {
        surveyList.remove(surveyIterator);
        return true;
      }
    }
    return false;
  }

  /* ******************************************************************************** */
  /*                                                                                  */
  /* GroupChat and Chat */
  /*                                                                                  */
  /* ******************************************************************************** */

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

  /**
   * Add a Chat in a group
   *
   * @param groupChatEntity group to add the chat
   * @param chatEntity      chat to add
   * @param maxChatEntity   maximum chat allowed
   * @return a new EventChatEntity
   */
  public EventChatEntity addChat(EventGroupChatEntity groupChatEntity, EventChatEntity chatEntity, int maxChatEntity) {
    groupChatEntity.addChat(chatEntity);
    return chatEntity;
  }

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Game */
  /*                                                                                  */
  /* ******************************************************************************** */

  /**
   * Add a game
   *
   * @param oneGame game to add
   * @return the game added
   */
  public EventGameEntity addGame(EventGameEntity oneGame) {
    gameList.add(oneGame);
    return oneGame;
  }

  /**
   * Remove a game
   *
   * @param oneGame game to remove
   * @return true if the game exist and is removed, false else
   */
  public boolean removeGame(EventGameEntity oneGame) {
    for (EventGameEntity stepIterator : gameList) {
      if (stepIterator.getId().equals(oneGame.getId())) {
        gameList.remove(stepIterator);
        return true;
      }
    }
    return false;
  }

  /**
   * Add a TrueOrLie entity
   *
   * @param gameEntity       game where the truthOrLie must be added
   * @param truthOrLieEntity entity to add
   * @return the added entity
   */
  public EventGameTruthOrLieEntity addTruthOrLie(EventGameEntity gameEntity, EventGameTruthOrLieEntity truthOrLieEntity) {
    gameEntity.getTruthOrLieList().add(truthOrLieEntity);
    return truthOrLieEntity;
  }


  @Override
  public String toString() {
    return "Event{" + super.toString() + "}";
  }

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Chat */
  /*                                                                                  */
  /* ******************************************************************************** */

  public enum TypeEventEnum {
    OPEN, OPENCONF, LIMITED, SECRET
  }

  public enum StatusEventEnum {
    INPREPAR, INPROG, CLOSED, CANCELLED
  }

  public enum DatePolicyEnum {
    ONEDATE, PERIOD
  }

  public enum ScopeEnum {
    OPEN, OPENCONF, LIMITED, SECRET
  }

  /* ******************************************************************************** */
  /*                                                                                  */
  /* Serialization */
  /*                                                                                  */
  /* ******************************************************************************** */


  public enum SubscriptionEventEnum {
    FREE, PREMIUM, EXCELLENCE
  }

  public static class AdditionalInformationEvent {
    /**
     * Participants will be added with a String
     */
    public boolean withParticipantsAsString = false;
  }

}

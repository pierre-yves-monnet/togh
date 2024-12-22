/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventGameTruthOrLieEntity                                                       */
/*                                                                                  */
/* Manage one player. The entity contains                                           */
/*  - the list of sentences and the status / lie-Truth                              */
/*  - the list of votes by another player                                           */
/*                                                                                  */
/*  Event.gameList* => EventGameEntity                                              */
/*      EventGameEntity.typeGame == TRUTHORLIE                                      */
/*      EventGameEntity.truthOrLieList* => EventGameTruthOrLieEntity                */
/*  Contains:                                                                       */
/*    playerUser : which player                                                     */
/*    List<EventGameTruthOrLieSentenceEntity> sentencesList : sentences proposed    */
/*    validate : sentences validated                                                */
/*    List<EventGameTruthOrLieVoteEntity> voteList : player voted for               */
/*                                                                                  */
/* ******************************************************************************** */

@Entity


@Table(name = "EVTGAMETOL", uniqueConstraints = {@UniqueConstraint(columnNames = {"playerid", "gameid"})})

@EqualsAndHashCode(callSuper = true)
public @Data
class EventGameTruthOrLieEntity extends UserEntity {
  public static final String CST_SLABOPERATION_SENTENCE = "sentencelist";
  public static final String CST_SLABOPERATION_PLAYERVOTE = "playervote";


  // Secret Santa: a list of participants. Each participant must do a gift to the next in the line
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SELECT)
  @BatchSize(size = 100)
  @JoinColumn(name = "tolid")
  @OrderBy("id")
  private List<EventGameTruthOrLieSentenceEntity> sentencesList = new ArrayList<>();

  /*
   * Sentences are validated.
   */
  @Column(name = "validatesentences")
  private Boolean validateSentences;

  /*
   * Who proposed this list
   * User attached to this task (maybe an external user, why not ?)
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "playerid")
  private ToghUserEntity playerUser;


  // Vote of THIS player. There is on EventGameTruthOrLieVoteEntity another players
  // Example with 3 players: at the end, the list should have 2 votes here (players vote for the 2 another players)
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @Fetch(value = FetchMode.SELECT)
  @BatchSize(size = 100)
  @JoinColumn(name = "tolid")
  @OrderBy("id")
  private List<EventGameTruthOrLieVoteEntity> voteList = new ArrayList<>();


}


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
/*  EventGameTruthOrLieVoteEntity,                                                  */
/*                                                                                  */
/* One vote for a player and another player.                                        */
/* This entity is attached to a player (EventGameTruthOrLieEntity).
/* Other player is [otherPlayerId]                                                  */
/* then the result of the vote is the list of VoteOneSentenceEntity. If there is 3  */
/* sentence to vote, there is 3 item in the list                                    */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity


@Table(name = "EVTGAMETOLVOTE")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventGameTruthOrLieVoteEntity extends UserEntity {


    /*
     * this vote is for the sentence for another player sentence.
     * The player must have validated its proposition of course
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sentenceplayerid")
    private ToghUserEntity otherPlayer;

    /*
     * The vote is validated
     */
    @Column(name = "validatevote")
    private Boolean validateVote;


    // Vote of THIS player. There is on EventGameTruthOrLieVoteEntity another players
    // Example with 3 players: at the end, the list should have 2 votes here (players vote for the 2 another players)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "voteid")
    @OrderBy("id")
    private List<EventGameTruthOrLieVoteOneSentenceEntity> voteSentenceList = new ArrayList<>();

}

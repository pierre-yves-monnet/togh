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

import javax.persistence.*;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventGameTruthOrLieVoteOneSentenceEntity                                        */
/*                                                                                  */
/* One vote for one sentence. Used in EvtGameTruthOrLieVote                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity


@Table(name = "EVTGAMETOLVOTEONESENTENCE")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventGameTruthOrLieVoteOneSentenceEntity extends UserEntity {


    /*
     * this vote is for the sentence for another player sentence.
     * The player must have validated its proposition of course
     */
    @Column(name = "sentenceid")
    private Long sentenceId;

    /*
     * The choice: LIE or TRUTH?
     */
    @Column(name = "statusvote", length = 10)
    @Enumerated(EnumType.STRING)
    private EventGameTruthOrLieSentenceEntity.StatusSentenceEnum statusVote;

}

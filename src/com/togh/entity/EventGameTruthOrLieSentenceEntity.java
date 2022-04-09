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
/*  EventGameEntity,                                                                */
/*                                                                                  */
/* Manage one player. The entity contains                                           */
/*  - the list of sentences and the status / lie-Truth                              */
/* -  the list of votes by another player                                           */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity


@Table(name = "EVTGAMETOLSENTENCE")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventGameTruthOrLieSentenceEntity extends UserEntity {
    public static final String CST_SLABOPERATION_SENTENCE = "sentencelist";
    @Column(name = "statussentence", length = 10)
    @Enumerated(EnumType.STRING)
    StatusSentenceEnum statusSentence;
    // name is part of the baseEntity
    @Column(name = "sentence", length = 400)
    private String sentence;
    // True or lie: one entity per player
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "tolsentenceid")
    @OrderBy("id")
    private List<EventGameTruthOrLieVoteEntity> truthOrLieVoteList = new ArrayList<>();

    public enum StatusSentenceEnum {
        LIE, TRUTH
    }
}

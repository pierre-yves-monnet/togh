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
/*  Manage Game in an event. Different Games exists, this entity is the basic of all*/
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity


@Table(name = "EVTGAME")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventGameEntity extends UserEntity {

    public static final String CST_SLABOPERATION_GAMELIST = "gamelist";
    public static final String CST_SLABOPERATION_PLAYERLIST = "playersList";


    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private EventGameEntity.GameStatusEnum status = GameStatusEnum.INPREPAR;


    // name is part of the baseEntity
    @Column(name = "description", length = 400)
    private String description;

    @Column(name = "typegame", length = 15)
    @Enumerated(EnumType.STRING)
    private EventGameEntity.TypeGameEnum typeGame;

    // Secret Santas: a list of participant. Each participant must do a gift to the next in the line
    @ElementCollection // 1
    @CollectionTable(name = "evtgameplayers", joinColumns = @JoinColumn(name = "id")) // 2
    @Column(name = "participantid") // 3
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 1000)
    @OrderBy("id")
    private List<Long> playersList = new ArrayList<>();

    @Column(name = "adminshowlist")
    private Boolean adminShowList;

    // True or lie: one entity per player
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "gameid")
    @OrderBy("id")
    private List<EventGameTruthOrLieEntity> truthOrLieList = new ArrayList<>();

    // TruthOrLie parameters
    @Column(name = "nbsentences")
    private Long nbSentences;

    // on this sentences, the number of truth. If 0, then the number is undefined, and each player can decide the number.
    @Column(name = "nbtruthsrequested")
    private Long nbTruthsRequested;


    @Column(name = "openingofthevote", length = 15)
    @Enumerated(EnumType.STRING)
    private EventGameEntity.OpeningOfTheVoteEnum openingOfTheVote;

    @Column(name = "discoveryofresults", length = 15)
    @Enumerated(EnumType.STRING)
    private EventGameEntity.DiscoverResultEnum discoverResult;

    // ---------- Secret Santas + True Or Lie: who plays, ALL or only ACTIVE users (Users who say "I'm part of"
    @Column(name = "scopegame", length = 15)
    @Enumerated(EnumType.STRING)
    private EventGameEntity.ScopeGameEnum scopeGame;

    public enum OpeningOfTheVoteEnum {
        IMMEDIAT, BEFOREEVENT, STARTEVENT
    }

    public enum DiscoverResultEnum {
        IMMEDIAT, STARTEVENT, ENDEVENT
    }

    public enum GameStatusEnum {
        INPREPAR, OPEN, CLOSE
    }

    public enum TypeGameEnum {
        SECRETSANTAS, TRUTHORLIE
    }

    public enum ScopeGameEnum {
        ALL, ACTIVE
    }

}

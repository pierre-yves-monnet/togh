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
import java.util.ArrayList;
import java.util.List;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventGameEntity,                                                                */
/*                                                                                  */
/*  Manage Game in an event. Different Games exists, this entity saves all of them  */
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
    private List<Long> playersList = new ArrayList<>();

    @Column(name = "adminshowlist")
    private Boolean adminShowList;
    // ---------- Secret Santas
    @Column(name = "scopegame", length = 15)
    @Enumerated(EnumType.STRING)
    private EventGameEntity.ScopeGameEnum scopeGame;

    public enum GameStatusEnum {
        INPREPAR, OPEN, CLOSE
    }

    public enum TypeGameEnum {
        SECRETSANTAS
    }

    public enum ScopeGameEnum {
        ALL, ACTIVE
    }

}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project                                                                     */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service.event;

import com.togh.engine.logevent.LogEvent;
import com.togh.entity.EventEntity;
import com.togh.entity.EventGameEntity;
import com.togh.entity.ParticipantEntity;

import java.util.*;
import java.util.stream.Collectors;

/* ******************************************************************************** */
/*                                                                                  */
/* EventGameParticipantController                                                   */
/*                                                                                  */
/* Action on game. This class does not implement the EventAbsChildController,       */
/* because EventGameController do it, and this class in only a subset of this       */
/* class                                                                            */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventGameParticipantController {

    private final EventController eventController;
    private final EventEntity eventEntity;
    private final EventGameEntity gameEntity;

    protected EventGameParticipantController(EventController eventController, EventEntity eventEntity, EventGameEntity gameEntity) {
        this.eventController = eventController;
        this.eventEntity = eventEntity;
        this.gameEntity = gameEntity;
    }

    /**
     * Return the list of player, according the scope of the game
     *
     * @return the list of players in the scope of the game
     */
    public List<ParticipantEntity> getListPlayersInScope() {
        List<ParticipantEntity> listParticipantsEntity = eventEntity.getParticipantList();
        if (EventGameEntity.ScopeGameEnum.ALL.equals(gameEntity.getScopeGame())) {
            return listParticipantsEntity;
        } else if (EventGameEntity.ScopeGameEnum.ACTIVE.equals(gameEntity.getScopeGame())) {
            return listParticipantsEntity.stream().filter(ParticipantEntity::getIsPartOf).collect(Collectors.toList());
        }
        // return all participants
        return listParticipantsEntity;
    }

    //

    /**
     * resynchronize the list of players according the new participants / Players and the scope
     *
     * @param reset if true, then the complete list is reset, and a new draw is made
     */
    public List<LogEvent> synchronizePlayersWithParticipant(boolean reset) {
        List<LogEvent> listLogEvents = new ArrayList<>();
        List<ParticipantEntity> listPotentialPlayers = getListPlayersInScope();
        List<ParticipantEntity> listAllParticipantsEntity = eventEntity.getParticipantList();
        // listPlayer is a list of ParticipantId
        List<Long> listPlayers = gameEntity.getPlayersList();

        // reset : clean all
        if (reset)
            listPlayers.clear();

        Map<Long, ParticipantEntity> mapPlayerParticipant = new HashMap<>();
        for (Long participantId : listPlayers) {
            List<ParticipantEntity> findParticipant = listAllParticipantsEntity
                    .stream()
                    .filter(participant -> participant.getId().equals(participantId))
                    .collect(Collectors.toList());
            mapPlayerParticipant.put(participantId, !findParticipant.isEmpty() ? findParticipant.get(0) : null);
        }


        // first pass, remove all players which are no more a player
        int i = 0;
        while (i < listPlayers.size()) {
            ParticipantEntity playerParticipant = mapPlayerParticipant.get(listPlayers.get(i));
            if (listPotentialPlayers.contains(playerParticipant)) {
                i++; // keep this player
            } else
                listPlayers.remove(i);
        }
        // second pass, detect all new players
        List<Long> newPlayers = listPotentialPlayers.stream()
                .map(ParticipantEntity::getId)
                .filter(id -> !mapPlayerParticipant.containsKey(id))
                .collect(Collectors.toList());
        // if we have a list, please random it
        Collections.shuffle(newPlayers);
        // add the list at the end
        listPlayers.addAll(newPlayers);


        // update now the list
        gameEntity.setPlayersList(listPlayers);
        return listLogEvents;
    }
}

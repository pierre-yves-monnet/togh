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
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService;
import com.togh.service.SubscriptionService;

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

public class EventGameParticipantController extends EventAbsChildController {

  protected final EventController eventController;
  protected final EventEntity eventEntity;
  protected final EventGameEntity gameEntity;

  protected EventGameParticipantController(EventController eventController, EventEntity eventEntity, EventGameEntity gameEntity) {
    super(eventController, eventEntity);
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
      return listParticipantsEntity.stream()
          .filter(participant -> ParticipantEntity.PartOfEnum.PARTOF.equals(participant.getPartOf()))
          .collect(Collectors.toList());
    }
    // return all participants
    return listParticipantsEntity;
  }

  //completeConsistant
  public List<LogEvent> completeConsistant() {
    return synchronizePlayersWithParticipant(false);
  }

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


  /**
   * Synchronize the sentenceEntity with the number of sentences required
   *
   * @param playerId player to synchronize
   * @return list of events given the status of the synchronization
   */
  public List<LogEvent> synchronizeTruthOrLie(Long playerId) {
    List<LogEvent> listEvents = new ArrayList<>();
    listEvents.addAll(synchronizePlayersWithParticipant(false));
    listEvents.addAll(completeConsistant());
    return listEvents;
  }


  /**
   * Is this part of the event is at the limit, according the subscription?
   *
   * @param updateContext
   * @return true is the controller is as the limit
   */
  @Override
  public boolean isAtLimit(EventService.UpdateContext updateContext) {
    return false;
  }

  /**
   * Create a new ChildEntity. Object is created, not saved in the database.
   *
   * @param updateContext        Information on update
   * @param slabOperation        SlabOperation to perform
   * @param eventOperationResult operationResult updated
   * @return List of entity to create
   */
  @Override
  public EventEntityPlan createEntity(EventService.UpdateContext updateContext, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
  }

  /**
   * add the entity in the database
   *
   * @param childEntity          Entity to save
   * @param slabOperation        SlabOperation to perform
   * @param eventOperationResult operationResult updated
   * @return the baseEntity added, which may be modified (persistenceid is updated)
   */
  @Override
  public BaseEntity addEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
  }

  /**
   * Database may return a constraint error, because 2 threads try to do the same operation at the same time.
   * So, the server has to deal with that. One solution is to retrieve the current record saved in the database, and return it
   *
   * @param childEntity          child Entity to insert
   * @param slabOperation        slab operation in progress
   * @param eventOperationResult eventOperationResult
   * @return the correct entity, which may be the existing entity in the database
   */
  @Override
  public BaseEntity manageConstraint(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
  }

  /**
   * Get the entity by it id
   *
   * @param entityId the entityId
   * @return the BaseEntity
   */
  @Override
  public BaseEntity getEntity(long entityId) {
    return null;
  }

  /**
   * Save the entity transported by the controller
   *
   * @param childEntity
   * @param slabOperation        SlabOperation to perform
   * @param eventOperationResult LogEvent may be updated in case of error
   * @return the baseEntiy, which may be has modified (persistenceid is updated)
   */
  @Override
  public BaseEntity updateEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
    return null;
  }

  /**
   * Remove the given entity
   *
   * @param childEntity          to remove
   * @param eventOperationResult LogEvent may be updated in case of error
   * @return
   */
  @Override
  public void removeEntity(BaseEntity childEntity, EventService.EventOperationResult eventOperationResult) {
    // Nothing to manage here, not possible to remove a participant
  }

  /**
   * The controller return the type limit acceptable.
   * By default, the maxEntity is not knoz, and had to be calculated outside.
   * So, before any save call, the setLimitNumber has to be called
   *
   * @return
   */
  @Override
  public SubscriptionService.LimitReach getLimitReach() {
    return null;
  }
}

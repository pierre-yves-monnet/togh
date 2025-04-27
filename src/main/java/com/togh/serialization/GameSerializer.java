/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventGameEntity;
import com.togh.entity.EventGameTruthOrLieEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.service.event.EventGameController;
import com.togh.service.event.EventGameParticipantController;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameSerializer extends BaseSerializer {

  public static final String JSON_SECRETSANTA_ADMIN_SHOW_LIST = "adminShowList";
  public static final String JSON_SECRETSANTA_PARTICIPANT_GIFTED_NAME = "giftedName";
  public static final String JSON_SECRETSANTA_PARTICIPANT_GIFTED_ID = "giftedId";
  public static final String JSON_SECRETSANTA_PARTICIPANT_GIFTED_FIRSTNAME = "giftedFirstName";
  public static final String JSON_SECRETSANTA_PARTICIPANT_GIFTED_LASTNAME = "giftedLastName";
  public static final String JSON_SECRETSANTA_PARTICIPANT_GIFTED_LABEL = "giftedLabel";
  public static final String JSON_USER_LABEL = "userLabel";
  public static final String JSON_USER_FIRST_NAME = "userFirstName";
  public static final String JSON_USER_LAST_NAME = "userLastName";
  public static final String JSON_PARTICIPANT_ID = "participantId";
  public static final String JSON_SECRETSANTA_GIFT_TO_PLAYER_LABEL = "giftToPlayerLabel";
  public static final String JSON_STATUS = "status";
  public static final String JSON_DESCRIPTION = "description";
  public static final String JSON_TYPE_GAME = "typeGame";
  public static final String JSON_SCOPE = "scopeGame";
  public static final String JSON_NUMBER_OF_PARTICIPANTS_IN_THE_SCOPE = "numberOfParticipantsInTheScope";
  public static final String JSON_NUMBER_OF_PLAYERS = "numberOfPlayers";
  public static final String JSON_TRUTHORLIE_NB_SENTENCES = "nbSentences";
  public static final String JSON_TRUTHORLIE_NB_TRUTHS_REQUESTED = "nbTruthsRequested";
  public static final String JSON_TRUTHORLIE_DISCOVER_RESULT = "discoverResult";
  public static final String JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_SENTENCES = "numberOfPlayersWhoSentences";
  public static final String JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_SENTENCES_PERCENT = "numberOfPlayersWhoSentencesPercent";
  public static final String JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_SENTENCES_DATA = "numberOfPlayerWhoSentencesData";
  public static final String JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_VOTED = "numberOfPlayersWhoVoted";
  public static final String JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_VOTED_PERCENT = "numberOfPlayersWhoVotedPercent";
  public static final String JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_VOTED_DATA = "numberOfPlayerWhoVotedData";

  public static final String JSON_VALIDATE = "validate";
  public static final String JSON_SENTENCES_LIST = "sentencesList";
  public static final String JSON_SENTENCE = "sentence";
  public static final String JSON_STATUS_SENTENCE = "statusSentence";
  public static final String JSON_VOTE_LIST = "voteList";
  public static final String JSON_PLAYER_ID = "playerId";
  public static final String JSON_PLAYER_VOTE = "playerVote";
  public static final String JSON_TRUTH_OR_LIE_LIST = "truthOrLieList";
  public static final String JSON_TRUTH_OR_LIE_RESULT = "result";

  /**
   * The serializer serialize an Entity Class. Return the entity
   *
   * @return the entity Class this serializer handle
   */
  @Override
  public Class<?> getEntityClass() {
    return EventGameEntity.class;
  }

  /**
   * GetMap - implement EntitySerialization
   *
   * @param baseEntity           Entity to serialize
   * @param parentEntity         Parent entity
   * @param serializerOptions    Serialization options
   * @param factorySerializer    factory to access all serializer
   * @param factoryUpdateGrantor factory to access Update Grantor
   * @return a serialisation map
   */
  @Override
  public Map<String, Object> getMap(BaseEntity baseEntity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
    EventGameEntity eventGameEntity = (EventGameEntity) baseEntity;
    Map<String, Object> resultMap = getBasicMap(eventGameEntity, serializerOptions);

    resultMap.put(JSON_STATUS, eventGameEntity.getStatus() == null ? EventGameEntity.GameStatusEnum.INPREPAR : eventGameEntity.getStatus().toString());
    resultMap.put(JSON_DESCRIPTION, eventGameEntity.getDescription() == null ? "" : eventGameEntity.getDescription());
    resultMap.put(JSON_TYPE_GAME, eventGameEntity.getTypeGame() == null ? EventGameEntity.TypeGameEnum.SECRETSANTAS : eventGameEntity.getTypeGame().toString());

    resultMap.put(JSON_SCOPE, eventGameEntity.getScopeGame() == null ? EventGameEntity.ScopeGameEnum.ALL : eventGameEntity.getScopeGame().toString());

    // statistics
    EventGameParticipantController playerController = serializerOptions.getEventController().getEventGameController().getEventParticipantController(eventGameEntity);
    if (playerController != null) {
      List<ParticipantEntity> listPotentialParticipant = playerController.getListPlayersInScope();
      resultMap.put(JSON_NUMBER_OF_PARTICIPANTS_IN_THE_SCOPE, listPotentialParticipant.size());
    }

    //------------------------ Secret Santa
    if (eventGameEntity.getTypeGame().equals(EventGameEntity.TypeGameEnum.SECRETSANTAS)) {
      completeMapSecretSantas(resultMap, eventGameEntity, serializerOptions, factorySerializer, factoryUpdateGrantor);
    }
    if (eventGameEntity.getTypeGame().equals(EventGameEntity.TypeGameEnum.TRUTHORLIE)) {
      completeMapTruthOrLie(resultMap, eventGameEntity, serializerOptions, factorySerializer, factoryUpdateGrantor);
    }

    return resultMap;
  }


  /**
   * Complete the map for Secret Santa
   *
   * @param resultMap            Map to complete
   * @param eventGameEntity      gameEntity
   * @param serializerOptions    Serialization options
   * @param factorySerializer    factory to access all serializer
   * @param factoryUpdateGrantor factory to access Update Grantor
   */
  private void completeMapSecretSantas(Map<String, Object> resultMap, EventGameEntity eventGameEntity,
                                       SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
    resultMap.put(JSON_SECRETSANTA_ADMIN_SHOW_LIST, eventGameEntity.getAdminShowList());

    ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromClass(ToghUserEntity.class);

    List<Map<String, Object>> listParticipantsMap = new ArrayList<>();
    List<Long> listPlayers = eventGameEntity.getPlayersList();

    if (listPlayers == null)
      listPlayers = new ArrayList<>();
    for (int i = 0; i < listPlayers.size(); i++) {
      Long participantId = listPlayers.get(i);
      Map<String, Object> recordParticipant = new HashMap<>();
      recordParticipant.put(JSON_PARTICIPANT_ID, participantId);
      ParticipantEntity participantEntity = serializerOptions.getEventController().getParticipantById(participantId);
      if (participantEntity != null && participantEntity.getUser() != null) {
        recordParticipant.put(JSON_USER_LABEL, toghUserSerializer.getUserLabel(participantEntity.getUser(), serializerOptions));
        recordParticipant.put(JSON_USER_FIRST_NAME, participantEntity.getUser().getFirstName());
        recordParticipant.put(JSON_USER_LAST_NAME, participantEntity.getUser().getLastName());
        int playerId = getGiftedPlayer(i, listPlayers);
        Long participantGifted = listPlayers.get(playerId);
        ParticipantEntity participantGiftedEntity = serializerOptions.getEventController().getParticipantById(participantGifted);
        if (participantGiftedEntity != null)
          recordParticipant.put(JSON_SECRETSANTA_GIFT_TO_PLAYER_LABEL, toghUserSerializer.getUserLabel(participantGiftedEntity.getUser(), serializerOptions));
      }
      listParticipantsMap.add(recordParticipant);
    }
    resultMap.put(EventGameEntity.CST_SLABOPERATION_PLAYERLIST, listParticipantsMap);
    resultMap.put(JSON_NUMBER_OF_PLAYERS, listPlayers.size());

    // get to whom I have to do a gift
    Long myParticipantGifted = null;
    for (int i = 0; i < listPlayers.size(); i++) {
      ParticipantEntity participantEntity = serializerOptions.getEventController().getParticipantById(listPlayers.get(i));

      if (participantEntity != null
          && participantEntity.getUser() != null
          && participantEntity.getUser().getId().equals(serializerOptions.getToghUser().getId())) {
        // get the participant : gift to the next one in the list
        myParticipantGifted = listPlayers.get(getGiftedPlayer(i, listPlayers));
      }
    }
    if (myParticipantGifted != null) {
      ParticipantEntity participantEntity = serializerOptions.getEventController().getParticipantById(myParticipantGifted);
      if (participantEntity != null) {
        resultMap.put(JSON_SECRETSANTA_PARTICIPANT_GIFTED_NAME, participantEntity.getName());
        resultMap.put(JSON_SECRETSANTA_PARTICIPANT_GIFTED_ID, myParticipantGifted);
        resultMap.put(JSON_SECRETSANTA_PARTICIPANT_GIFTED_FIRSTNAME, participantEntity.getUser().getFirstName());
        resultMap.put(JSON_SECRETSANTA_PARTICIPANT_GIFTED_LASTNAME, participantEntity.getUser().getLastName());
        resultMap.put(JSON_SECRETSANTA_PARTICIPANT_GIFTED_LABEL, toghUserSerializer.getUserLabel(participantEntity.getUser(), serializerOptions));
      }

    }
  }

  /**
   * Complete the map for ThruthOrLie
   *
   * @param resultMap            Map to complete
   * @param eventGameEntity      gameEntity
   * @param serializerOptions    Serialization options
   * @param factorySerializer    factory to access all serializer
   * @param factoryUpdateGrantor factory to access Update Grantor
   */
  private void completeMapTruthOrLie(Map<String, Object> resultMap,
                                     EventGameEntity eventGameEntity,
                                     SerializerOptions serializerOptions,
                                     FactorySerializer factorySerializer,
                                     FactoryUpdateGrantor factoryUpdateGrantor) {
    List<Long> listPlayers = eventGameEntity.getPlayersList();

    resultMap.put(JSON_NUMBER_OF_PLAYERS, listPlayers.size());


    //--------------------- Truth or lie
    resultMap.put(JSON_TRUTHORLIE_NB_SENTENCES, eventGameEntity.getNbSentences());
    resultMap.put(JSON_TRUTHORLIE_NB_TRUTHS_REQUESTED, eventGameEntity.getNbTruthsRequested());
    resultMap.put(JSON_TRUTHORLIE_DISCOVER_RESULT, eventGameEntity.getDiscoverResult());
    int countValidateSentence = 0;
    int countVote = 0;
    LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
    LocalDateTime oneDayBefore = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);
    LocalDateTime dateEvent = serializerOptions.getEventController().getEvent().getWhenTheEventStart();
    // return all sentences of all players
    // we send the value only if:
    // - user is the owner
    // - OR sentences are validated AND the vote can start
    List<Map<String, Object>> listTruthOrLie = new ArrayList<>();
    resultMap.put(JSON_TRUTH_OR_LIE_LIST, listTruthOrLie);
    if (eventGameEntity.getTruthOrLieList() != null) {
      for (EventGameTruthOrLieEntity truthOrLieEntity : eventGameEntity.getTruthOrLieList()) {
        if (Boolean.TRUE.equals(truthOrLieEntity.getValidateSentences()))
          countValidateSentence++;

        // check the vote
        boolean userVoteAll = truthOrLieEntity.getVoteList().stream()
            .filter(t -> {
              return Boolean.FALSE.equals(t.getValidateVote());
            })
            .count() == 0;
        if (userVoteAll)
          countVote++;

        BaseSerializer serializer = factorySerializer.getFromEntity(truthOrLieEntity);

        if (serializerOptions.getToghUser().getId().equals(truthOrLieEntity.getPlayerUser().getId()))
          listTruthOrLie.add(serializer.getMap(truthOrLieEntity, eventGameEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
      }
    }
    resultMap.put(JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_SENTENCES, countValidateSentence);
    if (listPlayers.size() > 0) {
      resultMap.put(JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_SENTENCES_PERCENT, (int) (100.0 * countValidateSentence / listPlayers.size()));
      resultMap.put(JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_SENTENCES_DATA,
          Map.of("sentence", countValidateSentence, "waiting", listPlayers.size() - countValidateSentence));
    }


    // vote:
    resultMap.put(JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_VOTED, countVote);
    if (listPlayers.size() > 0) {
      resultMap.put(JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_VOTED_PERCENT, (int) (100.0 * countVote / listPlayers.size()));
      resultMap.put(JSON_TRUTHORLIE_NUMBER_OF_PLAYERS_WHO_VOTED_DATA,
          Map.of("voted", countVote, "waiting", listPlayers.size() - countVote));
    }


    // Result : we ask the GameController to publish (or not) the result
    EventGameController eventGameController = serializerOptions.getEventController().getEventGameController();

    resultMap.put(JSON_TRUTH_OR_LIE_RESULT, eventGameController.getResult(eventGameEntity,
        serializerOptions.getToghUser(),
        serializerOptions,
        factorySerializer));

  }

  /**
   * for the player at range "range", give the range of the player to prepare the gift
   *
   * @param range range of the player
   * @return range of the player to prepare a gist
   */
  private int getGiftedPlayer(int range, List<Long> listPlayers) {
    if (range + 1 < listPlayers.size())
      return range + 1;
    else
      return 0;
  }
}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventGameTruthOrLieVoteOneSentenceEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/* ******************************************************************************** */
/*                                                                                  */
/* GameTruthOrLieVoteOneSentenceSerializer                                          */
/*                                                                                  */
/* Serialize one vote / one sentence                                               */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Component
public class GameTruthOrLieVoteOneSentenceSerializer extends BaseSerializer {
    public static final String JSON_PARTICIPANT_ID = "participantId";
    public static final String JSON_VALIDATE = "validate";
    public static final String JSON_SENTENCES_LIST = "sentencesList";
    public static final String JSON_VOTE_LIST = "voteList";
    public static final String JSON_VOTE_SENTENCES_LIST = "voteSentenceList";
    public static final String JSON_VOTE_ID = "id";
    public static final String JSON_VOTE_SENTENCE_ID = "sentenceId";
    public static final String JSON_SENTENCE = "sentence";
    public static final String JSON_STATUSVOTE = "statusVote";
    public static final String JSON_PLAYER_ID = "playerId";
    public static final String JSON_PLAYER_NAME = "playerName";

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventGameTruthOrLieVoteOneSentenceEntity.class;
    }

    /**
     * @param entity               entity to serialize
     * @param parentEntity         The parent of the entity, may be needed by the serializer. Null if the entity is the root.
     * @param serializerOptions    Options to serialize
     * @param factorySerializer    the factory has to be pass. Not possible to aAutowired it: we have a loop dependency else
     * @param factoryUpdateGrantor Factory to access the grantor
     * @return the map which contains the serialized object
     */
    @Override
    public Map<String, Object> getMap(BaseEntity entity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        EventGameTruthOrLieVoteOneSentenceEntity truthOrLieOneSentenceEntity = (EventGameTruthOrLieVoteOneSentenceEntity) entity;

        Map<String, Object> oneVoteMap = new HashMap<>();
        oneVoteMap.put(JSON_VOTE_ID, truthOrLieOneSentenceEntity.getId());
        oneVoteMap.put(JSON_VOTE_SENTENCE_ID, truthOrLieOneSentenceEntity.getSentenceId());

        oneVoteMap.put(JSON_STATUSVOTE, truthOrLieOneSentenceEntity.getStatusVote());
        return oneVoteMap;

    }

    /**
     * This method return the map, but with more information. Then, the complete sentence is set in the result, based on the sentenceId
     *
     * @param entity               entity to serialize
     * @param parentEntity         The parent of the entity, may be needed by the serializer. Null if the entity is the root.
     * @param allSentences         provide the list of all sentences, to enrich the result
     * @param serializerOptions    Options to serialize
     * @param factorySerializer    the factory has to be pass. Not possible to aAutowired it: we have a loop dependency else
     * @param factoryUpdateGrantor Factory to access the grantor
     * @return the map which contains the serialized object
     */
    public Map<String, Object> getMapRich(BaseEntity entity, BaseEntity parentEntity, Map<Long, String> allSentences, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        EventGameTruthOrLieVoteOneSentenceEntity truthOrLieOneSentenceEntity = (EventGameTruthOrLieVoteOneSentenceEntity) entity;
        Map<String, Object> oneVoteMap = getMap(entity, parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor);
        oneVoteMap.put(JSON_SENTENCE, allSentences.get(truthOrLieOneSentenceEntity.getSentenceId()));
        return oneVoteMap;
    }

}

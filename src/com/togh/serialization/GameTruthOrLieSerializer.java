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
import com.togh.entity.EventGameTruthOrLieSentenceEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/* ******************************************************************************** */
/*                                                                                  */
/* GameTruthOrLieSerializer                                                   */
/*                                                                                  */
/* one TruthOrLie game contains one TruthOrLie entity per player                    */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Component
public class GameTruthOrLieSerializer extends BaseSerializer {
    public static final String JSON_PARTICIPANT_ID = "participantId";
    public static final String JSON_VALIDATE = "validate";
    public static final String JSON_SENTENCES_LIST = "sentencesList";
    public static final String JSON_VOTE_LIST = "voteList";
    public static final String JSON_VOTE_SENTENCES_LIST = "voteSentenceList";
    public static final String JSON_VOTE_ID = "id";
    public static final String JSON_VOTE_SENTENCE_ID = "sentenceId";
    public static final String JSON_SENTENCE = "sentence";
    public static final String JSON_VOTE = "vote";
    public static final String JSON_PLAYER_ID = "playerId";
    public static final String JSON_PLAYER_NAME = "playerName";

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventGameTruthOrLieEntity.class;
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
    public Map<String, Object> getMap(BaseEntity entity, BaseEntity parentEntity,
                                      SerializerOptions serializerOptions,
                                      FactorySerializer factorySerializer,
                                      FactoryUpdateGrantor factoryUpdateGrantor) {
        EventGameTruthOrLieEntity truthOrLieEntity = (EventGameTruthOrLieEntity) entity;
        EventGameEntity eventGameEntity = (EventGameEntity) parentEntity;

        ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromClass(ToghUserEntity.class);

        Map<String, Object> record = getBasicMap(truthOrLieEntity, serializerOptions);

        record.put(JSON_PARTICIPANT_ID, truthOrLieEntity.getPlayerUser().getId());
        record.put(JSON_VALIDATE, getBoolean(truthOrLieEntity.getValidateSentences(), Boolean.FALSE));

        List<Map<String, Object>> listSentences = new ArrayList<>();
        record.put(JSON_SENTENCES_LIST, listSentences);
        for (EventGameTruthOrLieSentenceEntity sentenceEntity : truthOrLieEntity.getSentencesList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(sentenceEntity);

            listSentences.add(serializer.getMap(sentenceEntity, parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }

        // votation ?
        boolean joinTheVote = false;
        LocalDateTime currentDate = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime oneDayBefore = LocalDateTime.now(ZoneOffset.UTC).minusDays(1);
        LocalDateTime dateEvent = serializerOptions.getEventController().getEvent().getWhenTheEventStart();

        // only validate question can be join
        if (eventGameEntity != null) {
            if (EventGameEntity.OpeningOfTheVoteEnum.IMMEDIAT.equals(eventGameEntity.getOpeningOfTheVote()))
                joinTheVote = true;
            else if (EventGameEntity.OpeningOfTheVoteEnum.BEFOREEVENT.equals(eventGameEntity.getOpeningOfTheVote())) {
                joinTheVote = dateEvent != null && dateEvent.isBefore(oneDayBefore);
            } else // if (EventGameEntity.OpeningOfTheVoteEnum.STARTEVENT.equals(eventGameEntity.getOpeningOfTheVote())
            {
                joinTheVote = dateEvent != null && dateEvent.isBefore(currentDate);
            }
            if (joinTheVote) {
                // collect all sentences - ID is unique
                Map<Long, String> allSentences = new HashMap<>();
                for (EventGameTruthOrLieEntity truthOrLieEntityIterator : eventGameEntity.getTruthOrLieList()) {
                    for (EventGameTruthOrLieSentenceEntity sentenceIterator : truthOrLieEntityIterator.getSentencesList()) {
                        allSentences.put(sentenceIterator.getId(), sentenceIterator.getSentence());
                    }
                }

                // manage votation
                record.put(JSON_VOTE_LIST,
                        truthOrLieEntity.getVoteList()
                                .stream()
                                .map(votation -> {
                                    Map<String, Object> votationMap = getBasicMap(votation, serializerOptions);
                                    votationMap.put(JSON_PLAYER_ID, votation.getOtherPlayer().getId());
                                    votationMap.put(JSON_PLAYER_NAME, toghUserSerializer.getUserLabel(votation.getOtherPlayer(), serializerOptions));
                                    votationMap.put(JSON_VALIDATE, getBoolean(votation.getValidateVote(), Boolean.FALSE));

                                    votationMap.put(JSON_VOTE_SENTENCES_LIST,
                                            votation.getVoteSentenceList().stream().map(voteEntity -> {
                                                GameTruthOrLieVoteOneSentenceSerializer serializer = (GameTruthOrLieVoteOneSentenceSerializer) factorySerializer.getFromEntity(voteEntity);
                                                return serializer.getMapRich(voteEntity, votation, allSentences, serializerOptions, factorySerializer, factoryUpdateGrantor);
                                            }).collect(Collectors.toList())
                                    );
                                    return votationMap;
                                }).collect(Collectors.toList()));
            }
        }
        return record;
    }
}

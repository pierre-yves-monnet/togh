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
import com.togh.entity.*;
import com.togh.entity.base.BaseEntity;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.serialization.ToghUserSerializer;
import com.togh.service.EventService;
import com.togh.service.SubscriptionService;

import java.util.*;
import java.util.stream.Collectors;

public class EventGameController extends EventAbsChildController {


    protected EventGameController(EventController eventController, EventEntity eventEntity) {
        super(eventController, eventEntity);
    }

    @Override
    public boolean isAtLimit(EventService.UpdateContext updateContext) {
        return getEventEntity().getGameList().size() >= getMaxEntity();
    }

    @Override
    public EventEntityPlan createEntity(EventService.UpdateContext updateContext, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        EventGameEntity gameEntity = new EventGameEntity();
        // there is only one type as this moment: so set it by default
        gameEntity.setTypeGame(EventGameEntity.TypeGameEnum.SECRETSANTAS);
        gameEntity.setAdminShowList(false);
        return new EventEntityPlan(gameEntity);
    }

    @Override
    public BaseEntity getEntity(long entityId) {
        return getFactoryRepository().eventGameRepository.findById(entityId);
    }

    @Override
    public BaseEntity updateEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGameRepository.save((EventGameEntity) childEntity);
        return childEntity;
    }

    @Override
    public BaseEntity addEntity(BaseEntity childEntity, EventUpdate.Slab slabOperation, EventService.EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGameRepository.save((EventGameEntity) childEntity);
        getEventEntity().addGame((EventGameEntity) childEntity);
        getFactoryRepository().eventRepository.save(getEventEntity());
        return childEntity;
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

    @Override
    public void removeEntity(BaseEntity childEntity, EventService.EventOperationResult eventOperationResult) {
        getFactoryRepository().eventGameRepository.delete((EventGameEntity) childEntity);
        getEventEntity().removeGame((EventGameEntity) childEntity);
    }

    @Override
    public SubscriptionService.LimitReach getLimitReach() {
        return SubscriptionService.LimitReach.GAME;
    }

    /**
     * Some games are based on the participants list, giving for each participant a different participant (for a gift, to find him, to play with him...)
     *
     * @return the EventGameParticipantController
     */
    public EventGameParticipantController getEventParticipantController(EventGameEntity gameEntity) {
        if (gameEntity.getTypeGame() == EventGameEntity.TypeGameEnum.SECRETSANTAS)
            return new EventGameParticipantController(getEventController(), getEventEntity(), gameEntity);
        if (gameEntity.getTypeGame() == EventGameEntity.TypeGameEnum.TRUTHORLIE)
            return new EventGameTruthOrLieController(getEventController(), getEventEntity(), gameEntity);
        return null;
    }

    /**
     * Complete the consistency of the game
     *
     * @param gameEntity game entity
     * @return a list of Slab if any update has to be done. Some update can be performed directly in the gameEntity
     */
    public List<LogEvent> completeConsistant(EventGameEntity gameEntity) {
        EventGameParticipantController gameController = getEventParticipantController(gameEntity);
        return gameController.completeConsistant();
    }

    /**
     * Get the result of the vote
     *
     * @param gameEntity     gameEntity to collect the result
     * @param toghUserEntity user who want to have the result
     * @return the result on a map
     */
    public Map<String, Object> getResult(EventGameEntity gameEntity, ToghUserEntity toghUserEntity,
                                         SerializerOptions serializerOptions,
                                         FactorySerializer factorySerializer) {
        Map<String, Object> result = new HashMap<>();
        ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromClass(ToghUserEntity.class);

        boolean publish = this.getEventController().isOrganizer(toghUserEntity);
        // check the rule: do we publish the result?
        if (!publish) {
            switch (gameEntity.getDiscoverResult()) {
                case STARTEVENT:
                    publish = this.getEventController().getEvent().isEventStarted();
                    break;
                case ENDEVENT:
                    publish = this.getEventController().getEvent().isEventEnded();
                    break;
                case IMMEDIAT:
                default:
                    publish = true;
            }
        }
        if (!publish)
            return result;


        // preparation: build a dictionary for all sentences
        Map<Long, SentenceTransport> dictionarySentences = new HashMap<>();
        for (EventGameTruthOrLieEntity truthOrLieEntity : gameEntity.getTruthOrLieList()) {
            if (Boolean.TRUE.equals(truthOrLieEntity.getValidateSentences())) {
                for (EventGameTruthOrLieSentenceEntity sentence : truthOrLieEntity.getSentencesList())
                    dictionarySentences.put(sentence.getId(), new SentenceTransport(sentence, truthOrLieEntity));
            }
        }
        int numberOfValidateSentence = 0;
        List<Map<String, Object>> listResult = new ArrayList<>();
        for (EventGameTruthOrLieEntity truthOrLieEntity : gameEntity.getTruthOrLieList()) {
            if (Boolean.TRUE.equals(truthOrLieEntity.getValidateSentences())) {
                numberOfValidateSentence++;
            }
            Map<String, Object> onePlayer = new HashMap<>();

            onePlayer.put("name", toghUserSerializer.getUserLabel(truthOrLieEntity.getPlayerUser(), serializerOptions));
            onePlayer.put("validatesentences", truthOrLieEntity.getValidateSentences());

            List<Map<String, Object>> listVotesPlayer = new ArrayList<>();
            onePlayer.put("vote", listVotesPlayer);
            int numberOfVotes = 0;
            int totalNumberOfPoints = 0;
            for (EventGameTruthOrLieVoteEntity voteEntity : truthOrLieEntity.getVoteList()) {
                // vote is validated?
                if (!Boolean.TRUE.equals(voteEntity.getValidateVote()))
                    continue;
                numberOfVotes++;
                // check each sentences
                int numberOfPoints = 0;
                for (EventGameTruthOrLieVoteOneSentenceEntity voteSentence : voteEntity.getVoteSentenceList()) {
                    SentenceTransport sourceSentence = dictionarySentences.get(voteSentence.getSentenceId());
                    int pointsForTheSentence = 0;
                    if (sourceSentence.sentenceEntity.getStatusSentence().equals(voteSentence.getStatusVote()))
                        pointsForTheSentence = 10;
                    Map<String, Object> voteDetailsMap = new HashMap<>();
                    voteDetailsMap.put("sentence", sourceSentence.sentenceEntity.getSentence());
                    voteDetailsMap.put("statussentence", sourceSentence.sentenceEntity.getStatusSentence().toString());
                    voteDetailsMap.put("statusvote", voteSentence.getStatusVote().toString());
                    voteDetailsMap.put("pointsforthesentence", pointsForTheSentence);
                    voteDetailsMap.put("sourceplayer", toghUserSerializer.getUserLabel(sourceSentence.truthOrLieEntity.getPlayerUser(), serializerOptions));
                    numberOfPoints += pointsForTheSentence;
                    // we add the details only if the user is an organizer, or if it is its own vote
                    if (this.getEventController().isOrganizer(toghUserEntity)
                            || toghUserEntity.getId().equals(truthOrLieEntity.getPlayerUser().getId()))
                        listVotesPlayer.add(voteDetailsMap);
                }
                totalNumberOfPoints += numberOfPoints;
            }
            onePlayer.put("numberofvotes", numberOfVotes);
            onePlayer.put("totalvotes", truthOrLieEntity.getVoteList().size());
            onePlayer.put("points", totalNumberOfPoints);
            listResult.add(onePlayer);

        }
        listResult = listResult.stream()
                .sorted(Comparator.comparingInt(t -> -1 * ((Integer) t.get("points"))))
                .collect(Collectors.toList());
        //     .sorted(Collections.reverseOrder()) // Method on Stream<Integer>
        for (int i = 0; i < listResult.size(); i++) {
            listResult.get(i).put("range", (i + 1));
        }
        result.put("players", listResult);

        return result;
    }

    private static class SentenceTransport {
        EventGameTruthOrLieSentenceEntity sentenceEntity;
        EventGameTruthOrLieEntity truthOrLieEntity;

        public SentenceTransport(EventGameTruthOrLieSentenceEntity sentenceEntity,
                                 EventGameTruthOrLieEntity truthOrLieEntity) {
            this.sentenceEntity = sentenceEntity;
            this.truthOrLieEntity = truthOrLieEntity;
        }
    }
}
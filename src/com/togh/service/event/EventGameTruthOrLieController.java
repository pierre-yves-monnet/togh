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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/* ******************************************************************************** */
/*                                                                                  */
/* EventGameTruthOrLieController                                                   */
/*                                                                                  */
/* Specific control for the TruthOrLie                                                                            */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class EventGameTruthOrLieController extends EventGameParticipantController {

    protected EventGameTruthOrLieController(EventController eventController, EventEntity eventEntity, EventGameEntity gameEntity) {
        super(eventController, eventEntity, gameEntity);
    }

    /**
     * completeConsistant
     * Check the entity, and complete it. For example, when the player Bob validated its sentence, then we create for all another players (Franck, Victor) the Vote entity
     */
    @Override
    public List<LogEvent> completeConsistant() {
        List<LogEvent> listEvents = super.synchronizePlayersWithParticipant(false);

        List<ParticipantEntity> listPlayers = getListPlayersInScope();

        if (gameEntity.getOpeningOfTheVote() == null)
            gameEntity.setOpeningOfTheVote(EventGameEntity.OpeningOfTheVoteEnum.IMMEDIAT);
        if (gameEntity.getDiscoverResult() == null)
            gameEntity.setDiscoverResult(EventGameEntity.DiscoverResultEnum.STARTEVENT);

        // do we have a TrueOrLie created per user?
        long nbSentences = gameEntity.getNbSentences();
        for (ParticipantEntity playerEntity : listPlayers) {
            // search the TruthOrLie entity for the player
            EventGameTruthOrLieEntity eventGameTruthOrLieEntity = getEventGameTruthOrLie(playerEntity.getUser().getId());
            if (eventGameTruthOrLieEntity == null) {
                eventGameTruthOrLieEntity = new EventGameTruthOrLieEntity();
                eventGameTruthOrLieEntity.setPlayerUser(playerEntity.getUser());
                eventGameTruthOrLieEntity.setValidateSentences(Boolean.FALSE);
                eventEntity.addTruthOrLie(gameEntity, eventGameTruthOrLieEntity);
            }
            // check the number of sentences
            while (eventGameTruthOrLieEntity.getSentencesList().size() < nbSentences) {
                EventGameTruthOrLieSentenceEntity eventGameTruthOrLieSentenceEntity = new EventGameTruthOrLieSentenceEntity();
                getFactoryRepository().eventTruthOrLieSentenceRepository.save(eventGameTruthOrLieSentenceEntity);
                eventGameTruthOrLieEntity.getSentencesList().add(eventGameTruthOrLieSentenceEntity);
            }
            while (eventGameTruthOrLieEntity.getSentencesList().size() > nbSentences) {
                eventGameTruthOrLieEntity.getSentencesList().remove(eventGameTruthOrLieEntity.getSentencesList().size() - 1);
            }
        }


        // now create for each Sentence, the list of votes. Do that only when the sentences are validated
        for (EventGameTruthOrLieEntity eventGameTruthOrLieEntity : gameEntity.getTruthOrLieList()) {
            // Let's refresh the list of EventGameTruthOrLieVoteEntity for this player.
            // We check the another player: did they validate theirs sentence? If yes, then we can add immediately a new EventGameTruthOrLieVoteEntity.

            // preparation: list all existing EventGameTruthOrLieVoteEntity (we don't want to add twice a votation)
            Set<Long> existingVotationPlayers = eventGameTruthOrLieEntity.getVoteList()
                    .stream()
                    .map(EventGameTruthOrLieVoteEntity::getOtherPlayer)
                    .map(ToghUserEntity::getId)
                    .collect(Collectors.toSet());

            // now check another players
            for (EventGameTruthOrLieEntity anotherPlayerEntity : gameEntity.getTruthOrLieList()) {

                // another player who validated their vote
                if (Boolean.FALSE.equals(anotherPlayerEntity.getValidateSentences())
                        || anotherPlayerEntity.getPlayerUser().getId().equals(eventGameTruthOrLieEntity.getPlayerUser().getId()))
                    continue;
                // already exist
                if (existingVotationPlayers.contains(anotherPlayerEntity.getPlayerUser().getId()))
                    continue;
                // ok, add it now
                // The vote is not present, so add it now
                EventGameTruthOrLieVoteEntity voteEntity = new EventGameTruthOrLieVoteEntity();
                voteEntity.setOtherPlayer(anotherPlayerEntity.getPlayerUser());
                voteEntity.setValidateVote(false);
                List<EventGameTruthOrLieVoteOneSentenceEntity> voteList = voteEntity.getVoteSentenceList();
                if (voteList == null) {
                    voteList = new ArrayList<>();
                }
                Set<Long> voteBySentences = voteList.stream()
                        .map(EventGameTruthOrLieVoteOneSentenceEntity::getSentenceId)
                        .collect(Collectors.toSet());
                for (EventGameTruthOrLieSentenceEntity sentenceEntity : anotherPlayerEntity.getSentencesList()) {
                    if (!voteBySentences.contains(sentenceEntity.getId())) {
                        EventGameTruthOrLieVoteOneSentenceEntity voteOneSentenceEntity = new EventGameTruthOrLieVoteOneSentenceEntity();
                        voteOneSentenceEntity.setSentenceId(sentenceEntity.getId());
                        voteList.add(voteOneSentenceEntity);
                    }
                }
                voteEntity.setVoteSentenceList(voteList);


                eventGameTruthOrLieEntity.getVoteList().add(voteEntity);
            }

        }
        return listEvents;
    }

    /**
     * Return the GameTrueOrLie from the player. Null if the entity does not exist yet
     *
     * @param playerId playerId
     * @return the entity, null if not exist
     */
    public EventGameTruthOrLieEntity getEventGameTruthOrLie(Long playerId) {
        for (EventGameTruthOrLieEntity eventGameTruthOrLie : gameEntity.getTruthOrLieList()) {
            if (playerId.equals(eventGameTruthOrLie.getPlayerUser().getId()))
                return eventGameTruthOrLie;
        }
        return null;
    }
}

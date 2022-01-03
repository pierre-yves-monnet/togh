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
import com.togh.entity.ParticipantEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.service.event.EventGameParticipantController;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameSerializer extends BaseSerializer {

    public static final String JSON_ADMIN_SHOW_LIST = "adminShowList";
    public static final String JSON_PARTICIPANT_GIFTED_NAME = "giftedName";
    public static final String JSON_PARTICIPANT_GIFTED_ID = "giftedId";
    public static final String JSON_PARTICIPANT_GIFTED_FIRSTNAME = "giftedFirstName";
    public static final String JSON_PARTICIPANT_GIFTED_LASTNAME = "giftedLastName";
    public static final String JSON_PARTICIPANT_GIFTED_LABEL = "giftedLabel";
    public static final String JSON_USER_LABEL = "userLabel";
    public static final String JSON_USER_FIRST_NAME = "userFirstName";
    public static final String JSON_USER_LAST_NAME = "userLastName";
    public static final String JSON_PARTICIPANT_ID = "participantId";
    public static final String JSON_GIFT_TO_PLAYER_LABEL = "giftToPlayerLabel";
    public static final String JSON_STATUS = "status";
    public static final String JSON_DESCRIPTION = "description";
    public static final String JSON_TYPE_GAME = "typeGame";
    public static final String JSON_SCOPE = "scopeGame";
    public static final String JSON_NUMBER_OF_PARTICIPANTS_IN_THE_SCOPE = "numberOfParticipantsInTheScope";
    public static final String JSON_NUMBER_OF_PLAYERS = "numberOfPlayers";

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
        resultMap.put(JSON_ADMIN_SHOW_LIST, eventGameEntity.getAdminShowList());

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
                recordParticipant.put(JSON_USER_LABEL, participantEntity.getUser().getLabel());
                recordParticipant.put(JSON_USER_FIRST_NAME, participantEntity.getUser().getFirstName());
                recordParticipant.put(JSON_USER_LAST_NAME, participantEntity.getUser().getLastName());
                int playerId = getGiftedPlayer(i, listPlayers);
                Long participantGifted = listPlayers.get(playerId);
                ParticipantEntity participantGiftedEntity = serializerOptions.getEventController().getParticipantById(participantGifted);
                if (participantGiftedEntity != null)
                    recordParticipant.put(JSON_GIFT_TO_PLAYER_LABEL, participantGiftedEntity.getUser().getLabel());
            }
            listParticipantsMap.add(recordParticipant);
        }


        // Order the result ohn demand
    /*
        Collections.sort(listParticipantsMap, new Comparator<Map<String, Object>>()
        {
            public int compare(Map<String, Object> s1,
                               Map<String, Object> s2)
            {
                String label1 = (String) s1.get(JSON_USER_LABEL);
                String label2 = (String) s2.get(JSON_USER_LABEL);
                if (label1==null)
                    label1="";
                return label1.compareTo(label2);
            }
        });
*/

        resultMap.put(EventGameEntity.CST_SLABOPERATION_PLAYERLIST, listParticipantsMap);

        // statistics
        EventGameParticipantController playerController = serializerOptions.getEventController().getEventGameController().getEventPartipantController(eventGameEntity);
        List<ParticipantEntity> listPotentialParticipant = playerController.getListPlayersInScope();

        resultMap.put(JSON_NUMBER_OF_PARTICIPANTS_IN_THE_SCOPE, listPotentialParticipant.size());
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
                resultMap.put(JSON_PARTICIPANT_GIFTED_NAME, participantEntity.getName());
                resultMap.put(JSON_PARTICIPANT_GIFTED_ID, myParticipantGifted);
                resultMap.put(JSON_PARTICIPANT_GIFTED_FIRSTNAME, participantEntity.getUser().getFirstName());
                resultMap.put(JSON_PARTICIPANT_GIFTED_LASTNAME, participantEntity.getUser().getLastName());
                resultMap.put(JSON_PARTICIPANT_GIFTED_LABEL, participantEntity.getUser().getLabel());
            }

        }


        return resultMap;
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

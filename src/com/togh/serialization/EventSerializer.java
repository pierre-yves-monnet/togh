/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;


import com.togh.engine.tool.EngineTool;
import com.togh.entity.*;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.BaseUpdateGrantor;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class EventSerializer extends BaseSerializer {



    /* ******************************************************************************** */
    /*                                                                                  */
    /* Serialization */
    /*                                                                                  */
    /* ******************************************************************************** */

    private static final String CST_JSONOUT_PARTICIPANTS = "participants";
    public static final String JSON_ITINERARYSHOWMAP = "itineraryshowmap";
    public static final String JSON_GEOINSTRUCTIONS = "geoinstructions";
    private static final String JSON_STATUS_EVENT = "statusEvent";
    private static final String JSON_TYPE_EVENT = "typeEvent";
    public static final String JSON_DESCRIPTION = "description";
    private static final String CST_JSONOUT_NAME = "name";
    private static final String JSONOUT_DATE_POLICY = "datePolicy";
    private static final String CST_JSONOUT_LISTSYNTHETICPARTICIPANTS = "listParticipants";
    public static final String JSON_TASKLISTSHOWDATES = "tasklistshowdates";
    private static final String JSON_DATE_START_EVENT = "dateStartEvent";
    /**
     * All verbs used to produce the JSON Output information
     */
    private static final String JSON_DATE_EVENT = "dateEvent";
    /**
     * Time of the event
     */
    private static final String JSON_TIME_EVENT = "timeEvent";
    private static final String JSON_DATE_END_EVENT = "dateEndEvent";
    private static final String JSON_SUBSCRIPTION_EVENT = "subscriptionEvent";
    private static final String JSON_DURATION_EVENT = "durationEvent";

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class<?> getEntityClass() {
        return EventEntity.class;
    }

    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        EventEntity eventEntity = (EventEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(eventEntity, serializerOptions);

        resultMap.put(JSON_DATE_EVENT, EngineTool.dateToString(eventEntity.getDateEvent()));
        resultMap.put(JSON_TIME_EVENT, eventEntity.getTimeevent() == null ? "" : eventEntity.getTimeevent());
        resultMap.put(JSON_DURATION_EVENT, eventEntity.getDurationEvent() == null ? "" : eventEntity.getDurationEvent());
        resultMap.put(JSON_DATE_START_EVENT, EngineTool.dateToString(eventEntity.getDateStartEvent()));
        resultMap.put(JSON_DATE_END_EVENT, EngineTool.dateToString(eventEntity.getDateEndEvent()));
        resultMap.put(JSON_TYPE_EVENT, eventEntity.getTypeEvent() == null ? null : eventEntity.getTypeEvent().toString());
        resultMap.put(JSON_STATUS_EVENT, eventEntity.getStatusEvent() == null ? null : eventEntity.getStatusEvent().toString());
        resultMap.put(JSON_DESCRIPTION, eventEntity.getDescription());
        resultMap.put(JSON_SUBSCRIPTION_EVENT, eventEntity.getSubscriptionEvent() == null ? EventEntity.SubscriptionEventEnum.FREE.toString() : eventEntity.getSubscriptionEvent().toString());

        resultMap.put(JSON_TASKLISTSHOWDATES, eventEntity.getTaskListShowDates());

        resultMap.put(JSON_ITINERARYSHOWMAP, eventEntity.getItineraryShowMap());
        resultMap.put("itineraryshowdetails", eventEntity.getItineraryShowDetails());
        resultMap.put("itineraryshowexpenses", eventEntity.getItineraryShowExpenses());

        resultMap.put("shoppinglistshowdetails", eventEntity.getShoppingListShowDetails());
        resultMap.put("shoppinglistshowexpenses", eventEntity.getShoppinglistShowExpenses());

        resultMap.put("geoaddress", eventEntity.getGeoaddress());
        resultMap.put("geolng", eventEntity.getGeolng());
        resultMap.put("geolat", eventEntity.getGeolat());
        resultMap.put(JSON_GEOINSTRUCTIONS, eventEntity.getGeoinstructions());
        resultMap.put(JSONOUT_DATE_POLICY, eventEntity.getDatePolicy() == null ? null : eventEntity.getDatePolicy().toString());

        if (serializerOptions.getEventAccessGrantor().isOtherParticipantsVisible()) {
            List<Map<String, Object>> listParticipantsMap = new ArrayList<>();
            for (ParticipantEntity participantEntity : eventEntity.getParticipantList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(participantEntity);
                listParticipantsMap.add(serializer.getMap(participantEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
            }
            resultMap.put(CST_JSONOUT_PARTICIPANTS, listParticipantsMap);
        }
        // get tasks
        List<Map<String, Object>> listTasksMap = new ArrayList<>();
        for (EventTaskEntity taskEntity : eventEntity.getTaskList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(taskEntity);
            listTasksMap.add(serializer.getMap(taskEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(EventTaskEntity.CST_SLABOPERATION_TASKLIST, listTasksMap);

        // get Itinerary
        List<Map<String, Object>> listItineraryStepMap = new ArrayList<>();
        for (EventItineraryStepEntity itineraryStepEntity : eventEntity.getItineraryStepList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(itineraryStepEntity);
            listItineraryStepMap.add(serializer.getMap(itineraryStepEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(EventItineraryStepEntity.CST_SLABOPERATION_ITINERARYSTEPLIST, listItineraryStepMap);

        // get ShoppingList
        List<Map<String, Object>> listShoppingListMap = new ArrayList<>();
        for (EventShoppingListEntity shoppingListEntity : eventEntity.getShoppingList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(shoppingListEntity);
            listShoppingListMap.add(serializer.getMap(shoppingListEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(EventShoppingListEntity.CST_SLABOPERATION_SHOPPINGLIST, listShoppingListMap);

        // get SurveyList
        List<Map<String, Object>> listSurveyListMap = new ArrayList<>();
        for (EventSurveyEntity surveyEntity : eventEntity.getSurveyList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(surveyEntity);
            listSurveyListMap.add(serializer.getMap(surveyEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(EventSurveyEntity.CST_SLABOPERATION_SURVEYLIST, listSurveyListMap);

        // get GroupChatList
        List<Map<String, Object>> listGroupChat = new ArrayList<>();
        for (EventGroupChatEntity groupChatEntity : eventEntity.getGroupChatList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(groupChatEntity);

            listGroupChat.add(serializer.getMap(groupChatEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(EventGroupChatEntity.SLABOPERATION_GROUPCHATLIST, listGroupChat);

        // get games
        List<Map<String, Object>> listGames = new ArrayList<>();
        for (EventGameEntity gameEntity : eventEntity.getGameList()) {
            BaseSerializer serializer = factorySerializer.getFromEntity(gameEntity);

            listGames.add(serializer.getMap(gameEntity, eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        resultMap.put(EventGameEntity.CST_SLABOPERATION_GAMELIST, listGames);


        //event Preference may be null, it is fulfilled  by the consistent
        if (eventEntity.getPreferences() != null) {
            BaseSerializer serializerPreference = factorySerializer.getFromEntity(eventEntity.getPreferences());
            if (serializerPreference != null)
                resultMap.put(EventPreferencesEntity.CST_SLABOPERATION_PREFERENCES, serializerPreference.getMap(eventEntity.getPreferences(), eventEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        BaseUpdateGrantor baseUpdateGrantor = factoryUpdateGrantor.getFromEntity(eventEntity);
        if (baseUpdateGrantor != null)
            resultMap.put(JSON_READONLY_FIELDS, baseUpdateGrantor.getFieldsReadOnly(serializerOptions.getToghUser(), serializerOptions.getEventController()));

        resultMap.put(JSON_CONTROL_INFORMATION_EVENT, serializerOptions.getEventAccessGrantor().getControlInformation());
        return resultMap;
    }


    /**
     * Get only the header of event
     *
     * @param eventEntity                eventEntity
     * @param serializerOptions          Serializer option
     * @param additionalInformationEvent additional information
     * @param factorySerializer          to access the toghSerializer to get the user label
     * @return the header on event
     */
    public Map<String, Object> getHeaderMap(EventEntity eventEntity,
                                            SerializerOptions serializerOptions,
                                            EventEntity.AdditionalInformationEvent additionalInformationEvent,
                                            FactorySerializer factorySerializer) {
        Map<String, Object> resultMap = getBasicMap(eventEntity, serializerOptions);
        ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromClass(ToghUserEntity.class);

        resultMap.put(CST_JSONOUT_NAME, eventEntity.getName());
        resultMap.put(JSON_DATE_EVENT, EngineTool.dateToString(eventEntity.getDateEvent()));
        resultMap.put(JSON_DATE_START_EVENT, EngineTool.dateToString(eventEntity.getDateStartEvent()));
        resultMap.put(JSON_DATE_END_EVENT, EngineTool.dateToString(eventEntity.getDateEndEvent()));
        resultMap.put(JSONOUT_DATE_POLICY, eventEntity.getDatePolicy().toString());
        resultMap.put(JSON_TYPE_EVENT, eventEntity.getTypeEvent() == null ? null : eventEntity.getTypeEvent().toString());
        resultMap.put(JSON_STATUS_EVENT, eventEntity.getStatusEvent() == null ? null : eventEntity.getStatusEvent().toString());
        resultMap.put(JSON_SUBSCRIPTION_EVENT, eventEntity.getSubscriptionEvent().toString());
        if (additionalInformationEvent.withParticipantsAsString) {
            // create the list of participants
            String listParticipants = eventEntity.getParticipantList().stream()
                    .filter(t -> t.getUser() != null)
                    .map(t -> toghUserSerializer.getUserLabel(t.getUser(), serializerOptions))
                    .collect(Collectors.joining(", "));

            resultMap.put(CST_JSONOUT_LISTSYNTHETICPARTICIPANTS, listParticipants);
        }
        return resultMap;
    }

}

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
    private static final String CST_JSONOUT_STATUS_EVENT = "statusEvent";
    private static final String CST_JSONOUT_TYPE_EVENT = "typeEvent";
    private static final String CST_JSONOUT_DATE_POLICY = "datePolicy";
    private static final String CST_JSONOUT_DATE_END_EVENT = "dateEndEvent";
    private static final String CST_JSONOUT_DATE_START_EVENT = "dateStartEvent";
    private static final String CST_JSONOUT_NAME = "name";
    private static final String CST_JSONOUT_SUBSCRIPTION_EVENT = "subscriptionEvent";
    private static final String CST_JSONOUT_LISTSYNTHETICPARTICIPANTS = "listParticipants";


    /**
     * All verbe used to produce the JSON Output information
     */
    private static final String CST_JSONOUT_DATE_EVENT = "dateEvent";

    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return EventEntity.class;
    }

    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset, FactorySerializer factorySerializer) {
        EventEntity eventEntity = (EventEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(eventEntity, contextAccess, timezoneOffset);

        resultMap.put(CST_JSONOUT_DATE_EVENT, EngineTool.dateToString(eventEntity.getDateEvent()));
        resultMap.put(CST_JSONOUT_DATE_START_EVENT, EngineTool.dateToString(eventEntity.getDateStartEvent()));
        resultMap.put(CST_JSONOUT_DATE_END_EVENT, EngineTool.dateToString(eventEntity.getDateEndEvent()));
        resultMap.put(CST_JSONOUT_TYPE_EVENT, eventEntity.getTypeEvent() == null ? null : eventEntity.getTypeEvent().toString());
        resultMap.put(CST_JSONOUT_STATUS_EVENT, eventEntity.getStatusEvent() == null ? null : eventEntity.getStatusEvent().toString());
        resultMap.put("description", eventEntity.getDescription());
        resultMap.put(CST_JSONOUT_SUBSCRIPTION_EVENT, eventEntity.getSubscriptionEvent().toString());

        resultMap.put("tasklistshowdates", eventEntity.getTaskListShowDates());

        resultMap.put("itineraryshowmap", eventEntity.getItineraryShowMap());
        resultMap.put("itineraryshowdetails", eventEntity.getItineraryShowDetails());
        resultMap.put("itineraryshowexpenses", eventEntity.getItineraryShowExpenses());

        resultMap.put("shoppinglistshowdetails", eventEntity.getShoppingListShowDetails());
        resultMap.put("shoppinglistshowexpenses", eventEntity.getShoppinglistShowExpenses());

        if (contextAccess != ToghUserEntity.ContextAccess.PUBLICACCESS) {
            resultMap.put(CST_JSONOUT_DATE_POLICY, eventEntity.getDatePolicy() == null ? null : eventEntity.getDatePolicy().toString());
        }
        if (eventEntity.getTypeEvent() == EventEntity.TypeEventEnum.OPEN || contextAccess != ToghUserEntity.ContextAccess.PUBLICACCESS) {
            List<Map<String, Object>> listParticipantsMap = new ArrayList<>();
            for (ParticipantEntity participantEntity : eventEntity.getParticipantList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(participantEntity);
                listParticipantsMap.add(serializer.getMap(participantEntity, contextAccess, timezoneOffset, factorySerializer));
            }
            resultMap.put(CST_JSONOUT_PARTICIPANTS, listParticipantsMap);

            // get tasks
            List<Map<String, Object>> listTasksMap = new ArrayList<>();
            for (EventTaskEntity taskEntity : eventEntity.getTaskList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(taskEntity);
                listTasksMap.add(serializer.getMap(taskEntity, contextAccess, timezoneOffset, factorySerializer));
            }
            resultMap.put(EventTaskEntity.CST_SLABOPERATION_TASKLIST, listTasksMap);

            // get Itinerary
            List<Map<String, Object>> listItineraryStepMap = new ArrayList<>();
            for (EventItineraryStepEntity itineraryStepEntity : eventEntity.getItineraryStepList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(itineraryStepEntity);
                listItineraryStepMap.add(serializer.getMap(itineraryStepEntity, contextAccess, timezoneOffset, factorySerializer));
            }
            resultMap.put(EventItineraryStepEntity.CST_SLABOPERATION_ITINERARYSTEPLIST, listItineraryStepMap);

            // get ShoppingList
            List<Map<String, Object>> listShoppingListMap = new ArrayList<>();
            for (EventShoppingListEntity shoppingListEntity : eventEntity.getShoppingList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(shoppingListEntity);
                listShoppingListMap.add(serializer.getMap(shoppingListEntity, contextAccess, timezoneOffset, factorySerializer));
            }
            resultMap.put(EventShoppingListEntity.CST_SLABOPERATION_SHOPPINGLIST, listShoppingListMap);

            // get SurveyList
            List<Map<String, Object>> listSurveylistMap = new ArrayList<>();
            for (EventSurveyEntity surveyEntity : eventEntity.getSurveyList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(surveyEntity);
                listSurveylistMap.add(serializer.getMap(surveyEntity, contextAccess, timezoneOffset, factorySerializer));
            }
            resultMap.put(EventSurveyEntity.CST_SLABOPERATION_SURVEYLIST, listSurveylistMap);

            // get GroupChatList
            List<Map<String, Object>> listGroupChat = new ArrayList<>();
            for (EventGroupChatEntity groupChatEntity : eventEntity.getGroupChatList()) {
                BaseSerializer serializer = factorySerializer.getFromEntity(groupChatEntity);

                listShoppingListMap.add(serializer.getMap(groupChatEntity, contextAccess, timezoneOffset, factorySerializer));
            }
            resultMap.put(EventGroupChatEntity.CST_SLABOPERATION_GROUPCHATLIST, listGroupChat);
        }

        return resultMap;
    }


    public Map<String, Object> getHeaderMap(EventEntity eventEntity, ToghUserEntity.ContextAccess contextAccess, EventEntity.AdditionalInformationEvent additionalInformationEvent, Long timezoneOffset) {
        Map<String, Object> resultMap = getBasicMap(eventEntity, contextAccess, timezoneOffset);

        resultMap.put(CST_JSONOUT_NAME, eventEntity.getName());
        resultMap.put(CST_JSONOUT_DATE_EVENT, EngineTool.dateToString(eventEntity.getDateEvent()));
        resultMap.put(CST_JSONOUT_DATE_START_EVENT, EngineTool.dateToString(eventEntity.getDateStartEvent()));
        resultMap.put(CST_JSONOUT_DATE_END_EVENT, EngineTool.dateToString(eventEntity.getDateEndEvent()));
        resultMap.put(CST_JSONOUT_DATE_POLICY, eventEntity.getDatePolicy().toString());
        resultMap.put(CST_JSONOUT_TYPE_EVENT, eventEntity.getTypeEvent() == null ? null : eventEntity.getTypeEvent().toString());
        resultMap.put(CST_JSONOUT_STATUS_EVENT, eventEntity.getStatusEvent() == null ? null : eventEntity.getStatusEvent().toString());
        resultMap.put(CST_JSONOUT_SUBSCRIPTION_EVENT, eventEntity.getSubscriptionEvent().toString());
        if (additionalInformationEvent.withParticipantsAsString) {
            // create the list of participants
            String listParticipants = eventEntity.getParticipantList().stream()
                    .filter(t -> t.getUser() != null)
                    .map(t -> t.getUser().getLabel())
                    .collect(Collectors.joining(", "));

            resultMap.put(CST_JSONOUT_LISTSYNTHETICPARTICIPANTS, listParticipants);
        }
        return resultMap;
    }

}

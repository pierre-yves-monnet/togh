package com.togh.serialization;

import com.togh.entity.EventChatEntity;
import com.togh.entity.EventEntity;
import com.togh.entity.EventGroupChatEntity;
import com.togh.eventgrantor.access.EventAccessGrantor;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.service.event.EventController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class EventSerializerTest {

    @Autowired
    FactorySerializer factorySerializer;

    @Autowired
    private FactoryUpdateGrantor factoryUpdateGrantor;

    @Test
    void testEventSerializer() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName("Hello Word");
        eventEntity.setTypeEvent(EventEntity.TypeEventEnum.OPEN);
        BaseSerializer eventSerializer = factorySerializer.getFromEntity(eventEntity);

        EventController eventController = new EventController(eventEntity, null, null, null);
        EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, null, SerializerOptions.ContextAccess.EVENTACCESS);
        SerializerOptions serializerOptions = new SerializerOptions(null,
                eventController,
                0L,
                SerializerOptions.ContextAccess.EVENTACCESS,
                eventAccessGrantor);


        Map<String, Object> mapSerialized = eventSerializer.getMap(eventEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor);
        Assertions.assertEquals("Hello Word", mapSerialized.get(BaseSerializer.JSON_NAME));
    }

    @Test
    void testRelationSerializerEventEntity() {
        Assertions.assertEquals(EventSerializer.class, factorySerializer.getFromEntity(new EventEntity()).getClass());
    }

    @Test
    void testRelationSerializerGroupeChatEntity() {
        Assertions.assertEquals(GroupChatSerializer.class, factorySerializer.getFromEntity(new EventGroupChatEntity()).getClass());
    }

    @Test
    void testRelationSerializerChatEntity() {
        Assertions.assertEquals(ChatSerializer.class, factorySerializer.getFromEntity(new EventChatEntity()).getClass());
    }

    @Test
    void testGroupChatSerialiser() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName("Hello Word");
        eventEntity.setTypeEvent(EventEntity.TypeEventEnum.OPEN);


        EventGroupChatEntity groupChatEntity = new EventGroupChatEntity();
        groupChatEntity.setName("GENERAL");
        eventEntity.setTypeEvent(EventEntity.TypeEventEnum.OPEN);

        eventEntity.addGroupChat(groupChatEntity);

        EventChatEntity chatEntityHello = new EventChatEntity();
        chatEntityHello.setMessage("Hello");
        eventEntity.addChat(groupChatEntity, chatEntityHello, 10);

        EventChatEntity chatEntityWord = new EventChatEntity();
        chatEntityWord.setMessage("Word");
        eventEntity.addChat(groupChatEntity, chatEntityWord, 10);

        BaseSerializer eventSerializer = factorySerializer.getFromEntity(eventEntity);

        EventController eventController = new EventController(eventEntity, null, null, null);
        EventAccessGrantor eventAccessGrantor = EventAccessGrantor.getEventAccessGrantor(eventController, null, SerializerOptions.ContextAccess.EVENTACCESS);
        SerializerOptions serializerOptions = new SerializerOptions(null,
                eventController,
                0L,
                SerializerOptions.ContextAccess.EVENTACCESS,
                eventAccessGrantor);

        Map<String, Object> mapSerialized = eventSerializer.getMap(eventEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor);

        Assertions.assertEquals("Hello Word", mapSerialized.get(BaseSerializer.JSON_NAME));
        List<Map<String, Object>> listGroupChatSerialized = (List<Map<String, Object>>) mapSerialized.get(EventGroupChatEntity.SLABOPERATION_GROUPCHATLIST);
        Assertions.assertEquals(1, listGroupChatSerialized.size());

        Map<String, Object> MainGroupSerializedMap = listGroupChatSerialized.get(0);
        Assertions.assertEquals("GENERAL", MainGroupSerializedMap.get(BaseSerializer.JSON_NAME));

        List<Map<String, Object>> ListChatSerialized = (List<Map<String, Object>>) MainGroupSerializedMap.get(GroupChatSerializer.JSON_CHATLIST);
        Assertions.assertEquals(2, ListChatSerialized.size());

        Assertions.assertEquals("Hello", ListChatSerialized.get(0).get(ChatSerializer.JSON_OUT_MESSAGE));
        Assertions.assertEquals("Word", ListChatSerialized.get(1).get(ChatSerializer.JSON_OUT_MESSAGE));

    }

}

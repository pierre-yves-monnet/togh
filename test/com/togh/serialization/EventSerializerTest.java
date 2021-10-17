package com.togh.serialization;

import com.togh.entity.EventChatEntity;
import com.togh.entity.EventEntity;
import com.togh.entity.EventGroupChatEntity;
import com.togh.entity.ToghUserEntity;
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

    @Test
    void testEventSerialiser() {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setName("Hello Word");
        BaseSerializer eventSerializer = factorySerializer.getFromEntity(eventEntity);
        ToghUserEntity.ContextAccess contextAccess = ToghUserEntity.ContextAccess.PUBLICACCESS;
        Map<String, Object> mapSerialized = eventSerializer.getMap(eventEntity, contextAccess, 0L, factorySerializer);
        Assertions.assertEquals("Hello Word", mapSerialized.get(BaseSerializer.JSON_OUT_NAME));
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

        EventGroupChatEntity groupChatEntity = new EventGroupChatEntity();
        groupChatEntity.setName("GENERAL");
        eventEntity.addGroupChat(groupChatEntity);

        EventChatEntity chatEntityHello = new EventChatEntity();
        chatEntityHello.setMessage("Hello");
        eventEntity.addChat(groupChatEntity, chatEntityHello, 10);

        EventChatEntity chatEntityWord = new EventChatEntity();
        chatEntityWord.setMessage("Word");
        eventEntity.addChat(groupChatEntity, chatEntityWord, 10);

        eventEntity.setName("Hello Word");
        BaseSerializer eventSerializer = factorySerializer.getFromEntity(eventEntity);
        ToghUserEntity.ContextAccess contextAccess = ToghUserEntity.ContextAccess.FRIENDACCESS;

        Map<String, Object> mapSerialized = eventSerializer.getMap(eventEntity, contextAccess, 0L, factorySerializer);

        Assertions.assertEquals("Hello Word", mapSerialized.get(BaseSerializer.JSON_OUT_NAME));
        List<Map<String, Object>> listGroupChatSerialized = (List<Map<String, Object>>) mapSerialized.get(EventGroupChatEntity.CST_SLABOPERATION_GROUPCHATLIST);
        Assertions.assertEquals(1, listGroupChatSerialized.size());

        Map<String, Object> MainGroupSerializedMap = listGroupChatSerialized.get(0);
        Assertions.assertEquals("GENERAL", MainGroupSerializedMap.get(BaseSerializer.JSON_OUT_NAME));

        List<Map<String, Object>> ListChatSerialized = (List<Map<String, Object>>) MainGroupSerializedMap.get(GroupChatSerializer.JSON_CHATLIST);
        Assertions.assertEquals(2, ListChatSerialized.size());

        Assertions.assertEquals("Hello", ListChatSerialized.get(0).get(ChatSerializer.JSON_OUT_MESSAGE));
        Assertions.assertEquals("Word", ListChatSerialized.get(1).get(ChatSerializer.JSON_OUT_MESSAGE));

    }

}

package com.togh.serialization;

import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        Assertions.assertEquals("Hello Word", mapSerialized.get(BaseSerializer.CST_JSONOUT_NAME));
    }
}

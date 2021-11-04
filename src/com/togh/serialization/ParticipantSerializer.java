/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.EventEntity;
import com.togh.entity.ParticipantEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.repository.EventRepository;
import com.togh.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParticipantSerializer extends BaseSerializer {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private EventRepository eventRepository;


    /**
     * The serializer serialize an Entity Class. Return the entity
     *
     * @return the entity Class this serializer handle
     */
    @Override
    public Class getEntityClass() {
        return ParticipantEntity.class;
    }

    /**
     * GetMap - implement EntitySerialization
     *
     * @param baseEntity           Entity to serialize
     * @param serializerOptions    Serialization options
     * @param factorySerializer    factory to access all serializer
     * @param factoryUpdateGrantor factory to access Update Grantor
     * @return a serialisation map
     */
    @Override
    public Map<String, Object> getMap(BaseEntity baseEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
        ParticipantEntity participantEntity = (ParticipantEntity) baseEntity;
        Map<String, Object> resultMap = getBasicMap(participantEntity, serializerOptions);

        resultMap.put("role", participantEntity.getRole() == null ? null : participantEntity.getRole().toString());
        BaseSerializer userSerialize = factorySerializer.getFromEntity(participantEntity.getUser());
        resultMap.put("user", userSerialize.getMap(participantEntity.getUser(), serializerOptions, factorySerializer, factoryUpdateGrantor));
        resultMap.put("isPartOf", participantEntity.getIsPartOf());
        resultMap.put("numberOfParticipants", participantEntity.getNumberOfParticipants());
        resultMap.put("id", participantEntity.getId());
        resultMap.put("status", participantEntity.getStatus() == null ? null : participantEntity.getStatus().toString());
        EventEntity eventEntity = eventRepository.findByParticipant(participantEntity.getId());
        if (ParticipantEntity.StatusEnum.INVITED.equals(participantEntity.getStatus())) {
            String url = notifyService.getUrlInvitation(participantEntity.getUser(), eventEntity);
            resultMap.put("urlInvitation", url);
        }
        return resultMap;
    }
}

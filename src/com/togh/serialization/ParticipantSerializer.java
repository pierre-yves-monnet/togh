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
import com.togh.entity.ToghUserEntity;
import com.togh.entity.base.BaseEntity;
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
     * @param userEntity        userEntity
     * @param contextAccess     contextAccess to know what information has to be produce
     * @param timezoneOffset    timeoffset of the browser
     * @param factorySerializer
     * @return a serialisation map
     */
    public Map<String, Object> getMap(BaseEntity userEntity, ToghUserEntity.ContextAccess contextAccess, Long timezoneOffset, FactorySerializer factorySerializer) {
        ParticipantEntity participantEntity = (ParticipantEntity) userEntity;
        Map<String, Object> resultMap = getBasicMap(participantEntity, contextAccess, timezoneOffset);

        resultMap.put("role", participantEntity.getRole() == null ? null : participantEntity.getRole().toString());
        BaseSerializer userSerialize = factorySerializer.getFromEntity(participantEntity.getUser());
        resultMap.put("user", userSerialize.getMap(participantEntity.getUser(), contextAccess, timezoneOffset, factorySerializer));
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

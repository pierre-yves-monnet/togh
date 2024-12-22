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

  public static final String JSON_ROLE = "role";
  public static final String JSON_USER = "user";
  public static final String JSON_PART_OF = "partOf";
  public static final String JSON_NUMBER_OF_PARTICIPANTS = "numberOfParticipants";
  public static final String JSON_ID = "id";
  public static final String JSON_STATUS = "status";
  public static final String JSON_URL_INVITATION = "urlInvitation";
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
   * @param parentEntity         Parent of the Participant : this is the EventEntity
   * @param serializerOptions    Serialization options
   * @param factorySerializer    factory to access all serializer
   * @param factoryUpdateGrantor factory to access Update Grantor
   * @return a serialisation map
   */
  @Override
  public Map<String, Object> getMap(BaseEntity baseEntity, BaseEntity parentEntity, SerializerOptions serializerOptions, FactorySerializer factorySerializer, FactoryUpdateGrantor factoryUpdateGrantor) {
    ParticipantEntity participantEntity = (ParticipantEntity) baseEntity;
    Map<String, Object> resultMap = getBasicMap(participantEntity, serializerOptions);

    resultMap.put(JSON_ROLE, participantEntity.getRole() == null ? null : participantEntity.getRole().toString());
    BaseSerializer userSerialize = factorySerializer.getFromEntity(participantEntity.getUser());
    resultMap.put(JSON_USER, userSerialize.getMap(participantEntity.getUser(), parentEntity, serializerOptions, factorySerializer, factoryUpdateGrantor));
    resultMap.put(JSON_PART_OF, participantEntity.getPartOf() == null ? ParticipantEntity.PartOfEnum.DONTKNOW : participantEntity.getPartOf().toString());
    resultMap.put(JSON_NUMBER_OF_PARTICIPANTS, participantEntity.getNumberOfParticipants());
    resultMap.put(JSON_ID, participantEntity.getId());
    resultMap.put(JSON_STATUS, participantEntity.getStatus() == null ? null : participantEntity.getStatus().toString());
    EventEntity eventEntity = (EventEntity) parentEntity;
    if (ParticipantEntity.StatusEnum.INVITED.equals(participantEntity.getStatus())) {
      String url = notifyService.getUrlInvitation(participantEntity.getUser(), eventEntity);
      resultMap.put(JSON_URL_INVITATION, url);
    }
    return resultMap;
  }
}

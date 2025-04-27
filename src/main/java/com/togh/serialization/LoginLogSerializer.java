package com.togh.serialization;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.LoginLogEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoginLogSerializer extends BaseSerializer {

  /**
   * The serializer serialize an Entity Class. Return the entity
   *
   * @return the entity Class this serializer handle
   */
  @Override
  public Class getEntityClass() {
    return LoginLogEntity.class;
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
    LoginLogEntity loginLogEntity = (LoginLogEntity) baseEntity;
    Map<String, Object> resultMap = getBasicMap(loginLogEntity, serializerOptions);


    resultMap.put("statusConnection", loginLogEntity.getStatusConnection() == null ? null : loginLogEntity.getStatusConnection().toString());

    resultMap.put("email", loginLogEntity.getEmail() == null ? "" : loginLogEntity.getEmail());
    resultMap.put("ipAddress", loginLogEntity.getIpAddress() == null ? "" : loginLogEntity.getIpAddress());
    resultMap.put("googleId", loginLogEntity.getGoogleId() == null ? "" : loginLogEntity.getGoogleId());
    resultMap.put("numberOfTentatives", loginLogEntity.getNumberOfTentatives());
    resultMap.put("timeSlot", loginLogEntity.getTimeSlot());
    resultMap.put("connectionTime", EngineTool.dateToString(loginLogEntity.getDateCreation()));
    resultMap.put("connectionTimeZone", EngineTool.dateTimeToTimeString(loginLogEntity.getDateCreation(), serializerOptions.getTimezoneOffset()));
    resultMap.put("connectionTimeHumanZone", EngineTool.dateTimeToHumanString(loginLogEntity.getDateCreation(), serializerOptions.getTimezoneOffset()));

    return resultMap;
  }
}

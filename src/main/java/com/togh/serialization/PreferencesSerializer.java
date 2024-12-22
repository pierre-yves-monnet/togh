package com.togh.serialization;

import com.togh.entity.EventPreferencesEntity;
import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PreferencesSerializer extends BaseSerializer {
  /**
   * The serializer serialize an Entity Class. Return the entity
   *
   * @return the entity Class this serializer handle
   */
  @Override
  public Class getEntityClass() {
    return EventPreferencesEntity.class;
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
    EventPreferencesEntity preferencesEntity = (EventPreferencesEntity) baseEntity;
    Map<String, Object> resultMap = getBasicMap(preferencesEntity, serializerOptions);

    resultMap.put("currencyCode", preferencesEntity.getCurrencyCode());
    resultMap.put("accessChat", preferencesEntity.getAccessChat());
    resultMap.put("accessItinerary", preferencesEntity.getAccessItinerary());
    resultMap.put("accessTasks", preferencesEntity.getAccessTasks());
    resultMap.put("accessBring", preferencesEntity.getAccessBring());
    resultMap.put("accessSurveys", preferencesEntity.getAccessSurveys());
    resultMap.put("accessGames", preferencesEntity.getAccessGames());
    resultMap.put("accessLocalisation", preferencesEntity.getAccessLocalisation());
    resultMap.put("accessPhotos", preferencesEntity.getAccessPhotos());
    resultMap.put("accessExpenses", preferencesEntity.getAccessExpenses());
    resultMap.put("accessBudget", preferencesEntity.getAccessBudget());
    return resultMap;
  }
}


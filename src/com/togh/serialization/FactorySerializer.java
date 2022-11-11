/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.base.BaseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FactorySerializer {
  private static final Logger logger = Logger.getLogger(FactorySerializer.class.getName());
  private static final String LOG_HEADER = FactorySerializer.class.getSimpleName() + ": ";

  Map<Class, BaseSerializer> relations = new HashMap<>();

  /**
   * Thanks to Spring, the list of Serializer is calculated dynamically. Each serializer need to implement the interface and to be a Component
   *
   * @param serializers list of serializer detected.
   */
  FactorySerializer(List<BaseSerializer> serializers) {
    serializers.forEach(t -> relations.put(t.getEntityClass(), t));
  }

  /**
   * Get the serializer from the baseEntity
   *
   * @param baseEntity baseEntity search
   * @return the Serializer for this entity
   */
  public BaseSerializer getFromEntity(BaseEntity baseEntity) {
    BaseSerializer serializer = relations.get(baseEntity.getClass());
    if (serializer == null) {
      logger.severe(LOG_HEADER + "Unknown relation for [" + baseEntity.getClass().getName() + "] ");
      return null;
    }
    return serializer;
  }

  /**
   * Get the serializer from a entity class
   *
   * @param baseEntityClass class of the entity
   * @return the Serializer for this entity
   */
  public BaseSerializer getFromClass(Class<?> baseEntityClass) {
    BaseSerializer serializer = relations.get(baseEntityClass);
    if (serializer == null) {
      logger.severe(LOG_HEADER + "Unknown relation for [" + baseEntityClass.getName() + "] ");
      return null;
    }
    return serializer;
  }
}

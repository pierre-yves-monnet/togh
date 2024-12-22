/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.serialization;

import com.togh.entity.base.BaseEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;

import java.util.HashMap;
import java.util.Map;



/* ******************************************************************************** */
/*                                                                                  */
/*  EntitySerialization                                                                     */
/*                                                                                  */
/*  To serialize an Entity, the controller must implement this interface             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public abstract class BaseSerializer {
  static final String JSON_NAME = "name";
  static final String JSON_ID = "id";
  static final String JSON_AUTHORID = "authorid";

  static final String JSON_READONLY_FIELDS = "readOnlyFields";
  static final String JSON_CONTROL_INFORMATION_EVENT = "informationEvent";


  /**
   * The serializer serialize an Entity Class. Return the entity
   *
   * @return the entity Class this serializer handle
   */
  public abstract Class getEntityClass();

  /**
   * @param entity               entity to serialize
   * @param parentEntity         The parent of the entity, may be needed by the serializer. Null if the entity is the root.
   * @param serializerOptions    Options to serialize
   * @param factorySerializer    the factory has to be pass. Not possible to aAutowired it: we have a loop dependency else
   * @param factoryUpdateGrantor Factory to access the grantor
   * @return the map which contains the serialized object
   */
  public abstract Map<String, Object> getMap(BaseEntity entity,
                                             BaseEntity parentEntity, SerializerOptions serializerOptions,
                                             FactorySerializer factorySerializer,
                                             FactoryUpdateGrantor factoryUpdateGrantor);

  /**
   * getBaseMap. Each entity depend on UserEntity. So, this is the basic map
   *
   * @param baseEntity        Base entity to serialize
   * @param serializerOptions Serialize options
   * @return
   */
  protected Map<String, Object> getBasicMap(BaseEntity baseEntity, SerializerOptions serializerOptions) {
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put(JSON_ID, baseEntity.getId());
    resultMap.put(JSON_NAME, baseEntity.getName());
    return resultMap;
  }

  protected Boolean getBoolean(Boolean value, Boolean defaultValue) {
    return (value == null ? defaultValue : value);
  }

  protected String getEnumValue(Enum value, Enum defaultValue) {
    if (value == null)
      return (defaultValue == null ? null : defaultValue.toString());
    return value.toString();

  }
}

/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.eventgrantor.update;

import com.togh.entity.base.BaseEntity;
import com.togh.serialization.FactorySerializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/* ******************************************************************************** */
/*                                                                                  */
/*  FactoryPermission                                                               */
/*                                                                                  */
/*  One entity can define some specific permission. Example, a participant can't    */
/* change the type of event                                                         */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
@Service
public class FactoryUpdateGrantor {
  private static final Logger logger = Logger.getLogger(FactoryUpdateGrantor.class.getName());
  private static final String LOG_HEADER = FactorySerializer.class.getSimpleName() + ": ";

  Map<Class, BaseUpdateGrantor> relations = new HashMap<>();

  FactoryUpdateGrantor(List<BaseUpdateGrantor> grantors) {
    grantors.forEach(t -> relations.put(t.getEntityClass(), t));
  }

  /**
   * Get the serializer from the baseEntity
   *
   * @param baseEntity baseEntity search
   * @return the Serializer for this entity
   */
  public BaseUpdateGrantor getFromEntity(BaseEntity baseEntity) {
    BaseUpdateGrantor grantor = relations.get(baseEntity.getClass());
    // this is not an issue: all Entity may not have a specific grantor.
    return grantor;
  }
}


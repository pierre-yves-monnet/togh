/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.engine.tool;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.entity.base.BaseEntity;
import com.togh.service.EventService.LoadEntityResult;
import com.togh.service.EventService.UpdateContext;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/* ******************************************************************************** */
/*                                                                                  */
/* JpaTool, */
/*                                                                                  */
/* Group different tool to address JPA */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class JpaTool {

  private static final Logger logger = Logger.getLogger(JpaTool.class.getName());
  private static final String LOG_HEADER = JpaTool.class.getSimpleName() + ": ";


  private static final LogEvent eventInvalidUpdateOperation = new LogEvent(JpaTool.class.getName(), 1, Level.APPLICATIONERROR, "Invalid operation", "This operation failed", "Operation can't be done", "Check error");

  /**
   * update an entity
   *
   * @param baseEntity    Base entity to update
   * @param attributName  attribut to update
   * @param attributValue value to apply
   * @param updateContext context for this update
   * @return
   */
  @SuppressWarnings("unchecked")
  public static List<LogEvent> updateEntityOperation(BaseEntity baseEntity,
                                                     String attributName,
                                                     Object attributValue,
                                                     UpdateContext updateContext) {
    List<LogEvent> listEvents = new ArrayList<>();
    Object value = null;
    Method methodAttribut = searchMethodByName(baseEntity, attributName);
    if (methodAttribut == null) {
      listEvents.add(new LogEvent(eventInvalidUpdateOperation, attributName + " <="
          + (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue)));
      logger.severe(LOG_HEADER + " Invalid operation [" + attributName + "] on entity[" + baseEntity.getClass().getName() + "]");
      return listEvents;
    }
    String jpaAttributName = methodAttribut.getName().substring(3);
    // first letter is a lower case
    jpaAttributName = jpaAttributName.substring(0, 1).toLowerCase() + jpaAttributName.substring(1);
    try {
      if (attributValue != null) {
        @SuppressWarnings("rawtypes")
        Class returnType = methodAttribut.getReturnType();
        if (returnType.equals(Double.class)) {
          value = Double.valueOf(attributValue.toString());

        } else if (returnType.equals(BigDecimal.class)) {
          value = getBigDecimalFromString(attributValue.toString());

        } else if (returnType.equals(Boolean.class)) {
          value = getBooleanFromString(attributValue.toString());

        } else if (returnType.equals(Long.class)) {
          value = Long.valueOf(attributValue.toString());

        } else if (returnType.equals(Integer.class)) {
          value = Integer.valueOf(attributValue.toString());

        } else if (returnType.equals(LocalDateTime.class)) {
          value = EngineTool.stringToDateTime(attributValue.toString());

        } else if (returnType.equals(LocalDate.class)) {
          if (attributValue instanceof LocalDate) {
            value = attributValue;
          } else {
            long timezoneOffset = updateContext.getTimezoneOffset();
            if (baseEntity.isAbsoluteLocalDate(attributName))
              timezoneOffset = 0;
            value = EngineTool.stringToDate(attributValue.toString(), timezoneOffset);
          }

        } else if (returnType.equals(String.class)) {
          value = attributValue.toString();

        } else if (returnType.isEnum()) {
          value = Enum.valueOf(returnType, attributValue.toString());

        } else if (isClassBaseEntity(returnType)) {
          if (updateContext.getFactoryService().getEventService() != null) {
            LoadEntityResult loadResult = updateContext.getFactoryService().getEventService().loadEntity(returnType, Long.valueOf(attributValue.toString()));
            value = loadResult.entity;
            listEvents.addAll(loadResult.listLogEvents);
          }

        } else // ArrayList, String
          value = attributValue;

      }

      PropertyUtils.setSimpleProperty(baseEntity, jpaAttributName, value);
      baseEntity.touch();

    } catch (Exception e) {
      String errorMessage = String.format(LOG_HEADER + "Can't update [%s] Id[%d] Attribut[%s] Value[%s] : %s",
          baseEntity.getName(),
          baseEntity.getId(),
          jpaAttributName,
          (attributValue == null ? "null" : "(" + attributValue.getClass().getName() + ") " + attributValue),
          e.getMessage());
      logger.severe(errorMessage);

      listEvents.add(new LogEvent(eventInvalidUpdateOperation, e, errorMessage));
    }
    return listEvents;
  }


  /**
   * IsClassEntity
   */
  private static boolean isClassBaseEntity(Class<?> classToStudy) {
    while (classToStudy != null) {
      if (classToStudy.equals(BaseEntity.class))
        return true;
      classToStudy = classToStudy.getSuperclass();
    }
    return false;
  }

  /**
   * return a boolean value from a string
   *
   * @param valueSt
   * @return
   * @throws Exception
   */
  public static Boolean getBooleanFromString(String valueSt) throws Exception {
    if ("ON".equalsIgnoreCase(valueSt) || "TRUE".equalsIgnoreCase(valueSt))
      return Boolean.TRUE;
    return Boolean.FALSE;
  }


  /**
   * value may be a currency, like $33.344. So, remove all non numric expression.
   * french is 2 334,44 ===> One comma only
   *
   * @param valueSt
   * @return
   * @throws Exception
   */
  public static BigDecimal getBigDecimalFromString(String valueSt) throws Exception {
    // french is 2 334,44 ===> One comma only
    // americain is 2,334.44 ==> One comma and one point.
    // so we detect first the format
    int countComma = 0;
    int countDot = 0;
    for (int i = 0; i < valueSt.length(); i++) {
      if (valueSt.charAt(i) == ',')
        countComma++;
      if (valueSt.charAt(i) == '.')
        countDot++;
    }
    NumberFormat numberFormat = null;
    if ((countComma > 0 && countDot > 0) || countDot == 1) {
      // americain format, remove ,
      numberFormat = NumberFormat.getNumberInstance(Locale.US);
    } else {
      numberFormat = NumberFormat.getNumberInstance(Locale.FRANCE);
    }
    StringBuilder valueExpurged = new StringBuilder();
    for (int i = 0; i < valueSt.length(); i++) {
      if (valueSt.charAt(i) >= '0' && valueSt.charAt(i) <= '9')
        valueExpurged.append(valueSt.charAt(i));
      if (valueSt.charAt(i) == '.' || valueSt.charAt(i) == ',')
        valueExpurged.append(valueSt.charAt(i));
    }

    return new BigDecimal(numberFormat.parse(valueExpurged.toString()).toString());
  }


  public static Method searchMethodByName(BaseEntity baseEntity, String attributName) {
    String methodName = "get" + attributName;

    for (Method method : baseEntity.getClass().getMethods()) {
      if (method.getName().equalsIgnoreCase(methodName))
        return method;
    }
    return null;
  }
}

package com.togh.engine.logevent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* ******************************************************************************** */
/*                                                                                  */
/* LogEventFactory */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class LogEventFactory {

  /**
   * Only static method
   * private LogEventFactory() {
   * }
   * /**
   * is this list contains one error ? If yes, then we return true
   */
  public static boolean isError(final List<LogEvent> listEvents) {

    for (final LogEvent event : listEvents) {
      if (event.isError()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param listEvents
   * @return
   */
  public static String getHtml(final List<LogEvent> listEvents) {
    StringBuilder tableHtml = new StringBuilder();
    tableHtml.append("<table>");

    tableHtml.append(listEvents.stream()
        .map(event -> {
          return "<tr><td>" + event.getHtml() + "</td>" + "</tr>";
        })
        .collect(Collectors.joining("")));

    tableHtml.append("</table>");
    return tableHtml.toString();
  }

  /**
   * create the list in a synthetic way, as a list of events
   *
   * @param listEvents
   * @return
   */
  public static String getSyntheticHtml(final List<LogEvent> listEvents) {
    StringBuilder tableHtml = new StringBuilder();

    tableHtml.append("<table style=\"border:1px solid black;border-spacing: 10px 0px;border-collapse: separate;\">");
    tableHtml.append("<tr><td>Title</td><td>Level</td><td>Parameters</td></tr>");

    tableHtml.append(listEvents.stream()
        .map(event -> {
          return "<tr><td>" + event.getHtmlTitle() + "</td>"
              + "<td>" + event.getLevel().toString() + "</td>"
              + "<td>" + event.getParameters() + event.getExceptionDetails() + "</td>"
              + "</tr>";
        })
        .collect(Collectors.joining("")));

    tableHtml.append("</table>");
    return tableHtml.toString();
  }

  /**
   * return a list of event for a log
   *
   * @param listEvents
   * @return
   */
  public static String getSyntheticLog(final List<LogEvent> listEvents) {
    return listEvents.stream()
        .map(LogEvent::toString)
        .collect(Collectors.joining(" <~> "));
  }

  /**
   * return a synthetic log only for errors
   *
   * @param listEvents
   * @return
   */
  public static String getSyntheticErrorLog(final List<LogEvent> listEvents) {

    return listEvents.stream()
        .filter(LogEvent::isError)
        .map(LogEvent::toString)
        .collect(Collectors.joining(" <~> "));
  }

  /**
   * Json to listEvent
   *
   * @param listEventsJson list event in JSON
   * @eturn a list of LogEvent
   */
  public static List<LogEvent> getListEventsFromJson(List<Map<String, Serializable>> listEventsJson) {

    return listEventsJson.stream()
        .map(eventJson -> {
          return LogEvent.getInstanceFormJson(eventJson);
        })
        .collect(Collectors.toList());
  }

  /**
   * Event to Json
   *
   * @param listEvents listEvents to serialize
   * @return a list of serialized event
   */
  public static List<Map<String, Serializable>> getJson(List<LogEvent> listEvents) {
    return listEvents.stream()
        .map(event -> event.getJson(false))
        .collect(Collectors.toList());
  }

  /**
   * add the event in the list only if this event is a new one, in order to remove the duplication.
   * An event already exist if this is the same package/number/parameters (see BEvent.same() ).
   *
   * @param listEvents the list modified if needed
   * @param eventToAdd the new event to add
   */
  public static void addEventUniqueInList(final List<LogEvent> listEvents, final LogEvent eventToAdd) {
    if (listEvents == null) {
      return;
    }
    String idToCheck = eventToAdd.getSignature(false);
    boolean idExists = listEvents.stream()
        .map(LogEvent::getSignatureParameter)
        .anyMatch(idToCheck::equals);
    if (idExists)
      return;

    listEvents.add(eventToAdd);
  }

  /**
   * add a list of events in the list only if this event is a new one, in order to remove the duplication.
   * An event already exist if this is the same package/number/parameters (see BEvent.same() ).
   *
   * @param listEvents  list events
   * @param eventsToAdd the event to add
   */
  public static void addListEventsUniqueInList(final List<LogEvent> listEvents, final List<LogEvent> eventsToAdd) {
    for (final LogEvent event : eventsToAdd) {
      addEventUniqueInList(listEvents, event);
    }

  }

  /**
   * calculate from a list of event a UNIQUE list of event, keeping the same order.
   * An event already exist if this is the same package/number/parameters (see BEvent.same() ).
   *
   * @param listEvents list events
   * @return a list, where all events are unique
   */
  public static List<LogEvent> filterUnique(final List<LogEvent> listEvents) {

    return listEvents.stream()
        .filter(distinctByKey(event -> {
          return event.getSignature(true);
        }))
        .collect(Collectors.toList());
  }

  //Utility function
  private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
    Map<Object, Boolean> map = new ConcurrentHashMap<>();
    return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
  }
}

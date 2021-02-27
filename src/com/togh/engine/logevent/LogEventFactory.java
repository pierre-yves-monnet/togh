package com.togh.engine.logevent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* ******************************************************************************** */
/*                                                                                  */
/*  LogEventFactory                                                                 */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class LogEventFactory {

    
    /** Only static method
    private LogEventFactory() {        
    }
    /**
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
        for (final LogEvent event : listEvents) {
            tableHtml.append("<tr><td>" + event.getHtml() + "</td></tr>");
        }
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

        for (final LogEvent event : listEvents) {
            tableHtml.append("<tr><td>" + event.getHtmlTitle() + "</td>"
                    + "<td>" + event.getLevel().toString() + "</td>"
                    + "<td>" + event.getParameters() + event.getExceptionDetails() + "</td>"
                    + "</tr>");
        }
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
        StringBuilder tableLog = new StringBuilder();

        for (final LogEvent event : listEvents) {
            tableLog.append(event.toString() + " <~> ");
        }

        return tableLog.toString();
    }

    /**
     * return a synthetic log only for errors
     * 
     * @param listEvents
     * @return
     */
    public static String getSyntheticErrorLog(final List<LogEvent> listEvents) {
        StringBuilder tableLog = new StringBuilder();

        for (final LogEvent event : listEvents) {
            if (event.isError())
                tableLog.append(event.toString() + " <~> ");
        }
        return tableLog.toString();
    }

    /**
     * Json to listEvent
     */
    public static List<LogEvent> getListEventsFromJson(List<Map<String, Serializable>> listEventsJson) {
        List<LogEvent> listEvents = new ArrayList<>();
        for (Map<String, Serializable> eventJson : listEventsJson)
            listEvents.add(LogEvent.getInstanceFormJson(eventJson));
        return listEvents;
    }

    /**
     * Event to Json
     * 
     * @param listEvents
     * @return
     */
    public static List<Map<String, Serializable>> getJsonFromListEvents(List<LogEvent> listEvents) {
        List<Map<String, Serializable>> listEventsJson = new ArrayList<>();
        for (LogEvent event : listEvents)
            listEventsJson.add(event.getJson(false));
        return listEventsJson;
    }

    /**
     * add the event in the list only if this event is a new one, in order to remove the duplication.
     * An event already exist if this is the same package/number/parameters (see BEvent.same() ).
     *
     * @param listEvents the list modified if needed
     * @param event the new event to add
     */
    public static void addEventUniqueInList(final List<LogEvent> listEvents, final LogEvent eventToAdd) {
        if (listEvents == null) {
            return;
        }

        for (int i = 0; i < listEvents.size(); i++) {
            if (listEvents.get(i).isIdentical(eventToAdd)) {
                return;
            }
        }
        listEvents.add(eventToAdd);
    }

    /**
     * add a list of events in the list only if this event is a new one, in order to remove the duplication.
     * An event already exist if this is the same package/number/parameters (see BEvent.same() ).
     *
     * @param listEvents
     * @param eventsToAdd
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
     * @param listEvents
     * @return
     */
    public static List<LogEvent> filterUnique(final List<LogEvent> listEvents) {
        final List<LogEvent> listUnique = new ArrayList<LogEvent>();

        for (final LogEvent event : listEvents) {
            boolean alreadyExist = false;
            for (final LogEvent existingEvent : listUnique) {
                if (event.isIdentical(existingEvent)) {
                    alreadyExist = true;
                    break;
                }
            }
            if (!alreadyExist) {
                listUnique.add(event);
            }
        }
        return listUnique;
    }

}

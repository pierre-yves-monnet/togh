/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.engine.logevent;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/* ******************************************************************************** */
/*                                                                                  */
/*  LogEvent                                                                        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

public class LogEvent {

    private final LogEvent mReferenceEvent; // event reference

    public String mExceptionDetails;
    // all fields of Event
    private long mNumber;
    private Level mLevel;
    private String mPackageName;
    private String mTitle;
    /**
     * in case of error, the cause of the error
     */
    private String mCause;
    /**
     * in case of error, the consequence : is the started can't start ? some operation will not be possible ?
     */
    private String mConsequence;
    /**
     * in case of error, the action to do to fix it
     */
    private String mAction;
    private String mKey;
    private String mParameters; // optional parameters
    public LogEvent(final String packageName, final long number, final Level level, final String title,
                    final String cause, final String consequence,
                    final String action) {
        mNumber = number;
        mLevel = level;
        mPackageName = packageName.trim();
        mTitle = title;
        mCause = cause;
        mConsequence = consequence;
        mAction = action;
        mKey = packageName + "." + number;
        mReferenceEvent = null;
        mParameters = "";
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Constructor of Event */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    //----------------------------------------------------------------------------


    /**
     * constructor for normal event (info, success, debug)
     *
     * @param packageName
     * @param number
     * @param level
     * @param title
     */
    public LogEvent(final String packageName, final long number, final Level level, final String title,
                    final String cause) {
        mNumber = number;
        mLevel = level;
        mPackageName = packageName.trim();
        mTitle = title;
        mCause = cause;
        mAction = null;
        mKey = packageName + "." + number;
        mReferenceEvent = null;
        mParameters = "";
    }

    /**
     * this is the common constructor in usage of event. A main event is referenced, which give all explanations, and the event only capture some additionnal
     * parameters
     *
     * @param referenceEvent : the referentiel event, which contains the level, cause, explanation, action (if errors). Example, a event to explain that a file
     *                       can't be openned.
     * @param parameters     : to give more explanations to the event, the parameters carry all information to send to users (example, complete fileName)
     */
    public LogEvent(final LogEvent referenceEvent, final String parameters) {
        mReferenceEvent = referenceEvent;
        mParameters = parameters;
    }

    /**
     * Build an event from an Exception. The referentiel event contains all informations (explanation, cause, actions) and the exception is used to complete the
     * event. Any parameters are welcome
     * Default Constructor.
     *
     * @param referenceEvent: the referentiel event, which contains the level, cause, explanation, action (if errors). Example, a event to explain that a file
     *                        can't be openned.
     * @param e:              the exception, to collect more information
     * @param parameters:     to give more explanations to the event, the parameters carry all information to send to users (example, complete fileName)
     */
    public LogEvent(final LogEvent referenceEvent, final Exception e, final String parameters) {
        mReferenceEvent = referenceEvent;
        mParameters = parameters;
        // this is an error : keep the strack trace !
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        mExceptionDetails = sw + "<p>" + e.getMessage();

    }

    /**
     * create a Event Success with only a title.
     */
    public static LogEvent getInstanceShortSuccess(final String packageName, final long number, final String title) {
        return new LogEvent(packageName, number, Level.SUCCESS, title, null);
    }

    /**
     * create a Event Success with title and the cause, to give more explanation on the success.
     */
    public static LogEvent getInstanceSuccess(final String packageName, final long number, final String title, String cause) {
        return new LogEvent(packageName, number, Level.SUCCESS, title, cause);
    }

    /**
     * opposite function, assuming this is the previous function which generate the map.
     *
     * @param mapEvent
     * @return
     */
    public static LogEvent getInstanceFormJson(Map<String, Serializable> mapEvent) {

        String packageName = jsonToString(mapEvent.get("packageName"));
        long number = (Long) mapEvent.get("number");
        Level level = Level.valueOf((String) mapEvent.get("level"));
        String title = jsonToString(mapEvent.get("title"));
        String cause = jsonToString(mapEvent.get("cause"));
        String consequence = jsonToString(mapEvent.get("consequence"));
        String action = jsonToString(mapEvent.get("action"));
        String parameters = jsonToString(mapEvent.get("parameters"));

        LogEvent event = new LogEvent(packageName, number, level, title, cause, consequence, action);
        event.mParameters = parameters;
        return event;
    }

    private static String jsonToString(final Object source) {
        if (source == null) {
            return "";
        }
        return source.toString().replace("\\\"", "\"");
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Tools */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    public void addParameter(String parameter) {
        if (this.mParameters == null)
            this.mParameters = "";
        this.mParameters += parameter;
    }

    /**
     * is this event is consider to be an error ?
     *
     * @return
     */
    public boolean isError() {
        final Level level = getLevel();
        return (level == Level.APPLICATIONERROR || level == Level.CRITICAL || level == Level.ERROR);
    }

    public String getSignature(boolean withParameter) {
        return getPackageName() + "." + getNumber() + ":" + (withParameter ? getParameters() : "");
    }

    public String getSignatureParameter() {
        return getSignature(true);
    }

    public String getSignatureKey() {
        return getSignature(false);
    }

    /**
     * isSame
     * Compare the new event with this one. They are identical when the number / package / parameters are identical.
     */
    public boolean isIdentical(final LogEvent compareEvent) {
        return compareEvent.getSignatureParameter().equals(getSignatureParameter());
    }

    /**
     * sameEvent compare the number and the packagename, not the parameters.
     *
     * @param compareEvent
     * @return
     */
    public boolean isSameEvent(final LogEvent compareEvent) {
        return compareEvent.getSignatureKey().equals(getSignatureKey());
    }

    /**
     * log this event, in a reference way.
     * All information about the event are logged (package+number, level, title, cause, consequence, actions parameters)
     * No logger is given, then the logger used the package org.bonitasoft.log.event
     */
    public void log() {
        final Logger logger = Logger.getLogger(LogEvent.class.getName());
        log(logger);
    }

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Generators */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * log using the logger, so this is possible to configure the logger.properties to see or not this log.
     * All information about the event are logged (package+number, level, title, cause, consequence, actions parameters)
     *
     * @param logger to log on this logger
     */
    public void log(Logger logger) {

        final Level level = getLevel();

        String message = "Event[" + getPackageName() + "~" + getNumber() + "] *" + level.toString() + "* " + getTitle()
                + " [" + getParameters() + "] -Cause:"
                + getCause();
        if (getConsequence() != null) {
            message += " -Consequence:" + getConsequence();
        }
        if (getAction() != null) {
            message += " -Action:" + getAction();
        }
        if (mExceptionDetails != null) {
            message += " " + mExceptionDetails;
        }

        if (level == Level.DEBUG) {
            logger.info(message);
        } else if (level == Level.INFO || level == Level.SUCCESS) {
            logger.info(message);
        } else {
            logger.severe(message);
        }
    }

    /**
     * return a Map, which can be used to JSON the event or serialize it
     *
     * @return
     */
    public Map<String, Serializable> getJson(final boolean withHtml) {
        final Map<String, Serializable> json = new HashMap<>();
        json.put("number", getNumber());
        json.put("level", getLevel().toString());
        json.put("packageName", stringToJson(getPackageName()));
        json.put("title", stringToJson(getTitle()));
        json.put("cause", stringToJson(getCause()));
        json.put("action", stringToJson(getAction()));
        json.put("consequence", stringToJson(getConsequence()));
        json.put("key", getKey());
        json.put("parameters", stringToJson(getParameters()));
        json.put("eventClassName", getEventClassName());
        if (withHtml) {
            json.put("html", stringToJson(getHtml()));
        }
        return json;

    }

    /**
     * return a piece of HTML to display the event, using bootstrap classes
     *
     * @return
     */
    public String getHtml() {
        final StringBuilder htmlEvent = new StringBuilder();
        htmlEvent.append("<div style=\"border:1px solid black;padding-right: 20px;\">");

        htmlEvent.append(getHtmlTitle());
        if (getParameters() != null) {
            htmlEvent.append("<br><span style=\"margin-left:20px;\">" + getParameters() + "</span>");
            if (getExceptionDetails() != null) {
                htmlEvent.append("<br><span style=\"margin-left:20px;font-style: italic;font-size: 75%;\">"
                        + getExceptionDetails() + "</span>");
            }

        }
        htmlEvent.append("</div>");

        return htmlEvent.toString();

    }

    public String getHtmlTitle() {
        final StringBuilder htmlEvent = new StringBuilder();
        String titlePopover = getKey();
        StringBuilder contentPopover = new StringBuilder();
        if (getCause() != null && getCause().length() > 0)
            contentPopover.append("\nCause:" + getCause());
        if (getConsequence() != null && getConsequence().length() > 0)
            contentPopover.append("\nConsequence:" + getConsequence());
        if (getAction() != null && getAction().length() > 0)
            contentPopover.append("\nAction:" + getAction());


        // <button type="button" class="btn btn-lg btn-danger" data-bs-toggle="popover" title="Popover title" data-bs-content="And here's some amazing content. It's very engaging. Right?">Click to toggle popover</button>
        htmlEvent.append("<button type=\"button\" ");
        htmlEvent.append(" class=\"" + getEventClassName() + "\" ");
        htmlEvent.append(" data-bs-toggle=\"popover\" ");
        htmlEvent.append(" title=\"" + titlePopover + "\"");
        htmlEvent.append(" data-bs-content=\"" + contentPopover + "\" >");
        htmlEvent.append(getTitle() + "</button>");
        return htmlEvent.toString();
    }

    public String getEventClassName() {
        if (getLevel() == Level.CRITICAL || getLevel() == Level.ERROR) {
            return "badge bg-danger";
        } else if (getLevel() == Level.APPLICATIONERROR) {
            return "badge bg-warning text-dark";
        } else if (getLevel() == Level.SUCCESS) {
            return "badge bg-success";
        } else {
            return "badge bg-info text-dark";
        }
    }

    /**
     * this method is mainly for debugging
     */
    @Override
    public String toString() {
        // don't display the cause and the action, it's mainly for debugging
        return getPackageName() + ":" + getNumber() + " (" + getLevel().toString() + ") " + getTitle() + " "
                + getParameters() + " "
                + getExceptionDetails();
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* getter */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    public long getNumber() {
        return mReferenceEvent == null ? mNumber : mReferenceEvent.getNumber();
    }

    public Level getLevel() {
        return mReferenceEvent == null ? mLevel : mReferenceEvent.getLevel();
    }

    public String getPackageName() {
        return mReferenceEvent == null ? mPackageName : mReferenceEvent.getPackageName();
    }

    public String getTitle() {
        return mReferenceEvent == null ? mTitle : mReferenceEvent.getTitle();
    }

    public String getCause() {
        return mReferenceEvent == null ? mCause : mReferenceEvent.getCause();
    }

    public String getConsequence() {
        return mReferenceEvent == null ? mConsequence : mReferenceEvent.getConsequence();
    }

    public String getAction() {
        return mReferenceEvent == null ? mAction : mReferenceEvent.getAction();
    }

    public String getKey() {
        return mReferenceEvent == null ? mKey : mReferenceEvent.getKey();
    }

    public LogEvent getReferenceEvent() {
        return mReferenceEvent;
    }

    public String getParameters() {
        return mParameters != null ? mParameters : mReferenceEvent != null ? mReferenceEvent.getParameters() : null;
    }

    public String getExceptionDetails() {
        return mExceptionDetails == null ? "" : mExceptionDetails;
    }

    private String stringToJson(final String source) {
        if (source == null) {
            return "";
        }
        return source.replace("\"", "\\\"");
    }

    /**
     * DEBUG : for debug reason
     * INFO : in a sequence of operation, the INFO level is used to return information on the different step. For example, the method calculate an information,
     * it can be a INFO event
     * SUCCESS : in the sequence of operation, report each success with a SUCCESS level
     * MAININFO: the information is important, to get a synthesis level
     * APPLICATIONERROR : this is an error, but due to the external system. Example, the method receive an URL, but this URL is malformed : this is a
     * APPLICATIONERROR, the function can't work with this input
     * ERROR : an internal error. You catch a NullPointerException ? THe function should have an issue, and a ERROR should be reported.
     * CRITICAL : an internal error, but which are critical, and the system should stop.
     */
    public enum Level {
        DEBUG, INFO, MAININFO, SUCCESS, APPLICATIONERROR, ERROR, CRITICAL
    }
}

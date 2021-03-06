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

    /**
     * DEBUG : for debug reason
     * INFO : in a sequence of operation, the INFO level is use to return information on the different step. For example, the method calculate an information,
     * it can be a INFO event
     * SUCCESS : in the sequence of operation, report each success with a SUCCESS level
     * APPLICATIONERROR : this is an error, but due to the external system. Example, the method receive an URL, but this URL is malformed : this is a
     * APPLICATIONERROR, the function can't work with this input
     * ERROR : an internal error. You catch a NullPointerException ? THe function should have an issue, and a ERROR should be reported.
     * CRITICAL : an internal error, but which are critical, and the system should stop.
     */
    public enum Level {
        DEBUG, INFO, SUCCESS, APPLICATIONERROR, ERROR, CRITICAL
    };

    // all fields of Event
    private long mNumber;
    private Level mLevel;
    private String mPackageName;
    private String mTitle;
    /**
     * in case of error, the cause of the error
     */
    private String mCause;
    /** in case of error, the consequence : is the started can't start ? some operation will not be possible ? */
    private String mConsequence;

    /**
     * in case of error, the action to do to fix it
     */
    private String mAction;

    private String mKey;

    public String mExceptionDetails;
    private final LogEvent mReferenceEvent; // event reference
    private String mParameters; // optional parameters

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Constructor of Event */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    //----------------------------------------------------------------------------
  

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
     * this is the common constructor in usage of event. A main event is referenced, which give all explanations, and the event only capture some additionnal
     * parameters
     *
     * @param referenceEvent : the referentiel event, which contains the level, cause, explanation, action (if errors). Example, a event to explain that a file
     *        can't be openned.
     * @param parameters : to give more explanations to the event, the parameters carry all information to send to users (example, complete fileName)
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
     *        can't be openned.
     * @param e: the exception, to collect more information
     * @param parameters: to give more explanations to the event, the parameters carry all information to send to users (example, complete fileName)
     */
    public LogEvent(final LogEvent referenceEvent, final Exception e, final String parameters) {
        mReferenceEvent = referenceEvent;
        mParameters = parameters;
        // this is an error : keep the strack trace !
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        mExceptionDetails = sw.toString()+"<p>"+e.getMessage();
        
    }

    public void addParameter( String parameter ) {
        if (this.mParameters==null)
            this.mParameters="";
        this.mParameters += parameter;
    }
    /* ******************************************************************************** */
    /*                                                                                  */
    /* Tools */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */

    /**
     * is this event is consider to be an error ?
     *
     * @return
     */
    public boolean isError() {
        final Level level = getLevel();
        return (level == Level.APPLICATIONERROR || level == Level.CRITICAL || level == Level.ERROR);
    }

    /**
     * isSame
     * Compare the new event with this one. They are identical when the number / package / parameters are identical.
     */
    public boolean isIdentical(final LogEvent compareEvent) {
        return (compareEvent.getNumber() == getNumber()
                && compareEvent.getPackageName().equals(getPackageName())
                && compareEvent.getParameters().equals(getParameters()));
    }

    /**
     * sameEvent compare the number and the packagename, not the parameters.
     * 
     * @param compareEvent
     * @return
     */
    public boolean isSameEvent(final LogEvent compareEvent) {
        return (compareEvent.getNumber() == getNumber()
                && compareEvent.getPackageName().equals(getPackageName()));
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

    /* ******************************************************************************** */
    /*                                                                                  */
    /* Generators */
    /*                                                                                  */
    /*                                                                                  */
    /* ******************************************************************************** */
    /**
     * return a Map, which can be used to JSON the event or serialize it
     *
     * @return
     */
    public Map<String, Serializable> getJson(final boolean withHtml) {
        final Map<String, Serializable> json = new HashMap<String, Serializable>();
        json.put("number", getNumber());
        json.put("level", getLevel().toString());
        json.put("packageName", stringToJson(getPackageName()));
        json.put("title", stringToJson(getTitle()));
        json.put("cause", stringToJson(getCause()));
        json.put("action", stringToJson(getAction()));
        json.put("consequence", stringToJson(getConsequence()));
        json.put("key", getKey());
        json.put("parameters", stringToJson(getParameters()));
        if (withHtml) {
            json.put("html", stringToJson(getHtml()));
        }
        return json;

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
            htmlEvent.append("<br><span style=\"margin-left:30px;\">" + getParameters() + "</span>");
            if (getCause() != null) {
                htmlEvent.append("<br><span style=\"margin-left:30px;font-style: italic;font-size: 75%;\">Cause: "
                        + getCause() + "</span>");
            }
            if (getExceptionDetails() != null) {
                htmlEvent.append("<br><span style=\"margin-left:30px;font-style: italic;font-size: 75%;\">"
                        + getExceptionDetails() + "</span>");
            }

        }
        if (getConsequence() != null) {
            htmlEvent.append(
                    "<br><span style=\"margin-left:30px;font-style: italic;font-weight: bold; font-size: 75%;\">Consequence: "
                            + getConsequence()
                            + "</span>");
        }
        if (getAction() != null) {
            htmlEvent.append(
                    "<br><span style=\"margin-left:30px;font-style: italic;font-weight: bold; font-size: 75%;\">Action: "
                            + getAction() + "</span>");
        }
        htmlEvent.append("</div>");

        return htmlEvent.toString();

    }

    public String getHtmlTitle() {
        final StringBuilder htmlEvent = new StringBuilder();

        StringBuilder title = new StringBuilder();
        title.append(getKey());

        htmlEvent.append("<a href='#' ");
        if (getLevel() == Level.CRITICAL || getLevel() == Level.ERROR) {
            htmlEvent.append("class=\"label label-danger\" style=\"color:white;\" ");
            if (getConsequence() != null && getConsequence().length() > 0)
                title.append("\nConsequence:" + getConsequence());
            title.append("\nAction:" + getAction());
        } else if (getLevel() == Level.APPLICATIONERROR) {
            htmlEvent.append("class=\"label label-warning\" style=\"color:white;\" ");
            if (getConsequence() != null && getConsequence().length() > 0)
                title.append("\nConsequence:" + getConsequence());
            title.append("\nAction:" + getAction());
        } else if (getLevel() == Level.SUCCESS) {
            htmlEvent.append("class=\"label label-success\" style=\"color:white;\" ");
        } else {
            htmlEvent.append("class=\"label label-info\" style=\"color:white;\" ");
        }

        htmlEvent.append("\" title=\"" + title + "\"");
        htmlEvent.append(">" + getTitle());
        htmlEvent.append("</a>");
        return htmlEvent.toString();
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

    private static String jsonToString(final Object source) {
        if (source == null) {
            return "";
        }
        return source.toString().replace("\\\"", "\"");
    }
}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

/* ******************************************************************************** */
/*                                                                                  */
/* EventController, */
/*                                                                                  */
/* Control what's happen on an event. Pilot all operations */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
public class RestJsonConstants {

    public static final String CST_PARAM_FILTER_EVENTS = "filterEvents";
    public static final String CST_PARAM_NAME = "name";
    public static final String CST_LISTLOGEVENTS = "listLogEvents";
    public static final String CST_EVENTID = "eventId";
    public static final String CST_EVENT = "event";
    public static final String CST_LISTEVENTS = "events";
    public static final String CST_CHILDENTITY = "childEntity";
    public static final String CST_CHILDENTITYID = "childEntityId";
    /**
     * Status may have multiple value (in case of invitation for example), and 2 default value, OK and ERROR, are proposed
     */
    public static final String CST_STATUS = "status";
    public static final String CST_STATUS_V_OK = "OK";
    public static final String CST_STATUS_V_ERROR = "ERROR";
    public static final String CST_OKMESSAGE = "okMessage";
    public static final String CST_ERRORMESSAGE = "errorMessage";
    public static final String CST_ISINVITATIONSENT = "isInvitationSent";
    static final String CST_PARAM_AUTHORIZATION = "Authorization";
    static final String CST_INJSON_EVENTID = "eventid";
    static final String CST_NUMBERPERPAGE = "numberperpage";
    static final String CST_PAGE = "page";
    static final String CST_COUNTUSERS = "countusers";
    static final String CST_LISTUSERS = "users";
    static final String CST_USER = "user";
    static final String CST_PARAM_ONLY_NON_INVITED_USER = "onlyNonInvitedUser";
    static final String CST_PARAM_EMAIL = "email";
    protected static final String CST_PARAM_PHONE_NUMBER = "phoneNumber";
    protected static final String CST_PARAM_FIRST_NAME = "firstName";
    protected static final String CST_PARAM_LAST_NAME = "lastName";

    static final String CST_PARAM_SEARCHUSER_SENTENCE = "searchusersentence";
    static final String CST_PARAM_SEARCHUSER_ALL = "all";
    static final String CST_PARAM_SEARCHUSER_CONNECTED = "connected";
    static final String CST_PARAM_SEARCHUSER_BLOCK = "block";
    static final String CST_PARAM_SEARCHUSER_ADMINSTRATOR = "administrator";
    static final String CST_PARAM_SEARCHUSER_PREMIUM = "premium";
    static final String CST_PARAM_SEARCHUSER_EXCELLENCE = "excellence";
    static final String CST_PARAM_SEARCHUSER_TIMEZONEOFFSET = "timezoneoffset";
    
    
    static final String CST_PARAM_USERID = "userid";
    static final String CST_PARAM_ATTRIBUT = "attribut";
    static final String CST_PARAM_VALUE = "value";

}

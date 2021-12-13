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

    /**
     * filter
     */
    public static final String CST_PARAM_FILTER_EVENTS = "filterEvents";
    public static final String CST_PARAM_FILTER_EVENTS_V_MYEVENTS = "MyEvents";
    public static final String CST_PARAM_FILTER_EVENTS_V_ALLEVENTS = "AllEvents";
    public static final String CST_PARAM_FILTER_EVENTS_V_MYINVITATIONS = "MyInvitations";
    public static final String PARAM_TIMEZONEOFFSET = "timezoneoffset";

    public static final String CST_PARAM_TYPE_EVENTS = "TypeEvents";
    public static final String CST_PARAM_NAME = "name";
    public static final String LOG_EVENTS = "listLogEvents";
    public static final String CST_EVENT_ID = "eventId";
    public static final String EVENT = "event";
    public static final String CST_LIST_EVENTS = "events";
    public static final String CST_CHILDENTITY = "childEntity";
    public static final String CHILDENTITYID = "childEntityId";
    public static final String LIMIT_SUBSCRIPTION = "limitsubscription";
    /**
     * Status may have multiple value (in case of invitation for example), and 2 default value, OK and ERROR, are proposed
     */
    public static final String STATUS = "status";
    public static final String STATUS_V_OK = "OK";
    public static final String STATUS_V_ERROR = "ERROR";
    public static final String MESSAGE_OK = "okMessage";
    public static final String MESSAGE_ERROR = "errorMessage";
    public static final String MESSAGE_ERROR_SEND_EMAIL = "errorSendEmail";

    public static final String ISINVITATIONSENT = "isInvitationSent";
    protected static final String PARAM_PHONE_NUMBER = "phoneNumber";
    protected static final String PARAM_FIRST_NAME = "firstName";
    protected static final String PARAM_LAST_NAME = "lastName";

    static final String PARAM_AUTHORIZATION = "Authorization";
    static final String INJSON_EVENTID = "eventid";
    static final String PAGE = "page";
    static final String ITEMS_PER_PAGE = "itemsPerPage";
    static final String NUMBER_OF_PAGES = "numberOfPages";
    static final String NUMBER_OF_ITEMS = "numberOfItems";
    static final String CST_COUNTUSERS = "countusers";
    static final String LISTUSERS = "users";
    static final String USER = "user";
    static final String PARAM_ONLY_NON_INVITED_USER = "onlyNonInvitedUser";
    static final String PARAM_EMAIL = "email";
    static final String PARAM_SEARCHUSER_SENTENCE = "searchusersentence";
    static final String PARAM_SEARCHUSER_ALL = "all";
    static final String PARAM_SEARCHUSER_CONNECTED = "connected";
    static final String PARAM_SEARCHUSER_BLOCK = "block";
    static final String PARAM_SEARCHUSER_ADMINSTRATOR = "administrator";
    static final String PARAM_SEARCHUSER_PREMIUM = "premium";
    static final String PARAM_SEARCHUSER_EXCELLENCE = "excellence";
    static final String PARAM_SEARCHUSER_TIMEZONEOFFSET = "timezoneoffset";


    static final String PARAM_USERID = "userid";
    static final String PARAM_ATTRIBUT = "attribut";
    static final String PARAM_VALUE = "value";

    static final String LISTLOGINLOG = "listLoginLogs";
    static final String PARAM_SEARCHLOGINLOG_SENTENCE = "sentence";
    static final String PARAM_SEARCHLOGINLOG_OK = "ok";
    static final String PARAM_SEARCHLOGINLOG_UNKNOWNUSER = "unknownUser";
    static final String PARAM_SEARCHLOGINLOG_BADPASSWORD = "badPassword";
    static final String PARAM_SEARCHLOGINLOG_UNDERATTACK = "underAttack";
    static final String PARAM_SEARCHLOGINLOG_TIMEZONEOFFSET = "timezoneoffset";

    static final String PARAM_SEARCHLOGINLOG_DATESTART = "dateStart";
    static final String PARAM_SEARCHLOGINLOG_DATEEND = "dateEnd";
    static final String PARAM_SEARCHLOGINLOG_TIMESTART = "timeStart";
    static final String PARAM_SEARCHLOGINLOG_TIMEEND = "timeEnd";

    static final String PARAM_ACTIVE = "active";

    /**
     * List of constants
     */
    private RestJsonConstants() {
    }


}

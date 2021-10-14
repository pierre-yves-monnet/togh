/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.service.TranslatorService.Sentence;
import com.togh.service.event.EventController;
import com.togh.service.event.EventPresentationAttribut;
import com.togh.tool.email.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Notify service */
/*                                                                      */
/* This service is in charge to track any performance issue, and monitor */
/* the number of time a function is used for example */
/* -------------------------------------------------------------------- */

@Service
public class NotifyService {

    private static final String NBSP = "&nbsp;";
    private static final String BR = "<br>";

    private static final String HTTP_DEFAULT_HOST_TOGH = "http://localhost:3000";
    private static final String LOG_HEADER = "com.togh.NotifyService";
    private static final String MAIL_HOST = "localhost";

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private EventFactoryRepository factoryRepository;

    @Autowired
    private TranslatorService translatorService;

    @Autowired
    private ApiKeyService apiKeyService;
    private static final int MAIL_PORT = 2525;
    private static final boolean MAIL_TLS = false;
    private static final String MAIL_USER_NAME = "";
    private static final String MAIL_USER_PASSWORD = "";
    private final Logger logger = Logger.getLogger(NotifyService.class.getName());
    @Autowired
    private SendEmail sendEmail;

    private final static LogEvent eventEmailError = new LogEvent(NotifyService.class.getName(), 1, Level.APPLICATIONERROR, "Email Error", "The email can't be sent", "User will not received an email", "Check Exception");

    /**
     * @param toghUserEntity    The user to invite
     * @param invitedByToghUser Tog user who send the invitation
     * @param useMyEmailAsFrom  if true, the From email is the invityByToghUser email
     * @param eventEntity       the event Entity
     * @return a notification status. Email is sent
     */
    public NotificationStatus notifyNewUserInEvent(@Nonnull ToghUserEntity toghUserEntity,
                                                   ToghUserEntity invitedByToghUser,
                                                   boolean useMyEmailAsFrom,
                                                   @Nonnull EventEntity eventEntity) {

        // the userEntity will contains the language
        String lang = toghUserEntity.getLanguage() == null ? invitedByToghUser.getLanguage() : toghUserEntity.getLanguage();
        String subject = translatorService.getDictionarySentence(Sentence.INVITED_TOGH_EVENT, lang);

        StringBuilder cartridgeText = new StringBuilder();
        cartridgeText.append(translatorService.getDictionarySentence(Sentence.DEAR, lang));
        if (toghUserEntity.getFirstName() != null)
            cartridgeText.append(NBSP + toghUserEntity.getFirstName());
        if (toghUserEntity.getLastName() != null)
            cartridgeText.append(NBSP + toghUserEntity.getLastName());

        cartridgeText.append("," + BR);
        cartridgeText.append(translatorService.getDictionarySentence(Sentence.YOU_ARE_INVITED_BY, lang));
        cartridgeText.append(NBSP);
        cartridgeText.append("<span style=\"color: #1f78b4;font-weight: bold;\">" + invitedByToghUser.getLabel() + "</span>");
        cartridgeText.append(NBSP);
        cartridgeText.append(translatorService.getDictionarySentence(Sentence.TO_JOIN_A, lang));
        cartridgeText.append(NBSP);
        cartridgeText.append("<span style=\"color: #1f78b4;font-weight: bold;\">" + translatorService.getDictionarySentence(Sentence.TOGH_EVENT, lang) + "</span>");

        StringBuilder st = new StringBuilder();

        st.append(getEmailHeader(cartridgeText.toString(), lang));

        st.append(translatorService.getDictionarySentence(Sentence.TOGH_EVENT_EXPLANATION, lang));
        st.append(BR);

        st.append(String.format(translatorService.getDictionarySentence(Sentence.THE_EVENT_WE_PROPOSE_TO_JOIN, lang), invitedByToghUser.getLabel()));

        EventController eventController = EventController.getInstance(eventEntity, factoryService, factoryRepository);
        EventPresentationAttribut eventPresentationAttribut = new EventPresentationAttribut();
        eventPresentationAttribut.bannerAction = "<a href=\"" + getUrlInvitation(toghUserEntity, eventEntity) + "\""
                + " style=\"text-decoration: none;color: white;\">"
                + translatorService.getDictionarySentence(Sentence.REGISTER_AND_JOIN_THIS_EVENT, lang) + "</a>";

        st.append(eventController.getEventPresentation().getHtmlPresentation(eventPresentationAttribut, toghUserEntity));

        st.append(translatorService.getDictionarySentence(Sentence.TO_JOIN_EXPLANATION, lang) + BR);

        // To join, just click the button. Register yourself with our email address, and welcome to Togh!<br>
        st.append(translatorService.getDictionarySentence(Sentence.TO_ACCESS_TOGH, lang));
        st.append("<a href=\"" + getHttpLink(HTTP_DEFAULT_HOST_TOGH) + "\">Togh</a>");

        // You can access the Togh application via this link to have more information: <a href="http://localhost:3000/togh">Togh</a>

        return sendEmail(toghUserEntity.getEmail(),
                (useMyEmailAsFrom ? invitedByToghUser.getEmail() : null),
                subject, st.toString());
    }

    /**
     * Get the URL to use to arrive to the Togh event invitation
     *
     * @param toghUserEntity togh User
     * @param eventEntity    event
     * @return a complete URL
     */
    public String getUrlInvitation(ToghUserEntity toghUserEntity, EventEntity eventEntity) {
        boolean newUser = toghUserEntity.getStatusUser().equals(ToghUserEntity.StatusUserEnum.INVITED);
        return getHttpLink(HTTP_DEFAULT_HOST_TOGH)
                + "?action=" + (newUser ? "invitedNewUser" : "invitedUser")
                + "&invitationStamp=" + toghUserEntity.getInvitationStamp()
                + "&eventid=" + eventEntity.getId()
                + "&email=" + toghUserEntity.getEmail();
    }

    // --------------------------------------------------------------
    // 
    // Notification
    // 
    // --------------------------------------------------------------

    /**
     * Send the Lost Password email
     *
     * @param toghUserEntity
     * @param uuid
     * @return
     */
    public NotificationStatus sendLostPasswordEmail(ToghUserEntity toghUserEntity, String uuid) {

        String textInCartouche = translatorService.getDictionarySentence(Sentence.YOU_LOST_YOUR_PASSWORD, toghUserEntity.getLanguage()) + BR;
        textInCartouche += translatorService.getDictionarySentence(Sentence.CLICK_TO_RESET_PASSWORD, toghUserEntity.getLanguage());

        StringBuilder st = new StringBuilder();
        st.append(getEmailHeader(textInCartouche, toghUserEntity.getLanguage()));

        String url = getHttpLink(HTTP_DEFAULT_HOST_TOGH) + "?action=resetpassword&uuid=" + uuid;

        st.append("<a href='" + url + "'>" + translatorService.getDictionarySentence(Sentence.RESET_MY_PASSWORD, toghUserEntity.getLanguage()) + "</a>");
        return sendEmail(toghUserEntity.getEmail(),
                null,
                translatorService.getDictionarySentence(Sentence.TOGH_RESET_PASSWORD, toghUserEntity.getLanguage()),
                st.toString());
    }


    /**
     * A status of an event change
     * Some person may want to be notify?
     *
     * @param eventEntity
     * @param oldStatus   status before the change
     */
    public NotificationStatus notifyEventChangeStatus(@Nonnull EventEntity eventEntity, StatusEventEnum oldStatus) {
        return new NotificationStatus();
    }

    /**
     * Anevent is purged
     * Some person may want to be notify?
     *
     * @param eventEntity
     */
    public NotificationStatus notifyEventPurge(@Nonnull EventEntity eventEntity) {
        return new NotificationStatus();
    }


    private String getHttpLink(String defaultHttp) {
        return apiKeyService.getHttpToghServer(defaultHttp); // "http://localhost:7080/togh";
    }

    /**
     * Send Email
     *
     * @param emailTo     Email to this mail
     * @param emailFrom   Email From. If null, the Togh Email is used
     * @param mailSubject Subject of mail
     * @param mailContent Content of email
     * @return the notification status
     */
    private NotificationStatus sendEmail(String emailTo, String emailFrom, String mailSubject, String mailContent) {
        NotificationStatus notificationStatus = new NotificationStatus();

        notificationStatus.listEvents.addAll(sendEmail.sendOneEmail(emailTo, emailFrom, mailSubject, mailContent));
        if (!notificationStatus.listEvents.isEmpty() &&
                notificationStatus.listEvents.get(0).isSameEvent(SendEmail.eventNoEmailServerConfigured))
            notificationStatus.serverIssue = true;
        return notificationStatus;
    }

    // --------------------------------------------------------------
    // 
    // Toolbox password
    // 
    // --------------------------------------------------------------

    /**
     * @return
     */
    private String getEmailHeader(String textInCartouch, String lang) {
        StringBuilder st = new StringBuilder();

        st.append("<table with=\100%\"><tr>");
        st.append("<td><img src=\"" + getHttpLink(HTTP_DEFAULT_HOST_TOGH) + "/img/togh.jpg\" style=\"width:100\"/></td>");
        st.append("<td>" + textInCartouch + "</td>");
        st.append("</table>");
        return st.toString();

    }

    // --------------------------------------------------------------
    // 
    // Send Email
    // 
    // --------------------------------------------------------------

    public class NotificationStatus {

        public final List<LogEvent> listEvents = new ArrayList<>();
        private boolean serverIssue;

        public boolean isCorrect() {
            return !LogEventFactory.isError(listEvents);
        }

        public boolean hasServerIssue() {
            return serverIssue;
        }
    }

}

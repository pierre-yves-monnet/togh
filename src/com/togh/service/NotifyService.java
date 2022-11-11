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
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.serialization.ToghUserSerializer;
import com.togh.service.TranslatorService.Sentence;
import com.togh.service.event.EventController;
import com.togh.service.event.EventPresentationAttribut;
import com.togh.tool.email.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  private final static String LOG_HEADER = NotifyService.class.getSimpleName() + ": ";

  private static final String NBSP = "&nbsp;";
  private static final String BR = "<br>";

  private static final String HTTP_DEFAULT_HOST_TOGH = "http://localhost:3000";


  private static final String MAIL_HOST = "localhost";
  private static final int MAIL_PORT = 2525;
  private static final boolean MAIL_TLS = false;
  private static final String MAIL_USER_NAME = "";
  private static final String MAIL_USER_PASSWORD = "";
  private static final LogEvent eventEmailError = new LogEvent(NotifyService.class.getName(), 1, Level.APPLICATIONERROR, "Email Error", "The email can't be sent", "User will not received an email", "Check Exception");
  private static final LogEvent eventPurged = new LogEvent(NotifyService.class.getName(), 2, Level.MAININFO, "Event Purged", "A very old event is purged");
  private static final Logger logger = Logger.getLogger(NotifyService.class.getName());
  @Autowired
  private FactoryService factoryService;
  @Autowired
  private EventFactoryRepository factoryRepository;
  @Autowired
  private TranslatorService translatorService;
  @Autowired
  private ApiKeyService apiKeyService;
  @Autowired
  private LogService logService;
  @Autowired
  private SendEmail sendEmail;

  /**
   * @param toghUserEntity    The user to invite
   * @param invitedByToghUser Tog user who send the invitation
   * @param subject           email subject
   * @param message           email message
   * @param useMyEmailAsFrom  if true, the From email is the invityByToghUser email
   * @param eventEntity       the event Entity
   * @return a notification status. Email is sent
   */
  public NotificationStatus notifyNewUserInEvent(@Nonnull ToghUserEntity toghUserEntity,
                                                 ToghUserEntity invitedByToghUser,
                                                 String subject,
                                                 String message,
                                                 boolean useMyEmailAsFrom,
                                                 @Nonnull EventEntity eventEntity,
                                                 FactorySerializer factorySerializer) {
    ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromClass(ToghUserEntity.class);
    SerializerOptions serializeOptions = new SerializerOptions(toghUserEntity, 0L, SerializerOptions.ContextAccess.EVENTACCESS);

    // the userEntity will contain the language
    String lang = toghUserEntity.getLanguage() == null ? invitedByToghUser.getLanguage() : toghUserEntity.getLanguage();
    String subjectEmail = (subject == null || subject.trim().length() == 0) ?
        translatorService.getDictionarySentence(Sentence.INVITED_TOGH_EVENT, lang) :
        subject;

    StringBuilder cartridgeText = new StringBuilder();
    cartridgeText.append(translatorService.getDictionarySentence(Sentence.DEAR, lang));
    if (toghUserEntity.getFirstName() != null)
      cartridgeText.append(NBSP + toghUserEntity.getFirstName());
    if (toghUserEntity.getLastName() != null)
      cartridgeText.append(NBSP + toghUserEntity.getLastName());

    cartridgeText.append("," + BR);
    cartridgeText.append(translatorService.getDictionarySentence(Sentence.YOU_ARE_INVITED_BY, lang));
    cartridgeText.append(NBSP);
    cartridgeText.append("<span style=\"color: #1f78b4;font-weight: bold;\">" + toghUserSerializer.getUserLabel(invitedByToghUser, serializeOptions) + "</span>");
    cartridgeText.append(NBSP);
    cartridgeText.append(translatorService.getDictionarySentence(Sentence.TO_JOIN_A, lang));
    cartridgeText.append(NBSP);
    cartridgeText.append("<span style=\"color: #1f78b4;font-weight: bold;\">" + translatorService.getDictionarySentence(Sentence.TOGH_EVENT, lang) + "</span>");

    StringBuilder st = new StringBuilder();

    st.append(getEmailHeader(cartridgeText.toString(), lang));
    if (message != null && message.trim().length() > 0) {
      st.append(BR);
      st.append(message);
      st.append(BR);
    }
    st.append(translatorService.getDictionarySentence(Sentence.TOGH_EVENT_EXPLANATION, lang));
    st.append(BR);

    st.append(String.format(translatorService.getDictionarySentence(Sentence.THE_EVENT_WE_PROPOSE_TO_JOIN, lang),
        toghUserSerializer.getUserLabel(invitedByToghUser, serializeOptions)));

    EventController eventController = EventController.getInstance(eventEntity, factoryService, factoryRepository, factorySerializer);
    EventPresentationAttribut eventPresentationAttribut = new EventPresentationAttribut();
    eventPresentationAttribut.bannerAction = "<a href=\"" + getUrlInvitation(toghUserEntity, eventEntity) + "\""
        + " style=\"text-decoration: none;color: white;\">"
        + translatorService.getDictionarySentence(Sentence.REGISTER_AND_JOIN_THIS_EVENT, lang) + "</a>";

    st.append(eventController.getEventPresentation().getHtmlPresentation(eventPresentationAttribut, toghUserEntity, factorySerializer));

    st.append(translatorService.getDictionarySentence(Sentence.TO_JOIN_EXPLANATION, lang) + BR);

    // To join, just click the button. Register yourself with our email address, and welcome to Togh!<br>
    st.append(translatorService.getDictionarySentence(Sentence.TO_ACCESS_TOGH, lang));
    st.append("<a href=\"" + getHttpLink(HTTP_DEFAULT_HOST_TOGH) + "\">Togh</a>");

    // You can access the Togh application via this link to have more information: <a href="http://localhost:3000/togh">Togh</a>

    return sendEmail(toghUserEntity.getEmail(),
        (useMyEmailAsFrom ? invitedByToghUser.getEmail() : null),
        subjectEmail, st.toString());
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
        + (toghUserEntity.getInvitationStamp() != null ? "&invitationStamp=" + toghUserEntity.getInvitationStamp() : "")
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
   * @param toghUserEntity togh user
   * @param uuid           UUID to verify the lost password
   * @return the status of the notification
   */
  public NotificationStatus sendLostPasswordEmail(ToghUserEntity toghUserEntity, String uuid) {

    String textInCartouche = translatorService.getDictionarySentence(Sentence.YOU_LOST_YOUR_PASSWORD, toghUserEntity.getLanguage()) + BR;
    textInCartouche += translatorService.getDictionarySentence(Sentence.CLICK_TO_RESET_PASSWORD, toghUserEntity.getLanguage());

    StringBuilder st = new StringBuilder();

    String url = getHttpLink(HTTP_DEFAULT_HOST_TOGH) + "?action=resetpassword&uuid=" + uuid;

    st.append("<a href='" + url + "'>" + translatorService.getDictionarySentence(Sentence.RESET_MY_PASSWORD, toghUserEntity.getLanguage()) + "</a>");
    st.append(getEmailHeader(textInCartouche, toghUserEntity.getLanguage()));
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
   * @param eventEntity event entity
   * @param oldStatus   previous status
   * @return Notification Status
   */
  public NotificationStatus notifyEventChangeStatus(@Nonnull EventEntity eventEntity, StatusEventEnum oldStatus) {
    return new NotificationStatus();
  }

  /**
   * An event is purged
   * Some person may want to be notified?
   *
   * @param eventEntity the event entity
   * @param delayPurge  delay used to purge this event
   * @param datePurge   date close : all events before this date will be purged
   * @return the notification status
   */
  public NotificationStatus notifyEventPurged(@Nonnull EventEntity eventEntity, int delayPurge, LocalDateTime datePurge) {
    List<LogEvent> listEvents = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    listEvents.add(new LogEvent(eventPurged, "Event " + eventEntity
        + " dateEndEvent:" + (eventEntity.getDateEndEvent() == null ? "null" : eventEntity.getDateEndEvent().format(formatter))
        + " dateModification:" + (eventEntity.getDateModification() == null ? "null" : eventEntity.getDateModification().format(formatter))
        + " Date Close:" + datePurge.format(formatter)
        + " Delay Purged" + delayPurge));
    logService.registerLog(listEvents, null);
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
   * From the text in Cartridge, return the email header
   *
   * @param textInCartridge text in the Cartridge
   * @param language        language to translate the header
   * @return
   */
  private String getEmailHeader(String textInCartridge, String language) {
    StringBuilder st = new StringBuilder();

    st.append("<table with=\100%\"><tr>");
    st.append("<td><img src=\"" + getHttpLink(HTTP_DEFAULT_HOST_TOGH) + "/img/togh.jpg\" style=\"width:100\"/></td>");
    st.append("<td>" + textInCartridge + "</td>");
    st.append("</table>");
    return st.toString();

  }

  // --------------------------------------------------------------
  //
  // Send Email
  //
  // --------------------------------------------------------------

  public static class NotificationStatus {

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

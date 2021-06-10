/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.tool.email.SendEmail;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Notify service */
/*                                                                      */
/* This service is in charge to track any performance issue, and monitor */
/* the number of time a function is used for example    */
/* -------------------------------------------------------------------- */

@Service
public class NotifyService {
    
    private static final String HTTP_DEFAULT_HOST_TOGH = "http://localhost:3000/togh";
    private Logger logger = Logger.getLogger( NotifyService.class.getName());
    private final static String logHeader = "com.togh.NotifyService";

    @Autowired
    private FactoryService factoryService;
    
    @Autowired
    private ApiKeyService apiKeyService;

    
    private final static String mailHost = "localhost";
    private final static int mailPort=2525;
    private final static boolean mailTLS=false;
    private final static String mailUserName="";
    private final static String mailUserPassword="";
    
    /**
     * first key is the language, second the dictionary
     */
    private static Map<String, Map<String,String>> dictionary = new HashMap<>();
    
    private final static LogEvent eventEmailError = new LogEvent(NotifyService.class.getName(), 1, Level.APPLICATIONERROR, "Email Error", "The email can't be sent", "User will not received an email", "Check Exception");
    
    public class NotificationStatus {
        List<LogEvent> listEvents = new ArrayList<>();
        public boolean isCorrect() {
            return ! LogEventFactory.isError(listEvents);
        }
        public boolean serverIssue;
    }
    
    
    // --------------------------------------------------------------
    // 
    // Initialisation: populate dictionnary
    // 
    // --------------------------------------------------------------
    public void populateDictionary(Map<String,Map<String,String>> dictionaryUpdate) {
        dictionary.putAll(dictionaryUpdate);
    }
     
    private String getDictionarySentence( String message, String lang) {
        if (dictionary.containsKey(lang)) {
            if (dictionary.get( lang ).containsKey(message)) {
                return dictionary.get( lang ).get(message);
            }
        }
        return message;
    }
    
    
    // --------------------------------------------------------------
    // 
    // Notification
    // 
    // --------------------------------------------------------------

    public NotificationStatus notifyNewUserInEvent( ToghUserEntity userEntity, ToghUserEntity invitedBy, EventEntity eventEntity) {
        
       
        // the userEntity will contains the language 
        String subject="You are invited in a Togh Event!";
        
        StringBuilder st = new StringBuilder();
        st.append(getEmailHeader(userEntity.getLanguage(), userEntity));

        st.append(getDictionarySentence("You are invited by ", userEntity.getLanguage()));
        st.append("&nbsp;"+invitedBy.getLabel()+"&nbsp;");
        st.append(getDictionarySentence("to join the event ",userEntity.getLanguage()));
        st.append(eventEntity.getName()+".");
        st.append("<br>");
        
        st.append(getDictionarySentence("In this event, you will share participants, messages and a lot of more information!", userEntity.getLanguage()));
        st.append(getDictionarySentence("To join, just click here, to register yourself.",userEntity.getLanguage()));
        st.append(" <a href=\""+getHttpLink(HTTP_DEFAULT_HOST_TOGH)+"/registerUser\">"+getDictionarySentence("ToghRegistration",userEntity.getLanguage())+"</a");
        st.append("<br>");
        
        st.append(getDictionarySentence("Event description:",userEntity.getLanguage()));
        st.append("<br>");
        
        st.append(eventEntity.getDescription());
        return sendEmail(userEntity.getEmail(), subject, st.toString());
    }
    
    private String getHttpLink(String defaultHttp) {
        return apiKeyService.getHttpToghServer(defaultHttp); // "http://localhost:7080/togh";
    }
    
  
    
    
    /**
     * Send the Lost Password email
     * @param toghUserEntity
     * @param uuid
     * @return
     */
    public NotificationStatus sendLostPasswordEmail(ToghUserEntity toghUserEntity, String uuid) {
        
        StringBuilder st = new StringBuilder();
        st.append(getEmailHeader(toghUserEntity.getLanguage(), toghUserEntity));
        st.append(getDictionarySentence("We are sorry to heard that you lost your password", toghUserEntity.getLanguage()));
        st.append(getDictionarySentence("No worry, click on this link to reset it", toghUserEntity.getLanguage()));
        st.append("<br>");
        
        String url = getHttpLink(HTTP_DEFAULT_HOST_TOGH)+"?action=resetpassword&uuid="+uuid;
        
        st.append("<a href='"+url+"'>"+getDictionarySentence("Reset my password",toghUserEntity.getLanguage())+"</a>");
        return sendEmail(toghUserEntity.getEmail(), getDictionarySentence("Togh reset password",toghUserEntity.getLanguage()), st.toString());
    }
    
    
    // --------------------------------------------------------------
    // 
    // Toolbox password
    // 
    // --------------------------------------------------------------

    /**
     * 
     * @return
     */
    private String getEmailHeader(String lang, ToghUserEntity toghUserEntity) {
        StringBuilder st = new StringBuilder();

        st.append("<table with=\100%\"><tr style=\"color:red\">");
        st.append("<td><img src=\""+getHttpLink(HTTP_DEFAULT_HOST_TOGH)+"/img/togh.jpg\"/></td>");
        st.append("<td>"+getDictionarySentence("Togh",lang)+"</td>");
        st.append("</tr></table>");
        
        if (toghUserEntity !=null)
            st.append(getDictionarySentence("Dear", lang)+"&nbsp;"+toghUserEntity.getFirstName()+"&nbsp;"+toghUserEntity.getFirstName()+",");
        return st.toString();
    }
    
    // --------------------------------------------------------------
    // 
    // Send Email
    // 
    // --------------------------------------------------------------

    /**
     * 
     * @param emailTo
     * @param mailSubject
     * @param mailContent
     * @return
     */
    private NotificationStatus sendEmail (String emailTo, String mailSubject, String mailContent) {
        NotificationStatus notificationStatus = new NotificationStatus();

        SendEmail sendEmail = new SendEmail( factoryService );
        notificationStatus.listEvents.addAll( sendEmail.sendOneEmail( emailTo, mailSubject, mailContent ));
        if (notificationStatus.listEvents.size()>0 && 
                notificationStatus.listEvents.get(0).isSameEvent( SendEmail.eventNoEmailServerConfigured ))
            notificationStatus.serverIssue=true;    
        return notificationStatus;
    }
    
   
            
}

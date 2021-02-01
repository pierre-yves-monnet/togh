package com.togh.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.logevent.LogEvent;
import com.togh.logevent.LogEvent.Level;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Notify service */
/*                                                                      */
/* This service is in charge to track any performance issue, and monitor */
/* the number of time a function is used for example    */
/* -------------------------------------------------------------------- */

@Service
public class NotifyService {
    
    private Logger logger = Logger.getLogger( NotifyService.class.getName());
    private final static String logHeader = "com.togh.NotifyService";
    
    private final static String mailHost = "localhost";
    private final static int mailPort=2525;
    private final static boolean mailTLS=false;
    private final static String mailUserName="";
    private final static String mailUserPassword="";
    
    
    private final static LogEvent eventEmailError = new LogEvent(NotifyService.class.getName(), 1, Level.APPLICATIONERROR, "Email Error", "The email can't be sent", "User will not received an email", "Check Exception");
    
    
    
    public List<LogEvent> notifyNewUserInEvent( ToghUserEntity userEntity, ToghUserEntity invitedBy, EventEntity eventEntity) {
        
        // the userEntity will contains the language 
        String subject="You are invited in a Togh Event!";
        
        StringBuilder message = new StringBuilder();
        message.append( "<table with=\100%\"><tr style=\"background-color:red\"><td> Welcome to Togh !</td></tr></table>" );
        message.append(" You are invited by "+invitedBy.getLabel()+" to join the event "+eventEntity.getName()+". In this event, you will share participants, messages and a lot of more information!");
        message.append("To join, just click here, to register yourself <a href=\""+getHttpLink()+"/registerUser\"> ToghRegistration");
        message.append("<br>");
        message.append(" Event description:<br>");
        message.append(eventEntity.getDescription());
        return sendEmail(userEntity.getEmail(), subject, message.toString());
    }
    
    private String getHttpLink() {
        return "localhost:7080/togh";
    }
    
    private class ToghAuthenticator extends Authenticator {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication( mailUserName, mailUserPassword);
        }
    }
    
    
    
    
    private List<LogEvent> sendEmail (String email, String mailSubject, String mailContent) {
        List<LogEvent> listEvents = new ArrayList<>();
        
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", mailTLS);
        prop.put("mail.smtp.host", mailHost);
        prop.put("mail.smtp.port", mailPort);
        // prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
        Authenticator auth = new ToghAuthenticator();
        Session session = Session.getInstance(prop, auth);
        
        Message message= new MimeMessage(session);
        try {
            message.setFrom( new InternetAddress("togh@togh.com"));

            message.setRecipients( Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject( mailSubject );
    
    
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(mailContent, "text/html");
    
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
    
            message.setContent(multipart);
    
            Transport.send(message);
        } catch (MessagingException e) {
            logger.severe(logHeader+"Error sending email to  ["+email+"]");
            listEvents.add( new LogEvent( eventEmailError, e, "Email["+email+"]"));
        }        
        return listEvents;
    }
}

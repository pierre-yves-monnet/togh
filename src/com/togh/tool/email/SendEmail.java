package com.togh.tool.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPSendFailedException;
import com.sun.mail.smtp.SMTPTransport;
import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.service.ApiKeyService;
import com.togh.service.FactoryService;

public class SendEmail {

    private FactoryService factoryService;

    public static final LogEvent eventSendEmail = new LogEvent(SendEmail.class.getName(), 1, Level.APPLICATIONERROR, "Error Email", "Error sending email", "Email can't be send", "Check the exception");
    public static final LogEvent eventNoEmailServerConfigured = new LogEvent(SendEmail.class.getName(), 2, Level.APPLICATIONERROR, "No Email server configured", "The SMTP server is not configured. No email can be send", "Email can't be send", "Setup the SMTP server configuration");
    private static Logger logger = Logger.getLogger(SendEmail.class.getName());

    public SendEmail(FactoryService factoryService) {

        this.factoryService = factoryService;
    }

    ;

    /**
     * @param emailTo
     * @return
     */
    public List<LogEvent> sendOneEmail(String emailTo, String mailSubject, String mailContent) {

        List<LogEvent> listEvents = new ArrayList<>();
        ApiKeyService keyService = factoryService.getApiKeyService();
        String logConnection="";
        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        if (keyService.getSmtpHost() == null || keyService.getSmtpPort() == null) {
            listEvents.add(eventNoEmailServerConfigured);
            return listEvents;
        }
        props.put("mail.smtp.host", keyService.getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(keyService.getSmtpPort()));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        logConnection+=String.format("host[%s] port[%d] Auth[true] starttls.enable[true] user[%s]",
                keyService.getSmtpHost(),
                keyService.getSmtpPort(),
                keyService.getSmtpUserName());
        // https://mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
        // prop.put("mail.smtp.auth", true);
        // prop.put("mail.smtp.starttls.enable", mailTLS);
        // final String username = "username@gmail.com";
        // final String password = "password";

        // prop.put("mail.smtp.host", "smtp.gmail.com");

        // prop.put("mail.smtp.port", "587"); 465 ?
        // prop.put("mail.smtp.auth", "true");
        // prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(keyService.getSmtpUserName(), keyService.getSmtpUserPassword());
                    }
                });
        // Get the default Session object.
        // Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(keyService.getSmtpFrom()));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));

            // Set Subject: header field
            message.setSubject(mailSubject);

            // Now set the actual message
            message.setContent(mailContent, "text/html");

            Transport.send(message);

            /*
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

            // connect
            t.connect(keyService.getSmtpHost(), keyService.getSmtpUserName(), keyService.getSmtpUserPassword());


            t.sendMessage(message, message.getAllRecipients());
            t.close();
            */

        } catch (SMTPSendFailedException smtpEx) {
            logger.severe("SendMessage  connection "+logConnection+" Exception "+ smtpEx.getMessage());
            listEvents.add(new LogEvent(eventNoEmailServerConfigured, smtpEx, "Sender"));

        } catch (MessagingException mex) {
            logger.severe("SendMessage  connection "+logConnection+" Exception "+ mex.getMessage());
            listEvents.add(new LogEvent(eventNoEmailServerConfigured, mex, "Email[" + emailTo + "]"));

        } catch (Exception e) {
            logger.severe("SendMessage  connection "+logConnection+" Exception "+ e.getMessage());
            listEvents.add(new LogEvent(eventSendEmail, e, "Email[" + emailTo + "]"));
        }
        return listEvents;
    }
}

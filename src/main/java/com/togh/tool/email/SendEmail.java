package com.togh.tool.email;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.service.ApiKeyService;
import com.togh.service.FactoryService;
import com.togh.service.SmtpKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Component
public class SendEmail {

  public static final LogEvent eventEmailSent = new LogEvent(SendEmail.class.getName(), 1, Level.SUCCESS, "Mail sent with success", "The email is sent with success");
  public static final LogEvent eventSendEmail = new LogEvent(SendEmail.class.getName(), 2, Level.APPLICATIONERROR, "Error Email", "Error sending email", "Email can't be send", "Check the exception");
  public static final LogEvent eventNoEmailServerConfigured = new LogEvent(SendEmail.class.getName(), 3, Level.APPLICATIONERROR, "No Email server configured", "The SMTP server is not configured. No email can be send", "Email can't be send", "Setup the SMTP server configuration");
  private static final Logger logger = Logger.getLogger(SendEmail.class.getName());
  @Autowired
  private JavaMailSender emailSender;
  @Autowired
  private FactoryService factoryService;

  /**
   * @param emailTo     email address
   * @param emailFrom   Email From. If null, the Togh Email is used
   * @param mailSubject subject of the email
   * @param mailContent content of the email
   * @return list of events to describe the status of the operation
   */
  public List<LogEvent> sendOneEmail(String emailTo, String emailFrom, String mailSubject, String mailContent) {

    ApiKeyService keyService = factoryService.getApiKeyService();
    return sendOneEmail(emailTo, emailFrom, mailSubject, mailContent, keyService);
  }

  /**
   * Send an email
   * See https://www.baeldung.com/spring-email,
   * https://mkyong.com/spring-boot/spring-boot-how-to-send-email-via-smtp/
   *
   * @param emailTo      email address
   * @param mailSubject  subject of the email
   * @param mailContent  content of the email
   * @param emailService to get access of all needed key
   * @return list of events to describe the status of the operation
   */
  public List<LogEvent> sendOneEmail(String emailTo,
                                     String emailFrom,
                                     String mailSubject,
                                     String mailContent,
                                     SmtpKeyService emailService) {

    List<LogEvent> listEvents = new ArrayList<>();
    String logConnection = "";
    // Get system properties

    // Setup mail server
    if (emailService.getSmtpHost() == null || emailService.getSmtpPort() == 0) {
      listEvents.add(eventNoEmailServerConfigured);
      return listEvents;
    }
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();


    mailSender.setHost(emailService.getSmtpHost());
    mailSender.setPort(emailService.getSmtpPort());
    mailSender.setUsername(emailService.getSmtpUserName());
    mailSender.setPassword(emailService.getSmtpUserPassword());
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true"); // <-- required sometimes
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");  // <-- important for Java 11+

    props.put("mail.debug", "true");

    logConnection += String.format("host[%s] port[%d] Auth[true] starttls.enable[true] user[%s]",
        emailService.getSmtpHost(),
        emailService.getSmtpPort(),
        emailService.getSmtpUserName());

    try {

      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

      helper.setFrom(emailFrom != null ? emailFrom : emailService.getSmtpFrom());
      helper.setTo(emailTo);
      helper.setSubject(mailSubject);
      helper.setText(mailContent, true);
      mailSender.send(mimeMessage);

      listEvents.add(new LogEvent(eventEmailSent, "Sent to [" + emailTo + "]"));

    } catch (MailException smtpEx) {
      logger.severe("SendMessage  connection " + logConnection + " Exception " + smtpEx.getMessage());
      listEvents.add(new LogEvent(eventNoEmailServerConfigured, smtpEx, "Sender " + smtpEx.getMessage()));

    } catch (Exception e) {
      logger.severe("SendMessage  connection " + logConnection + " Exception " + e.getMessage());
      listEvents.add(new LogEvent(eventSendEmail, e, "Email[" + emailTo + "]"));
    }
    return listEvents;
  }
}

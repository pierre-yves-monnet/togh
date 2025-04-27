/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.entity.LoginLogEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.repository.LoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/* ******************************************************************************** */
/*                                                                                  */
/* UnderAttackService, */
/*                                                                                  */
/* Manage any suspicious operation */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class UnderAttackService {
  private final Logger logger = Logger.getLogger(UnderAttackService.class.getName());
  @Autowired
  private LoginLogRepository loginLogRepository;

  /**
   * Suspicious and incorrect login
   *
   * @param loginStatus the login is suspicious
   */
  public void reportSuspiciousLogin(LoginService.LoginResult loginStatus) {
    // first, calculate the timeSlot
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:");
    String timeSlot = now.format(formatter) + ((now.getMinute() / 15) * 15);
    logger.info("Connection Email[" + loginStatus.email + "] googleId[" + loginStatus.googleId + "] Status[" + loginStatus.status + "] @ [" + timeSlot + "]");


    if (loginLogRepository.countByTimeSlot(timeSlot) > 100) {
      // we are under attack: Stop to log
      try {
        Thread.sleep(30000);
      } catch (Exception e) {
        // nothing to do here
      }
    }
    try {
      LoginLogEntity loginLogEntity = loginLogRepository.findByTimeSlot(timeSlot,
          loginStatus.email,
          loginStatus.googleId,
          loginStatus.ipAddress,
          loginStatus.status);

      if (loginLogEntity != null && loginLogEntity.getStatusConnection().equals(loginStatus.status)) {
        loginLogEntity.setNumberOfTentatives(loginLogEntity.getNumberOfTentatives() + 1);
      } else {
        loginLogEntity = new LoginLogEntity();
        loginLogEntity.setEmail(loginStatus.email);
        loginLogEntity.setGoogleId(loginStatus.googleId);
        loginLogEntity.setIpAddress(loginStatus.ipAddress);
        loginLogEntity.setTimeSlot(timeSlot);
        loginLogEntity.setNumberOfTentatives(1);
        loginLogEntity.setStatusConnection(loginStatus.status);
        loginLogEntity.setExplanation(loginStatus.explanation);
      }

      loginLogRepository.save(loginLogEntity);
    } catch (Exception e) {
      logger.severe("Can't save loginLog " + e);
    }
  }

  /**
   * Report an incorrect access
   *
   * @param toghUser      the user who do the action. May be null if nobody is connected (reason sould be NOT_CONNECTED or NOT_AN_ADMINISTRATOR
   * @param urlOrFunction Url or function to access
   * @param reason        reason
   */
  public void reportNotAutorizedAction(ToghUserEntity toghUser, String urlOrFunction, NOT_AUTHORIZED_REASON reason) {
    // to be implemented
  }


  public enum NOT_AUTHORIZED_REASON {NOT_CONNECTED, NOT_AN_ADMINISTRATOR, CONFIDENTIAL_ACCESS}


}

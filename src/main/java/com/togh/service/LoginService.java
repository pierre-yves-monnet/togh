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
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.PrivilegeUserEnum;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.entity.ToghUserEntity.StatusUserEnum;
import com.togh.entity.ToghUserEntity.TypePictureEnum;
import com.togh.entity.ToghUserLostPasswordEntity;
import com.togh.entity.ToghUserLostPasswordEntity.StatusProcessEnum;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.repository.ToghUserLostPasswordRepository;
import com.togh.restcontroller.RestHttpConstant;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.serialization.ToghUserSerializer;
import com.togh.service.MonitorService.Chrono;
import com.togh.service.NotifyService.NotificationStatus;
import com.togh.service.ToghUserService.SearchUsersResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Logger;

/* ******************************************************************************** */
/*                                                                                  */
/* LoginService, */
/*                                                                                  */
/* Manage the login service part */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class LoginService {


  private static final LogEvent eventUnknownId = new LogEvent(LoginService.class.getName(), 1, Level.APPLICATIONERROR, "Unknown user", "There is no user behind this ID", "Operation can't be done", "Check the ID");
  private static final LogEvent eventUserDisconnected = new LogEvent(LoginService.class.getName(), 2, Level.SUCCESS, "User disconnected", "User disconnected with success");
  private static final LogEvent eventCantSaveLostPassword = new LogEvent(LoginService.class.getName(), 3, Level.ERROR, "Can't save lost password", "The data 'lostPassword' can't be save in the database", "Procedure to reset the password failed", "check Exception ");
  private static final LogEvent eventEmailResetPasswordFailed = new LogEvent(LoginService.class.getName(), 4, Level.ERROR, "Impossible to send the reset password email", "The email can't be send", "Procedure to reset the password failed", "check Exception ");

  private static final String OPERATION_V_NOT_EXIST = "NotExist";
  private static final String OPERATION_V_NOT_REGISTERED_ON_PORTAL = "NotRegisteredOnPortal";
  private static final String OPERATION_V_USER_BLOCKED_OR_DISABLED = "UserBlockedOrDisabled";
  private static final String OPERATION_V_BAD_PASSWORD = "BadPassword";
  private static final String OPERATION_CONNECT_USER_NO_VERIFICATION = "ConnectUserNoVerification";
  private final static int delayMinutesDisconnectInactiveUser = 30;
  private final static String LOG_HEADER = LoginService.class.getSimpleName() + ": ";
  /* -------------------------------------------------------------------- */
  /*                                                                      */
  /* Connect / disconnect / IsConnected */
  /*                                                                      */
  /* -------------------------------------------------------------------- */
  private final Random random = new Random();
  private final Logger logger = Logger.getLogger(LoginService.class.getName());
  private final Map<String, UserConnected> cacheUserConnected = new HashMap<>();
  @Autowired
  private ToghUserLostPasswordRepository toghUserLostPasswordRepository;
  @Autowired
  private NotifyService notifyService;
  @Autowired
  private MonitorService monitorService;
  @Autowired
  private ToghUserService toghUserService;
  @Autowired
  private FactorySerializer factorySerializer;
  @Autowired
  private FactoryUpdateGrantor factoryUpdateGrantor;
  @Autowired
  private StatsService statsService;
  @Autowired
  private UnderAttackService underAttackService;

  /**
   * @param emailOrName email or the name to connect
   * @param password    password
   * @return a LoginResult status
   */
  public LoginResult connectWithEmail(String emailOrName, String password, String ipAddress) {
    LoginResult loginStatus = new LoginResult();
    loginStatus.email = emailOrName;
    loginStatus.ipAddress = ipAddress;
    Chrono chronoConnection = monitorService.startOperation("ConnectUserWithEmail");

    ToghUserEntity toghUserEntity = toghUserService.findToConnect(emailOrName);
    if (toghUserEntity == null) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_NOT_EXIST);
      loginStatus.status = LoginStatus.UNKNOWUSER;
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    // Special case: an invited user with a password.... this is not normal.
    // When the user register, it set a password, and it must move to Portal. Let's catch that
    if (SourceUserEnum.INVITED.equals(toghUserEntity.getSource())
        && toghUserEntity.getPassword() != null
        && toghUserEntity.getPassword().length() > 0) {
      toghUserEntity.setSource(SourceUserEnum.PORTAL);
      toghUserService.saveUser(toghUserEntity);
    }

    // this user must be registered on the portal
    if (!(SourceUserEnum.PORTAL.equals(toghUserEntity.getSource())
        || SourceUserEnum.SYSTEM.equals(toghUserEntity.getSource()))) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_NOT_REGISTERED_ON_PORTAL);
      loginStatus.status = LoginStatus.NOTREGISTERED;
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    // password inactif or block: remove it
    if (!StatusUserEnum.ACTIF.equals(toghUserEntity.getStatusUser())) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_USER_BLOCKED_OR_DISABLED);
      loginStatus.status = LoginStatus.BLOCKED;
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    // check the password
    String passwordEncrypted = ToghUserService.encryptPassword(password);
    if (!toghUserEntity.checkPassword(passwordEncrypted)) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_BAD_PASSWORD);
      loginStatus.status = LoginStatus.BADPASSWORD; // don't say that the user exists...
      loginStatus.explanation = "Password given has (" + password.length() + ") char, original password is " + toghUserEntity.getLengthPassword();
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    loginStatus.userConnected = toghUserEntity;
    loginStatus.connectionToken = connectUser(toghUserEntity, false);
    loginStatus.isConnected = true;
    monitorService.endOperation(chronoConnection);
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;
  }


  public LoginResult ghostConnect(Long ghostUserId, String ipAddress) {
    LoginResult loginStatus = new LoginResult();
    Chrono chronoConnection = monitorService.startOperation("GhostConnectUserWithEmail");
    ToghUserEntity toghUserEntity = toghUserService.getUserFromId(ghostUserId);

    if (toghUserEntity == null) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_NOT_EXIST);
      loginStatus.status = LoginStatus.UNKNOWUSER;
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }

    // this user must be registered on the portal
    if (!(SourceUserEnum.PORTAL.equals(toghUserEntity.getSource())
        || SourceUserEnum.SYSTEM.equals(toghUserEntity.getSource()))) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_NOT_REGISTERED_ON_PORTAL);
      loginStatus.status = LoginStatus.NOTREGISTERED;
      return loginStatus;
    }

    // check the password
    loginStatus.email = toghUserEntity.getEmail();
    loginStatus.ipAddress = ipAddress;

    loginStatus.userConnected = toghUserEntity;
    loginStatus.connectionToken = connectUser(toghUserEntity, true);
    loginStatus.isConnected = true;
    monitorService.endOperation(chronoConnection);
    return loginStatus;
  }

  /**
   * Internal connection: we trust who call it and then we connect the user
   */
  public LoginResult connectNoVerification(String email) {
    LoginResult loginStatus = new LoginResult();
    loginStatus.email = email;

    Chrono chronoConnection = monitorService.startOperation(OPERATION_CONNECT_USER_NO_VERIFICATION);

    Optional<ToghUserEntity> endUserEntity = toghUserService.getUserFromEmail(email);
    if (endUserEntity.isEmpty()) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_NOT_EXIST);
      return loginStatus;
    }
    loginStatus.userConnected = endUserEntity.get();
    loginStatus.connectionToken = connectUser(endUserEntity.get(), false);
    loginStatus.isConnected = true;
    loginStatus.status = LoginStatus.OK;
    monitorService.endOperation(chronoConnection);
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;

  }

  /**
   * SSO connection: just give a name, and should connect Internal connection: we trust who call it, and then we connect the user
   */
  public LoginResult connectSSO(String email, boolean isGoogle, String ipAddress) {
    LoginResult loginStatus = new LoginResult();
    loginStatus.email = email;
    loginStatus.ipAddress = ipAddress;
    Chrono chronoConnection = monitorService.startOperation(OPERATION_CONNECT_USER_NO_VERIFICATION);

    Optional<ToghUserEntity> toghUserEntity = toghUserService.getUserFromEmail(email);
    if (toghUserEntity.isEmpty()) {
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_NOT_EXIST);
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    // not correct is the source is not the correct one
    if (isGoogle && (!toghUserEntity.get().getSource().equals(SourceUserEnum.GOOGLE))) {
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }

    loginStatus.userConnected = toghUserEntity.get();
    loginStatus.connectionToken = connectUser(toghUserEntity.get(), false);
    loginStatus.isConnected = true;
    monitorService.endOperation(chronoConnection);
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;

  }

  // create a new user
  public LoginResult registerNewUser(String email,
                                     String firstName,
                                     String lastName,
                                     String password,
                                     SourceUserEnum sourceUser,
                                     TypePictureEnum typePicture,
                                     String picture) {
    LoginResult loginStatus = new LoginResult();
    loginStatus.email = email;
    Optional<ToghUserEntity> toghUserEntityOptional = toghUserService.getUserFromEmail(email);
    ToghUserEntity toghUserEntity;
    if (toghUserEntityOptional.isPresent()) {
      toghUserEntity = toghUserEntityOptional.get();

      // user already exist: so, time to save it password
      if (SourceUserEnum.INVITED.equals(toghUserEntity.getSource())) {
        // Ok, this is the first time the user join!

        toghUserService.setPassword(toghUserEntity, password);

        toghUserEntity.setFirstName(firstName);
        toghUserEntity.setLastName(lastName);
        toghUserEntity.calculateName();
        toghUserEntity.setSource(SourceUserEnum.PORTAL);
        toghUserService.saveUser(toghUserEntity);
        toghUserEntity.setTypePicture(typePicture);
        toghUserEntity.setPicture(picture);
        toghUserEntity.setStatusUser(StatusUserEnum.ACTIF);
        toghUserEntity.setShowTipsUser(true);
        toghUserEntity.setShowTakeATour(true);
        loginStatus.status = LoginStatus.OK;
      } else {
        // Hum, someone try to access an existing user via the registration ? Well try...
        // Or maybe this is just a duplicate coincidence
        loginStatus.status = LoginStatus.ALREADYEXISTUSER;
        underAttackService.reportSuspiciousLogin(loginStatus);
        return loginStatus;
      }
      underAttackService.reportSuspiciousLogin(loginStatus);
    } else {
      // encrypt the password now
      String passwordEncrypted = ToghUserService.encryptPassword(password);
      toghUserEntity = toghUserService.createNewUser(firstName, lastName, passwordEncrypted, email, sourceUser);

      toghUserEntity.setTypePicture(typePicture);
      toghUserEntity.setPicture(picture);
    }

    // save modifications now
    try {
      toghUserService.saveUser(toghUserEntity);
      loginStatus.status = LoginStatus.OK;
    } catch (Exception e) {
      logger.severe(LOG_HEADER + "Can't create new user: " + e);
      loginStatus.status = LoginStatus.SERVERISSUE;
    }
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;

  }

  /**
   * Do the connection operation
   *
   * @param toghUserEntity  togh user to connect
   * @param ghostConnection may be treu, then we don't want to update the statistics
   * @return a connectionStamp
   */
  private String connectUser(ToghUserEntity toghUserEntity, boolean ghostConnection) {
    // Generate a ConnectionStamp
    String randomStamp = String.valueOf(System.currentTimeMillis()) + random.nextInt(100000);

    toghUserEntity.setConnectionStamp(randomStamp);
    toghUserEntity.setConnectionTime(LocalDateTime.now(ZoneOffset.UTC));
    toghUserEntity.setConnectionLastActivity(LocalDateTime.now(ZoneOffset.UTC));

    toghUserService.saveUser(toghUserEntity);

    if (!ghostConnection)
      statsService.registerLogin();

    // keep in the cache to be more efficient
    UserConnected userConnected = new UserConnected();
    userConnected.toghUserEntity = toghUserEntity;
    cacheUserConnected.put(randomStamp, userConnected);
    return randomStamp;
  }

  /**
   * check that the user can access this RestAdminTranslator
   *
   * @param connectionStamp stamp to identify the connection
   * @return the ToghUser
   * @throws ResponseStatusException bob
   */
  public ToghUserEntity isAdministratorConnected(String connectionStamp) throws ResponseStatusException {
    ToghUserEntity toghUser = isConnected(connectionStamp);

    if (toghUser == null)
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

    // check if the user is an administrator
    if (!isAdministrator(toghUser)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTANADMINISTRATOR);
    }
    return toghUser;
  }

  /**
   * Sent the email. The message to the user is added, because it is translated by the interface (which contains the dictionary)
   *
   * @param email email to identify the user
   * @return the login status
   */
  public LoginResult lostMyPassword(String email) {
    LoginResult loginStatus = new LoginResult();

    // find the user from the email
    Optional<ToghUserEntity> toghUserEntity = toghUserService.getUserFromEmail(email);
    if (toghUserEntity.isEmpty()) {
      loginStatus.status = LoginStatus.UNKNOWUSER;
      return loginStatus;
    }
    // Generate a UUID
    UUID uuid = UUID.randomUUID();
    ToghUserLostPasswordEntity lostPasswordEntity = new ToghUserLostPasswordEntity();
    lostPasswordEntity.setUuid(uuid.toString());
    lostPasswordEntity.setUser(toghUserEntity.get());
    LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
    localDateTime = localDateTime.minusHours(-2);
    lostPasswordEntity.setDateValidity(localDateTime);
    lostPasswordEntity.setStatusProcess(StatusProcessEnum.PREPARATION);

    try {
      toghUserLostPasswordRepository.save(lostPasswordEntity);
    } catch (Exception ex) {
      loginStatus.status = LoginStatus.SERVERISSUE;
      loginStatus.listEvents.add(new LogEvent(eventCantSaveLostPassword, ex, "User[" + toghUserEntity.get().getLogLabel() + "]"));
      monitorService.registerErrorEvents(loginStatus.listEvents);
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }


    // send the email now
    NotificationStatus notificationStatus = notifyService.sendLostPasswordEmail(toghUserEntity.get(), uuid.toString());

    if (notificationStatus.isCorrect()) {
      lostPasswordEntity.setStatusProcess(StatusProcessEnum.EMAILSENT);
    } else if (notificationStatus.hasServerIssue()) {
      lostPasswordEntity.setStatusProcess(StatusProcessEnum.SERVERISSUE);
      loginStatus.status = LoginStatus.SERVERISSUE;
      loginStatus.listEvents.add(new LogEvent(eventEmailResetPasswordFailed, "User[" + toghUserEntity.get().getLogLabel() + "]"));
    } else {
      lostPasswordEntity.setStatusProcess(StatusProcessEnum.EMAILINERROR);
      loginStatus.status = LoginStatus.BADEMAIL;
      loginStatus.listEvents.add(new LogEvent(eventEmailResetPasswordFailed, "User[" + toghUserEntity.get().getLogLabel() + "]"));

    }

    try {
      toghUserLostPasswordRepository.save(lostPasswordEntity);
    } catch (Exception ex) {
      loginStatus.status = LoginStatus.SERVERISSUE;
      loginStatus.listEvents.add(new LogEvent(eventCantSaveLostPassword, ex, "User[" + toghUserEntity.get().getLogLabel() + "]"));
      monitorService.registerErrorEvents(loginStatus.listEvents);
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }

    monitorService.registerErrorEvents(loginStatus.listEvents);
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;
  }

  public LoginResult changePasswordAndConnect(String uuid, String password) {
    LoginResult loginStatus = new LoginResult();
    Chrono chronoConnection = monitorService.startOperation("retrieveFromUUID");

    List<ToghUserLostPasswordEntity> listUUID = toghUserLostPasswordRepository.findByUUID(uuid);
    if (listUUID.isEmpty()) {
      loginStatus.status = LoginStatus.UNKNOWUSER;
      return loginStatus;
    }
    ToghUserEntity toghUserEntity = listUUID.get(0).getUser();
    // password inactif or block: remove it
    if (!StatusUserEnum.ACTIF.equals(toghUserEntity.getStatusUser())) {
      loginStatus.status = LoginStatus.UNKNOWUSER;
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_USER_BLOCKED_OR_DISABLED);
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    // search the user now
    loginStatus.userConnected = toghUserEntity;
    // change the password now
    toghUserService.setPassword(toghUserEntity, password);
    toghUserService.saveUser(toghUserEntity);

    // now connect
    loginStatus.userConnected = toghUserEntity;
    loginStatus.connectionToken = connectUser(toghUserEntity, false);
    loginStatus.isConnected = true;
    monitorService.endOperation(chronoConnection);
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;

  }

  /**
   * User is updated (picture change, name change...) : just refresh this information
   *
   * @param toghUser togh user
   */
  public void userIsUpdated(ToghUserEntity toghUser) {
    for (UserConnected userConnected : cacheUserConnected.values()) {
      if (userConnected.toghUserEntity.getId().equals(toghUser.getId()))
        userConnected.toghUserEntity = toghUser;
    }
  }

  /**
   * Is Connected
   *
   * @param connectionStamp to identify the connection
   * @return a user, or null if nobody is connected
   */
  public ToghUserEntity isConnected(String connectionStamp) {
    LocalDateTime timeCheck = LocalDateTime.now(ZoneOffset.UTC);
    // is the user is in the cache AND the last ping was less than 2 mn ? If you, we trust the cache.
    UserConnected userConnected = cacheUserConnected.get(connectionStamp);
    if (userConnected != null) {
      userConnected.lastPing = LocalDateTime.now(ZoneOffset.UTC);
      Duration duration = Duration.between(userConnected.lastCheck, userConnected.lastPing);
      if (duration.toMinutes() < 1)
        return userConnected.toghUserEntity;
    }

    // more than 2 mn or not in the cache? Get it from the database
    ToghUserEntity endUserEntity = toghUserService.getUserFromConnectionStamp(connectionStamp);
    // if not exist ==> It's disconnected in fact !
    if (endUserEntity == null)
      return null;
    // maybe the last check was too old in the database too?
    LocalDateTime lastActivity = endUserEntity.getConnectionLastActivity();
    if (lastActivity == null || Duration.between(lastActivity, timeCheck).toMinutes() > delayMinutesDisconnectInactiveUser) {
      // incoherent, or too old disconnect it
      endUserEntity.setConnectionStamp(null);
      cacheUserConnected.remove(connectionStamp);
    }
    // we are all good, update the last activity then
    endUserEntity.setConnectionLastActivity(timeCheck);

    // the cache may have no trace of this user ? Case in a cluster, the user just arrive on this server.
    if (userConnected == null) {
      userConnected = new UserConnected();
      userConnected.toghUserEntity = endUserEntity;
    }
    userConnected.lastCheck = timeCheck;
    cacheUserConnected.put(connectionStamp, userConnected);
    return userConnected.toghUserEntity;
  }

  /**
   * Is this user an admin ?
   *
   * @param toghUser togh user
   * @return true if the user is an administrator
   */
  public boolean isAdministrator(ToghUserEntity toghUser) {
    if (toghUser == null)
      return false;
    return PrivilegeUserEnum.ADMIN.equals(toghUser.getPrivilegeUser());
  }

  /**
   * Change the password
   *
   * @param toghUser Togh user to change the password
   * @param password password to change
   * @return login status
   */
  public LoginResult changePassword(ToghUserEntity toghUser,
                                    String password) {
    LoginResult loginStatus = new LoginResult();
    Chrono chronoConnection = monitorService.startOperation("changePassword");


    // password inactif or block: remove it
    if (!StatusUserEnum.ACTIF.equals(toghUser.getStatusUser())) {
      loginStatus.status = LoginStatus.UNKNOWUSER;
      monitorService.endOperationWithStatus(chronoConnection, OPERATION_V_USER_BLOCKED_OR_DISABLED);
      underAttackService.reportSuspiciousLogin(loginStatus);
      return loginStatus;
    }
    // search the user now
    loginStatus.userConnected = toghUser;
    // change the password now
    toghUserService.setPassword(toghUser, password);
    toghUserService.saveUser(toghUser);

    loginStatus.isConnected = true;
    monitorService.endOperation(chronoConnection);
    underAttackService.reportSuspiciousLogin(loginStatus);
    return loginStatus;
  }

  /**
   * Disconnect user based on the connectionStamp
   *
   * @param connectionStamp Connection Stamp to retrieve the connection
   */
  public void disconnectUser(String connectionStamp) {
    UserConnected userConnected = cacheUserConnected.get(connectionStamp);
    ToghUserEntity toghUserEntity;

    if (userConnected == null) {
      // search in the database
      toghUserEntity = toghUserService.getUserFromConnectionStamp(connectionStamp);
    } else {
      toghUserEntity = userConnected.toghUserEntity;
    }
    if (toghUserEntity == null)
      return; // already disconnected, or not exist

    cacheUserConnected.remove(connectionStamp);

    toghUserEntity.setConnectionStamp(null);
    toghUserService.saveUser(toghUserEntity);

    cacheUserConnected.remove(connectionStamp);
  }

  /**
   * Search, then disconnect all users with no activity for delayMinutesDisconnectInactiveUser, a constant
   */
  public void disconnectInactiveUsers() {
    LocalDateTime timeCheck = LocalDateTime.now(ZoneOffset.UTC);
    timeCheck = timeCheck.minusMinutes(delayMinutesDisconnectInactiveUser);
    SearchUsersResult searchUsersResult = toghUserService.searchConnectedUsersNoActivity(timeCheck, 0, 1000);
    logger.info(String.format("%s disconnectInactiveUsers found %d users", LOG_HEADER, searchUsersResult.listUsers.size()));
    for (ToghUserEntity toghUserEntity : searchUsersResult.listUsers) {
      logger.info(LOG_HEADER + "disconnectInactiveUsers Disconnect[" + toghUserEntity.getLogLabel() + "]");
      toghUserEntity.setConnectionStamp(null);
      toghUserService.saveUser(toghUserEntity);
    }
  }

  /**
   * disconnect a user
   *
   * @param userId userId to disconnect
   * @return Operation Status
   */
  public OperationLoginUser disconnectUser(long userId) {
    OperationLoginUser operationUser = new OperationLoginUser();

    operationUser.toghUserEntity = toghUserService.getUserFromId(userId);

    if (operationUser.toghUserEntity == null) {
      operationUser.listLogEvents.add(new LogEvent(eventUnknownId, "Id[" + userId + "]"));
      return operationUser;
    }
    cacheUserConnected.remove(operationUser.toghUserEntity.getConnectionStamp());
    operationUser.toghUserEntity.setConnectionStamp(null);
    toghUserService.saveUser(operationUser.toghUserEntity);
    operationUser.listLogEvents.add(eventUserDisconnected);
    return operationUser;

  }

  /**
   * When the password is lost, a UUID is generated. Then, the page "change my password" is acceded. Form the UUID, information are send back
   *
   * @param uuid Uuid to retrieve the connection
   * @return Login status
   */
  public LoginResult getFromUUID(String uuid) {
    LoginResult loginStatus = new LoginResult();
    Chrono chronoConnection = monitorService.startOperation("retrieveFromUUID");

    List<ToghUserLostPasswordEntity> listUUID = toghUserLostPasswordRepository.findByUUID(uuid);
    if (listUUID.isEmpty()) {
      loginStatus.status = LoginStatus.UNKNOWUSER;
      return loginStatus;
    }
    // search the user now
    loginStatus.userConnected = listUUID.get(0).getUser();
    loginStatus.status = LoginStatus.OK;

    monitorService.endOperation(chronoConnection);

    return loginStatus;
  }

  // --------------------------------------------------------------
  //
  // LostMyPassword
  //
  // --------------------------------------------------------------

  /**
   * listEvents are not sent back, it's a server information
   *
   * @return Map of login information
   */
  public Map<String, Object> getLoginResultMap(LoginResult loginResult) {
    Map<String, Object> map = new HashMap<>();
    map.put("isConnected", loginResult.isConnected);
    map.put("token", loginResult.connectionToken);
    if (loginResult.userConnected != null) {
      ToghUserSerializer toghUserSerializer = (ToghUserSerializer) factorySerializer.getFromEntity(loginResult.userConnected);
      SerializerOptions serializerOptions = new SerializerOptions(loginResult.userConnected, 0L, SerializerOptions.ContextAccess.MYPROFILE);

      map.put("user", toghUserSerializer.getMap(loginResult.userConnected, null,
          serializerOptions,
          factorySerializer,
          factoryUpdateGrantor));
    }

    return map;
  }

  public enum LoginStatus {OK, BADEMAIL, SERVERISSUE, UNKNOWUSER, BADPASSWORD, ALREADYEXISTUSER, NOTREGISTERED, BLOCKED}

  /**
   * LoginResult class
   */
  public static class LoginResult {

    public boolean isConnected = false;
    public LoginStatus status = LoginStatus.OK;
    public ToghUserEntity userConnected;
    public String connectionToken = null;

    public String email = "";
    public String googleId = "";
    public String ipAddress = "null";
    public String explanation = null;

    public List<LogEvent> listEvents = new ArrayList<>();

    @Autowired
    private FactorySerializer factorySerializer;

    @Autowired
    private FactoryUpdateGrantor factoryUpdateGrantor;


  }

  /**
   * class of status
   */
  public static class OperationLoginUser {

    public List<LogEvent> listLogEvents = new ArrayList<>();
    public ToghUserEntity toghUserEntity = null;
  }

  /**
   * User Connected information
   */
  private static class UserConnected {

    public ToghUserEntity toghUserEntity;
    public LocalDateTime lastPing = LocalDateTime.now(ZoneOffset.UTC);
    public LocalDateTime lastCheck = LocalDateTime.now(ZoneOffset.UTC);

  }

}

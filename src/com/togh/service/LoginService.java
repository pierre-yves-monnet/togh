/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login service */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.PrivilegeUserEnum;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.entity.ToghUserEntity.StatusUserEnum;
import com.togh.entity.ToghUserEntity.TypePictureEnum;
import com.togh.restcontroller.RestHttpConstant;
import com.togh.service.MonitorService.Chrono;
import com.togh.service.ToghUserService.SearchUsersResult;


/* ******************************************************************************** */
/*                                                                                  */
/*  LoginService,                                                                 */
/*                                                                                  */
/*  Manage the login service part    */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class LoginService {
 
    @Autowired
    FactoryService factoryService;

    @Autowired 
    ToghUserService toghUserService;
    
    @Autowired 
    StatsService statsService;
    
    private static final LogEvent eventUnknowId = new LogEvent(LoginService.class.getName(), 1, Level.APPLICATIONERROR, "Unknow user", "There is no user behind this ID", "Operation can't be done", "Check the ID");
    private static final LogEvent eventUserDisconnected = new LogEvent(LoginService.class.getName(), 2, Level.SUCCESS, "User disconnected", "User disconnected with success");

    private Logger logger = Logger.getLogger(LoginService.class.getName());
    private final static String logHeader ="LoginService:";
    
   private int delayMinutesDisconnectInactiveUser=30;
    
    public static class LoginStatus {
        public boolean isConnected=false;
        public boolean isCorrect=false;
        public ToghUserEntity userConnected;
        public String connectionToken = null;
        
        public Map<String,Object> getMap(){
            Map<String,Object> map = new HashMap<>();
            map.put("isConnected", isConnected);
            map.put("token", connectionToken);
            if (userConnected!=null)
                map.put("user", userConnected);
            return map;
        }
    }
    
    /**
     * 
     * @param email
     * @param password
     * @return
     */
    public LoginStatus connectWithEmail(String emailOrName, String password) {
        LoginStatus loginStatus = new LoginStatus();
        MonitorService monitorService = factoryService.getMonitorService();
        Chrono chronoConnection = monitorService.startOperation("ConnectUserWithEmail");
        
        ToghUserEntity toghUserEntity = toghUserService.findToConnect( emailOrName );
        if (toghUserEntity==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        // this user must be registered on the portal
        if (! (SourceUserEnum.PORTAL.equals( toghUserEntity.getSource()) 
                || SourceUserEnum.SYSTEM.equals( toghUserEntity.getSource() ))) {
            monitorService.endOperationWithStatus(chronoConnection, "NotRegisteredOnPortal");
            return loginStatus;
        }
        // password inactif or block: remove it
        if (! StatusUserEnum.ACTIF.equals(toghUserEntity.getStatusUser()))  {
            monitorService.endOperationWithStatus(chronoConnection, "UserBlockedOrDisabled");
            return loginStatus;
        }
        // check the password
        if (! toghUserEntity.checkPassword(password)) {
            monitorService.endOperationWithStatus(chronoConnection, "BadPassword");
            return loginStatus;
        }
        loginStatus.isConnected=true;
        loginStatus.userConnected = toghUserEntity;
        loginStatus.connectionToken = connectUser( toghUserEntity );
        monitorService.endOperation(chronoConnection);
        return loginStatus;
    }
    
    
    /**
     * Internal connection: we trust who call it and then we connect the user
     */
    public LoginStatus connectNoVerification(String email) {
        LoginStatus loginStatus = new LoginStatus();
        MonitorService monitorService = factoryService.getMonitorService();

        Chrono chronoConnection = monitorService.startOperation("ConnectUserNoVerification");
        
        ToghUserEntity endUserEntity =  toghUserService.getFromEmail( email );
        if (endUserEntity==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUserEntity;        
        loginStatus.connectionToken = connectUser( endUserEntity );
        loginStatus.isCorrect=true;
        monitorService.endOperation(chronoConnection);
        return loginStatus;
        
    }
    /**
     * SSO connection: just give a name, and should connect Internal connection: we trust who call it and then we connect the user
     */
    public LoginStatus connectSSO(String email, boolean isGoogle) {
        LoginStatus loginStatus = new LoginStatus();
        MonitorService monitorService = factoryService.getMonitorService();

        Chrono chronoConnection = monitorService.startOperation("ConnectUserNoVerification");
        
        ToghUserEntity endUserEntity = toghUserService.getFromEmail(email);
        if (endUserEntity==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        // not correct is the source is not the correct one
        if (isGoogle && (! endUserEntity.getSource().equals(SourceUserEnum.GOOGLE)))
            return loginStatus;
        
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUserEntity;
        loginStatus.connectionToken = connectUser( endUserEntity );
        monitorService.endOperation(chronoConnection);
        return loginStatus;
        
    }
    
    // create a new user
    public LoginStatus registerNewUser(String email, String firstName, String lastName, String password, SourceUserEnum sourceUser,
            TypePictureEnum typePicture,
            String picture)  {
        LoginStatus loginStatus = new LoginStatus();
        ToghUserEntity toghUserEntity = toghUserService.getFromEmail( email );
        if (toghUserEntity !=null) {
            return loginStatus;
        }
        toghUserEntity = ToghUserEntity.getNewUser(firstName, lastName, email, password, sourceUser);
        toghUserEntity.setTypePicture( typePicture);
        toghUserEntity.setPicture(picture);
        try {
            toghUserService.saveUser( toghUserEntity );
            loginStatus.isCorrect=true;
        } catch(Exception e) {
            logger.severe(logHeader+"Can't create new user: "+e.toString());
        }
        return loginStatus;

    }
    

    
    
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Connect / disconnect / IsConnected */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    Random random = new Random();

    private class UserConnected {
        public ToghUserEntity toghUserEntity;
        public LocalDateTime lastPing = LocalDateTime.now(ZoneOffset.UTC);
        public LocalDateTime lastCheck = LocalDateTime.now(ZoneOffset.UTC);

    }
    
    private Map<String,UserConnected> cacheUserConnected  = new HashMap<>();
    private String connectUser( ToghUserEntity toghUserEntity ) {
        // Generate a ConnectionStamp
        // MonitorService monitorService = factoryService.getMonitorService();

        String randomStamp = String.valueOf(System.currentTimeMillis())+ String.valueOf( random.nextInt() );
        
        toghUserEntity.setConnectionStamp( randomStamp );
        toghUserEntity.setConnectionTime( LocalDateTime.now(ZoneOffset.UTC));
        toghUserEntity.setConnectionLastActivity( LocalDateTime.now(ZoneOffset.UTC));
       
        toghUserService.saveUser(toghUserEntity);
        
        statsService.registerLogin();
        
        // keep in the cache to be more efficient
        UserConnected userConnected = new UserConnected();
        userConnected.toghUserEntity = toghUserEntity;
        cacheUserConnected.put( randomStamp, userConnected);
        return randomStamp;
    }
    
    /**
     * User is updated (picture change, name change...) : just refresh this information
     * @param toghUserEntity
     */
    public void userIsUpdated( ToghUserEntity toghUserEntity) {
        for (UserConnected userConnected: cacheUserConnected.values()) {
            if (userConnected.toghUserEntity.getId() == toghUserEntity.getId())
                userConnected.toghUserEntity = toghUserEntity;
        }
    }
    
    /**
     * 
     * @param connectionStamp
     * @return a user, or null if nobody is connected
     */
    public ToghUserEntity isConnected( String connectionStamp) {
        LocalDateTime timeCheck =LocalDateTime.now( ZoneOffset.UTC);
        // is the user is in the cache AND the last ping was less than 2 mn ? If you, we trust the cache.
        UserConnected userConnected = cacheUserConnected.get( connectionStamp);
        if (userConnected != null) {
            userConnected.lastPing = LocalDateTime.now( ZoneOffset.UTC );
            Duration duration = Duration.between(userConnected.lastCheck,userConnected.lastPing );
            if (duration.toMinutes() < 1) 
                return userConnected.toghUserEntity;
        }

        // more than 2 mn or not in the cache? Get it from the database
        ToghUserEntity endUserEntity = toghUserService.getUserFromConnectionStamp( connectionStamp );
        // if not exist ==> It's disconnected in fact !
        if (endUserEntity == null) 
            return null;
        // maybe the last check was too old in the database too?
        LocalDateTime lastActivity = endUserEntity.getConnectionLastActivity();
        if (lastActivity==null || Duration.between(lastActivity,timeCheck).toMinutes() > delayMinutesDisconnectInactiveUser) {
            // incoherent, or too old disconnect it
            endUserEntity.setConnectionStamp(null);
            cacheUserConnected.remove(connectionStamp);        
        }
        // we are all good, update the last activity then
        endUserEntity.setConnectionLastActivity( timeCheck );
        
        // the cache may have no trace of this user ? Case in a cluster, the user just arrive on this server.
        if (userConnected==null ) {
            userConnected = new UserConnected(); 
            userConnected.toghUserEntity = endUserEntity;
        }
        userConnected.lastCheck = timeCheck ;
        cacheUserConnected.put(connectionStamp, userConnected);
        return userConnected.toghUserEntity;                
    }
    
    /**
     * Is this user an admin ?
     * @param userId
     * @return
     */
    public boolean isAdministrator(ToghUserEntity toghUser ) {
        if (toghUser == null)
            return false;
        return PrivilegeUserEnum.ADMIN.equals(toghUser.getPrivilegeUser());
    }
    
    /**
     * check that the user can access this RestAdminTranslator
     * @param connectionStamp
     * @return
     * @throws ResponseStatusException
     * bob
     */
    public ToghUserEntity isAdministratorConnected( String connectionStamp ) throws ResponseStatusException {
        ToghUserEntity toghUser = isConnected(connectionStamp);

        if (toghUser == null)
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        // check if the user is an administrator
        if  ( ! isAdministrator( toghUser)) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTANADMINISTRATOR);
        }
        return toghUser;
    }
    /**
     * Disconnect
     * @param connectionStamp
     */
    public void disconnectUser(String connectionStamp ) {
        UserConnected userConnected = cacheUserConnected.get( connectionStamp);
        ToghUserEntity toghUserEntity = null;

        if (userConnected==null) {
            // search in the database
            toghUserEntity = toghUserService.getUserFromConnectionStamp( connectionStamp );
        }
        else {
            toghUserEntity = userConnected.toghUserEntity;
        }
        if (toghUserEntity == null)
            return; // already disconnected, or not exist

        cacheUserConnected.remove( connectionStamp );
        
        toghUserEntity.setConnectionStamp(null);
        toghUserService.saveUser(toghUserEntity);

        cacheUserConnected.remove(connectionStamp);
    }

    /**
     * Search, then disconnect all users with no activity for delayMinutesDisconnectInactiveUser, a constant
     */
    public void disconnectInactiveUsers() {
        LocalDateTime timeCheck = LocalDateTime.now(ZoneOffset.UTC);
        timeCheck = timeCheck.minusMinutes( delayMinutesDisconnectInactiveUser );
        SearchUsersResult searchUsersResult = toghUserService.searchConnectedUsersNoActivity( timeCheck,0,1000 );
        logger.info(logHeader+"disconnectInactiveUsers found "+ searchUsersResult.listUsers.size());
        for (ToghUserEntity toghUserEntity : searchUsersResult.listUsers) {
            logger.info(logHeader+"disconnectInactiveUsers Disconnect["+ toghUserEntity.getLabel()+"]");
            toghUserEntity.setConnectionStamp(null);
            toghUserService.saveUser( toghUserEntity );
        }
    }
    
    /**
     * Disconnect explicitaly a user
     * @param userId
     * @return
     */
    public class OperationLoginUser{
        public List<LogEvent> listLogEvents = new ArrayList<>();
        public ToghUserEntity toghUserEntity= null;
    }
    public OperationLoginUser disconnectUser( long userId) {
        OperationLoginUser operationUser = new OperationLoginUser();

        operationUser.toghUserEntity =   toghUserService.getUserFromId(userId);

        if (operationUser.toghUserEntity == null) {
            operationUser.listLogEvents.add(new LogEvent(eventUnknowId, "Id[" + userId + "]"));
            return operationUser;
        }
        cacheUserConnected.remove( operationUser.toghUserEntity.getConnectionStamp() );
        operationUser.toghUserEntity.setConnectionStamp( null );
        toghUserService.saveUser( operationUser.toghUserEntity );
        operationUser.listLogEvents.add( eventUserDisconnected );
        return operationUser;

    }
}

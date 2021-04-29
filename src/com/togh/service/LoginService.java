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

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login service */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.PrivilegeUserEnum;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.entity.ToghUserEntity.StatusUserEnum;
import com.togh.restcontroller.RestHttpConstant;
import com.togh.service.MonitorService.Chrono;


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
    StatsService statsService;
    
    private Logger logger = Logger.getLogger(LoginService.class.getName());
    private final static String logHeader ="LoginService:";
    
   
    
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
        
        ToghUserEntity endUserEntity = factoryService.getToghUserService().findToConnect( emailOrName );
        if (endUserEntity==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        // this user must be registered on the portal
        if (! (SourceUserEnum.PORTAL.equals( endUserEntity.getSource()) 
                || SourceUserEnum.SYSTEM.equals( endUserEntity.getSource() ))) {
            monitorService.endOperationWithStatus(chronoConnection, "NotRegisteredOnPortal");
            return loginStatus;
        }
        // password inactif or block: remove it
        if (! StatusUserEnum.ACTIF.equals(endUserEntity.getStatusUser()))  {
            monitorService.endOperationWithStatus(chronoConnection, "UserBlockedOrDisabled");
            return loginStatus;
        }
        // check the password
        if (! endUserEntity.checkPassword(password)) {
            monitorService.endOperationWithStatus(chronoConnection, "BadPassword");
            return loginStatus;
        }
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUserEntity;
        loginStatus.connectionToken = connectUser( endUserEntity );
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
        
        ToghUserEntity endUserEntity =  factoryService.getToghUserService().getFromEmail( email );
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
        
        ToghUserEntity endUserEntity = factoryService.getToghUserService().getFromEmail(email);
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
    public LoginStatus registerNewUser(String email, String firstName, String lastName, String password, SourceUserEnum sourceUser)  {
        LoginStatus loginStatus = new LoginStatus();
        ToghUserEntity endUserEntity = factoryService.getToghUserService().getFromEmail( email );
        if (endUserEntity !=null) {
            return loginStatus;
        }
        endUserEntity = ToghUserEntity.getNewUser(firstName, lastName, email, password, sourceUser);
        
        try {
            factoryService.getToghUserService().saveUser( endUserEntity );
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
        public ToghUserEntity toghUser;
        public LocalDateTime lastPing = LocalDateTime.now(ZoneOffset.UTC);
        public LocalDateTime lastCheck = LocalDateTime.now(ZoneOffset.UTC);

    }
    
    private Map<String,UserConnected> cacheUserConnected  = new HashMap<>();
    private String connectUser( ToghUserEntity toghUser ) {
        // Generate a ConnectionStamp
        // MonitorService monitorService = factoryService.getMonitorService();

        String randomStamp = String.valueOf(System.currentTimeMillis())+ String.valueOf( random.nextInt() );
        
        toghUser.setConnectionStamp( randomStamp );
        toghUser.setConnectionTime( LocalDateTime.now(ZoneOffset.UTC));
        toghUser.setConnectionLastActivity( LocalDateTime.now(ZoneOffset.UTC));
       
        factoryService.getToghUserService().saveUser(toghUser);
        
        statsService.registerLogin();
        
        // keep in the cache to be more efficient
        UserConnected userConnected = new UserConnected();
        userConnected.toghUser = toghUser;
        cacheUserConnected.put( randomStamp, userConnected);
        return randomStamp;
    }
    
    /**
     * 
     * @param connectionStamp
     * @return a user, or null if nobody is connected
     */
    public ToghUserEntity isConnected( String connectionStamp) {
        LocalDateTime timeCheck = LocalDateTime.now();
        // is the user is in the cache AND the last ping was less than 2 mn ? If you, we trust the cache.
        UserConnected userConnected = cacheUserConnected.get( connectionStamp);
        if (userConnected != null) {
            userConnected.lastPing = LocalDateTime.now();
            Duration duration = Duration.between(userConnected.lastCheck, userConnected.lastPing);
            if (duration.toMinutes() < 2) 
                return userConnected.toghUser;
        }

        // more than 2 mn or not in the cache? Get it from the database
        ToghUserEntity endUserEntity = factoryService.getToghUserService().getUserFromConnectionStamp( connectionStamp );
        // if not exist ==> It's disconnected in fact !
        if (endUserEntity == null) 
            return null;
        // maybe the last check was too old in the database too?
        LocalDateTime lastActivity = endUserEntity.getConnectionLastActivity();
        if (lastActivity==null || Duration.between(lastActivity,timeCheck).toMinutes()>30) {
            // incoherent, or too old disconnect it
            endUserEntity.setConnectionStamp(null);
            cacheUserConnected.remove(connectionStamp);        
        }
        // we are all good, update the last activity then
        endUserEntity.setConnectionLastActivity( timeCheck );
        
        // the cache may have no trace of this user ? Case in a cluster, the user just arrive on this server.
        if (userConnected==null ) {
            userConnected = new UserConnected(); 
            userConnected.toghUser = endUserEntity;
        }
        userConnected.lastCheck = timeCheck;
        cacheUserConnected.put(connectionStamp, userConnected);
        return userConnected.toghUser;                
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
        ToghUserEntity endUserEntity = null;

        if (userConnected==null) {
            // search in the database
            endUserEntity = factoryService.getToghUserService().getUserFromConnectionStamp( connectionStamp );
        }
        else {
            endUserEntity = userConnected.toghUser;
        }
        if (endUserEntity == null)
            return; // already disconnected, or not exist

        endUserEntity.setConnectionStamp(null);
        factoryService.getToghUserService().saveUser(endUserEntity);

        cacheUserConnected.remove(connectionStamp);
    }

    
  
}

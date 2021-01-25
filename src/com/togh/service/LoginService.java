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
import org.springframework.stereotype.Service;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.repository.EndUserRepository;
import com.togh.service.LoginService.LoginStatus;
import com.togh.service.MonitorService.Chrono;



@Service
public class LoginService {
 
    @Autowired
    FactoryService factoryService;
    
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
    public LoginStatus connectWithEmail(String email, String password) {
        LoginStatus loginStatus = new LoginStatus();
        MonitorService monitorService = factoryService.getMonitorService();
        Chrono chronoConnection = monitorService.startOperation("ConnectUserWithEmail");
        
        ToghUserEntity endUser = factoryService.getToghUserService().getFromEmail( email );
        if (endUser==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        // this user must be registered on the portal
        if (! SourceUserEnum.PORTAL.equals( endUser.getSourceUser())) {
            monitorService.endOperationWithStatus(chronoConnection, "NotRegisteredOnPortal");
            return loginStatus;
        }
        
        // check the password
        if (! endUser.checkPassword(password)) {
            monitorService.endOperationWithStatus(chronoConnection, "BadPassword");
            return loginStatus;
        }
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUser;
        loginStatus.connectionToken = connectUser( endUser);
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
        
        ToghUserEntity endUser =  factoryService.getToghUserService().getFromEmail( email );
        if (endUser==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUser;
        loginStatus.connectionToken = connectUser( endUser);
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
        
        ToghUserEntity endUser = factoryService.getToghUserService().getFromEmail(email);
        if (endUser==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        // not correct is the source is not the correct one
        if (isGoogle && (! endUser.getSourceUser().equals(SourceUserEnum.GOOGLE)))
            return loginStatus;
        
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUser;
        loginStatus.connectionToken = connectUser( endUser);
        monitorService.endOperation(chronoConnection);
        return loginStatus;
        
    }
    
    // create a new user
    public LoginStatus registerNewUser(String email, String firstName, String lastName, String password, SourceUserEnum sourceUser)  {
        LoginStatus loginStatus = new LoginStatus();
        ToghUserEntity endUser = factoryService.getToghUserService().getFromEmail( email );
        if (endUser !=null) {
            return loginStatus;
        }
        endUser = new ToghUserEntity();
        endUser.setEmail(email);
        endUser.setFirstname(firstName);
        endUser.setLastName(lastName);
        endUser.setPassword(password);
        endUser.setSourceUser(sourceUser);
        try {
            factoryService.getToghUserService().saveUser(endUser);
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
        public long userId;
        public LocalDateTime lastPing = LocalDateTime.now(ZoneOffset.UTC);
        public LocalDateTime lastCheck = LocalDateTime.now(ZoneOffset.UTC);

    }
    
    private Map<String,UserConnected> cacheUserConnected  = new HashMap<>();
    private String connectUser( ToghUserEntity endUser ) {
        // Generate a ConnectionStamp
        MonitorService monitorService = factoryService.getMonitorService();

        String randomStamp = String.valueOf(System.currentTimeMillis())+ String.valueOf( random.nextInt() );
        
        endUser.setConnectionStamp( randomStamp );
        endUser.setConnectionTime( LocalDateTime.now(ZoneOffset.UTC));
        endUser.setConnectionLastActivity( LocalDateTime.now(ZoneOffset.UTC));
       
        factoryService.getToghUserService().saveUser(endUser);
        
        // keep in the cache to be more efficient
        UserConnected userConnected = new UserConnected();
        userConnected.userId = endUser.getId();
        cacheUserConnected.put( randomStamp, userConnected);
        return randomStamp;
    }
    
    /**
     * 
     * @param connectionStamp
     * @return a user, or null if nobody is connected
     */
    public Long isConnected( String connectionStamp) {
        LocalDateTime timeCheck = LocalDateTime.now();
        // is the user is in the cache AND the last ping was less than 2 mn ? If you, we trust the cache.
        UserConnected userConnected = cacheUserConnected.get( connectionStamp);
        if (userConnected != null) {
            userConnected.lastPing = LocalDateTime.now();
            Duration duration = Duration.between(userConnected.lastCheck, LocalDateTime.now());
            if (duration.toMinutes() < 2) 
                return userConnected.userId;
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
            userConnected.userId = endUserEntity.getId();
        }
        userConnected.lastCheck = timeCheck;
        cacheUserConnected.put(connectionStamp, userConnected);
        return userConnected.userId;                
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
            endUserEntity = factoryService.getToghUserService().getUserFromId( userConnected.userId);
        }
        if (endUserEntity == null)
            return; // already disconnected, or not exist

        endUserEntity.setConnectionStamp(null);
        cacheUserConnected.remove(connectionStamp);
    }

    
  
}

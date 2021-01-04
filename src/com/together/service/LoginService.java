package com.together.service;

import java.time.Duration;
import java.time.LocalDateTime;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login service */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;
import com.together.data.entity.EndUserEntity;
import com.together.service.MonitorService.Chrono;



@Service
public class LoginService extends ToghService {
 
    
    public static class LoginStatus {
        public boolean isConnected=false;
        public EndUserEntity userConnected;
        public String connectionStamp = "bob";
        
        public Map<String,Object> getMap(){
            Map<String,Object> map = new HashMap<>();
            map.put("isConnected", isConnected);
            if (userConnected!=null)
                map.put("user", userConnected.getMapForJson());
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
        UserService userService=serviceAccessor.getUserService();
        MonitorService monitorService=serviceAccessor.getMonitorService();

        Chrono chronoConnection = monitorService.startOperation("ConnectUserWithEmail");
        
        EndUserEntity endUser = userService.getFromEmail( email );
        if (endUser==null) {
            monitorService.endOperationWithStatus(chronoConnection, "NotExist");
            return loginStatus;
        }
        // check the password
        if (! endUser.checkPassword(password)) {
            monitorService.endOperationWithStatus(chronoConnection, "BadPassword");
            return loginStatus;
        }
        loginStatus.isConnected=true;
        loginStatus.userConnected = endUser;
        loginStatus.connectionStamp = connectUser( endUser);
        monitorService.endOperation(chronoConnection);
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
        public LocalDateTime lastPing = LocalDateTime.now();
        public LocalDateTime lastCheck = LocalDateTime.now();

    }
    
    private Map<String,UserConnected> cacheUserConnected  = new HashMap<>();
    private String connectUser( EndUserEntity endUser ) {
        // Generate a ConnectionStamp
        
        String randomStamp = String.valueOf(System.currentTimeMillis())+ String.valueOf( random.nextInt() );
        
        endUser.setConnectionStamp( randomStamp );
        endUser.setConnectionTime( LocalDateTime.now());
        endUser.setConnectionLastActivity( LocalDateTime.now());
       
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
        UserService userService=serviceAccessor.getUserService();

        // more than 2 mn or not in the cache? Get it from the database
        EndUserEntity endUserEntity = userService.getUserFromConnectionStamp( connectionStamp );
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
        EndUserEntity endUserEntity = null;
        UserService userService=serviceAccessor.getUserService();

        if (userConnected==null) {
            // search in the database
            endUserEntity = userService.getUserFromConnectionStamp( connectionStamp );
        }
        else {
            endUserEntity = userService.getUserFromId( userConnected.userId);
        }
        if (endUserEntity == null)
            return; // already disconnected, or not exist

        endUserEntity.setConnectionStamp(null);
        cacheUserConnected.remove(connectionStamp);
    }

}

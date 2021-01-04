package com.together.repository.mem;

import java.util.HashMap;
import java.util.Map;

import com.together.data.entity.EndUserEntity;
import com.together.repository.EndUserRepository;

public class EndUserMemRepository implements EndUserRepository {

    private Map<Long,EndUserEntity> mapEndUserEntity = new HashMap<>();

    
    public EndUserMemRepository() {
        mapEndUserEntity.put(1L, getSimulateEndUser(1L, "py", "Pierre-Yves", "Monnet", "pierre-yves.monnet@laposte.net"));
        mapEndUserEntity.put(2L, getSimulateEndUser(2L, "christelle", "Christelle", "Monnet", "christelle.monnet@laposte.net"));
        mapEndUserEntity.put(3L, getSimulateGoogleUser(3L, "pierreyve.monnet@gmail.com"));
    }
    @Override
    public EndUserEntity getUserFromEmail(String email) {
        if (email==null)
            return null;
        for (EndUserEntity endUser : mapEndUserEntity.values()) {
            if (email.equals(endUser.getEmail()))
                return endUser;
        }
        return null;
    } 
    
  
    @Override
    public EndUserEntity getUserFromId(long id) {
        for (EndUserEntity endUser : mapEndUserEntity.values()) {
            if (id == endUser.getId())
                return endUser;
        }
        return null;
    }
    @Override
    public EndUserEntity getUserFromConnectionStamp(String connectionStamp) {
        for (EndUserEntity endUser : mapEndUserEntity.values()) {
            if (connectionStamp.equals( endUser.getConnectionStamp()))
                return endUser;
        }
        return null;
    }
    
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Simulation */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    private EndUserEntity getSimulateEndUser(Long userId, String userName, String firstName, String lastName, String email) {
        EndUserEntity endUserEntity = new EndUserEntity( userName );
        endUserEntity.setFirstName(firstName);
        endUserEntity.setLastName( lastName );
        endUserEntity.setEmail( email );
        endUserEntity.setPassword("tog");
        endUserEntity.setId( userId );
        return endUserEntity;        
    }
    
    private EndUserEntity getSimulateGoogleUser(long userId, String googleId ) {
        EndUserEntity endUserEntity = new EndUserEntity( "" );
        endUserEntity.setGoogleId(googleId);
        endUserEntity.setId( userId );
        return endUserEntity;        
    }
}
    
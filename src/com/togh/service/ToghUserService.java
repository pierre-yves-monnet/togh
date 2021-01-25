package com.togh.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.repository.EndUserRepository;

@Service
public class ToghUserService {

  
    private Logger logger = Logger.getLogger(ToghUserService.class.getName());
    private final static String logHeader ="ThogUserService:";
  
    @Autowired
    FactoryService factoryService;
  
	@Autowired
    private EndUserRepository endUserRepository;
    
    
    public ToghUserEntity getUserFromId(long userId ) {
        return endUserRepository.findById( userId );
    }
    
    public ToghUserEntity getFromEmail(String email ) {
        return endUserRepository.findByEmail(email);
    }
    public ToghUserEntity getUserFromConnectionStamp(String connectionStamp ) {
        return endUserRepository.findByConnectionStamp(connectionStamp);
    }
    
    public void saveUser(ToghUserEntity user ) {
        endUserRepository.save( user );
    }

 
    /**
     * Register a new user
     * 
     */
    public ToghUserEntity registerNewUser(String firstName, String lastName, String password, String email, SourceUserEnum sourceUser) {
        ToghUserEntity endUser = new ToghUserEntity();
    endUser.setEmail(email);
    endUser.setFirstname(firstName);
    endUser.setLastName(lastName);
    endUser.setPassword(password);
    endUser.setSourceUser(sourceUser);
    try {
        factoryService.getToghUserService().saveUser(endUser);
        return endUser;
    } catch(Exception e) {
        logger.severe(logHeader+"Can't create new user: "+e.toString());
        return null;
    }
    }

    /**
     * Invite a new user: we register it with the status Invited, then we sent an email
     * @param email
     * @return
     */
    public class CreationStatus {
        ToghUserEntity userEntity;
        boolean isEmailIsCorrect=false;
        boolean isEmailSent=false;
    }
    public CreationStatus inviteNewUser(String email) {
        CreationStatus invitationStatus= new CreationStatus();
        try {
            // Check the email now: we don't want to create a bad user
            invitationStatus.isEmailIsCorrect=true;
        
        // fullfill the event
        invitationStatus.userEntity = new ToghUserEntity();
        invitationStatus.userEntity.setEmail(email);
        invitationStatus.userEntity.setSourceUser(SourceUserEnum.INVITED);

        factoryService.getToghUserService().saveUser(invitationStatus.userEntity);
        
        // send the email now
        invitationStatus.isEmailSent=true;
        
            return invitationStatus;
        } catch(Exception e) {
            logger.severe(logHeader+"Can't create new user: "+e.toString());
            invitationStatus.userEntity = null;
            return invitationStatus;
        }
    }
}

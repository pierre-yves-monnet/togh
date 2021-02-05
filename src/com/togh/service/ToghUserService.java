package com.togh.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.h2.engine.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.logevent.LogEvent;
import com.togh.logevent.LogEventFactory;
import com.togh.repository.EndUserRepository;

@Service
public class ToghUserService {

  
    private Logger logger = Logger.getLogger(ToghUserService.class.getName());
    private final static String logHeader ="ThogUserService:";
  
    @Autowired
    FactoryService factoryService;
  
	@Autowired
    private EndUserRepository endUserRepository;
    
	@Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    
    
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
        transactionTemplate = new TransactionTemplate(transactionManager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(definition);
        
        try {
            endUserRepository.save( user );
            transactionManager.commit(status);
        } catch (Exception ex) {
            transactionManager.rollback(status);
        }
        
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
    endUser.setSource(sourceUser);
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
    Boolean myTest = new Boolean( true );
    public CreationStatus inviteNewUser(String email, ToghUserEntity invitedByUser, EventEntity event) {
        CreationStatus invitationStatus= new CreationStatus();
        try {
            // Check the email now: we don't want to create a bad user
            invitationStatus.isEmailIsCorrect=true;
        
        // fullfill the event
        invitationStatus.userEntity = new ToghUserEntity();
        invitationStatus.userEntity.setEmail(email);
        invitationStatus.userEntity.setSource(SourceUserEnum.INVITED);

        factoryService.getToghUserService().saveUser(invitationStatus.userEntity);
        
        // send the email now
        invitationStatus.isEmailSent=true;
        NotifyService notifyService = factoryService.getNotifyService();
        List<LogEvent> listEvents = notifyService.notifyNewUserInEvent( invitationStatus.userEntity, invitedByUser, event);
        if (LogEventFactory.isError(listEvents))
            invitationStatus.isEmailSent=false;
        else
            invitationStatus.isEmailSent=true;
        
            return invitationStatus;
        } catch(Exception e) {
            logger.severe(logHeader+"Can't create new user: "+e.toString());
            invitationStatus.userEntity = null;
            return invitationStatus;
        }
    }
    
    public List<ToghUserEntity> searchUsers( String firstName, String lastName, String phoneNumber, String email) {
        
        return endUserRepository.findByAttributes( firstName, lastName, phoneNumber, email);
    }
    
}

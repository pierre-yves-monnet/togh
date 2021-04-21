/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.PrivilegeUserEnum;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.repository.ToghUserRepository;
import com.togh.restcontroller.RestHttpConstant;

@Service
public class ToghUserService {

    private static final String TOGHADMIN = "admintogh";
    private static final String TOGHADMINPASSWORD= "togh";
    private Logger logger = Logger.getLogger(ToghUserService.class.getName());
    private static final String LOG_HEADER = ToghUserService.class.getName()+":";

    @Autowired
    FactoryService factoryService;

    @Autowired
    private ToghUserRepository endUserRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    
    /**
     * Check if the user toghadmin exist. If not, create it
     */
    @PostConstruct
    public void init() {
        ToghUserEntity adminUser = endUserRepository.findByName( TOGHADMIN );
        if (adminUser == null ) {
            adminUser = new ToghUserEntity();
            adminUser.setName( TOGHADMIN );
            adminUser.setPassword( TOGHADMINPASSWORD );
            adminUser.setEmail("toghadmin@togh.com");
            adminUser.setPrivilegeUser(PrivilegeUserEnum.ADMIN);
            adminUser.setSource( SourceUserEnum.SYSTEM);
            endUserRepository.save( adminUser );
        }
        
    }
    
    
    public ToghUserEntity getUserFromId(long userId) {
        Optional<ToghUserEntity> toghUserEntity= endUserRepository.findById(userId);
        if (toghUserEntity.isPresent())
            return toghUserEntity.get();
        return null;
    }

    public ToghUserEntity getFromEmail(String email) {
        return endUserRepository.findByEmail(email);
    }
    
    public ToghUserEntity findToConnect(String emailOrName) {
        return endUserRepository.findToConnect(emailOrName);
    }

    public ToghUserEntity getUserFromConnectionStamp(String connectionStamp) {
        return endUserRepository.findByConnectionStamp(connectionStamp);
    }

    public void saveUser(ToghUserEntity user) {
        // transactionTemplate = new TransactionTemplate(transactionManager);
        // DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        // TransactionStatus status = transactionManager.getTransaction(definition);

        try {
            endUserRepository.save(user);
            // transactionManager.commit(status);
        } catch (Exception ex) {
            // transactionManager.rollback(status);
        }

    }

    /**
     * Register a new user
     */
    public ToghUserEntity registerNewUser(String firstName, String lastName, String password, String email, SourceUserEnum sourceUser) {
        ToghUserEntity endUser = ToghUserEntity.getNewUser(email, firstName, lastName, password, sourceUser);
        try {
            factoryService.getToghUserService().saveUser(endUser);
            return endUser;
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't create new user: " + e.toString());
            return null;
        }
    }

    /**
     * Invite a new user: we register it with the status Invited, then we sent an email
     * 
     * @param email
     * @return
     */
    public class CreationResult {

        public ToghUserEntity toghUser;
        public boolean isEmailIsCorrect = false;
        public boolean isEmailSent = false;
    }

    
    public CreationResult inviteNewUser(String email, ToghUserEntity invitedByUser, EventEntity event) {
        CreationResult invitationStatus = new CreationResult();
        try {
            // Check the email now: we don't want to create a bad user
            invitationStatus.isEmailIsCorrect = true;

            // fullfill the event
            invitationStatus.toghUser = new ToghUserEntity();
            invitationStatus.toghUser.setEmail(email);
            invitationStatus.toghUser.setSource(SourceUserEnum.INVITED);

            factoryService.getToghUserService().saveUser(invitationStatus.toghUser);

            // send the email now
            invitationStatus.isEmailSent = true;
            NotifyService notifyService = factoryService.getNotifyService();
            List<LogEvent> listEvents = notifyService.notifyNewUserInEvent(invitationStatus.toghUser, invitedByUser, event);
            if (LogEventFactory.isError(listEvents))
                invitationStatus.isEmailSent = false;
            else
                invitationStatus.isEmailSent = true;

            return invitationStatus;
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't create new user: " + e.toString());
            invitationStatus.toghUser = null;
            return invitationStatus;
        }
    }

    /**
     * Search users
     */
    public class SearchUsersResult {

        public List<ToghUserEntity> listUsers;
        public int page = 0;
        public int numberPerPage = 1;
        public Long countUsers;
    }

    /**
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param email
     * @param page page number, start at 0
     * @param pageCount number of item per page. If this number is 0, then move to 1
     * @return
     */
    public SearchUsersResult searchUsers(String firstName, String lastName, String phoneNumber, String email, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = endUserRepository.findPublicUsers(firstName, lastName, phoneNumber, email, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = endUserRepository.countPublicUsers(firstName, lastName, phoneNumber, email);
        return searchResult;
    }

    public SearchUsersResult searchUsersOutEvent(String firstName, String lastName, String phoneNumber, String email, long eventId, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = endUserRepository.findPublicUsersOutEvent(firstName, lastName, phoneNumber, email, eventId, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = endUserRepository.countPublicUsersOutEvent(firstName, lastName, phoneNumber, email, eventId);
        return searchResult;
    }
    
    


    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Privilege */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    /**
     * How many item an user can creates in an event ?
     * @param userEntity
     * @return
     */
    public int getPrivilegesNumberOfItems( ToghUserEntity toghUser ) {
        if (toghUser.getSubscriptionUser() == SubscriptionUserEnum.FREE)
            return 15;
        if (toghUser.getSubscriptionUser() == SubscriptionUserEnum.PREMIUM)
            return 100;
        if (toghUser.getSubscriptionUser() == SubscriptionUserEnum.ILLIMITED)
            return 1000;
        // not a FREE user, so this is a very limited one
        return 2;
    }
    /**
     * Return the map of privilege for this user. Then, the interface can work with these privilege
     * @param toghUserEntity
     * @return
     */
    public Map<String,Object> getPrivileges(ToghUserEntity toghUser ) {
        Map<String,Object> result = new HashMap<>();
        result.put("NBITEMS", getPrivilegesNumberOfItems( toghUser));
        result.put("PRIVILEGEUSER", toghUser.getPrivilegeUser().toString());
        return result;
    }
    
   

}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.engine.tool.JpaTool;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.PrivilegeUserEnum;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.entity.ToghUserEntity.StatusUserEnum;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.repository.ToghUserRepository;
import com.togh.service.EventService.EventOperationResult;
import com.togh.service.EventService.UpdateContext;

@Service
public class ToghUserService {

    private static final String TOGHADMIN = "admintogh";
    private static final String TOGHADMINPASSWORD = "togh";
    private Logger logger = Logger.getLogger(ToghUserService.class.getName());
    private static final String LOG_HEADER = ToghUserService.class.getName() + ":";
    private static final LogEvent eventUnknowId = new LogEvent(ToghUserService.class.getName(), 1, Level.APPLICATIONERROR, "Unknow user", "There is no user behind this ID", "Operation can't be done", "Check the ID");

    @Autowired
    FactoryService factoryService;

    @Autowired
    private ToghUserRepository endUserRepository;

    /**
     * Check if the user toghadmin exist. If not, create it
     */
    @PostConstruct
    public void init() {
        ToghUserEntity adminUser = endUserRepository.findByName(TOGHADMIN);
        if (adminUser == null) {
            adminUser = new ToghUserEntity();
            adminUser.setName(TOGHADMIN);
            adminUser.setPassword(TOGHADMINPASSWORD);
            adminUser.setEmail("toghadmin@togh.com");
            adminUser.setPrivilegeUser(PrivilegeUserEnum.ADMIN);
            adminUser.setSource(SourceUserEnum.SYSTEM);
            endUserRepository.save(adminUser);
        }

    }

    public ToghUserEntity getUserFromId(long userId) {
        Optional<ToghUserEntity> toghUserEntity = endUserRepository.findById(userId);
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

    public SearchUsersResult searchAdminUsers(String searchUserSentence, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = endUserRepository.findSentenceUsers(searchUserSentence, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = endUserRepository.countSentenceUsers(searchUserSentence);
        return searchResult;
    }

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This is a restriction filter. So, if all is null/false, there is no restriction
     * 
     * @author Firstname Lastname
     */
    public static class CriteriaSearchUser {

        public String searchSentence = null;
        public boolean connected = false;
        public boolean block = false;
        public boolean administrator = false;
        public boolean premium = false;
        public boolean illimited = false;

    }

    public SearchUsersResult findUserByCriterias(CriteriaSearchUser criteriaSearch, int page, int numberPerPage) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        // TypedQuery<ToghUserEntity> query = cb.createQuery(ToghUserEntity.class);
        
        StringBuilder sqlRequest = new StringBuilder();
                sqlRequest.append( "select toghuser from ToghUserEntity toghuser where 1=1 " );
        /**
         * Search Sentence
         */
                List<Object> listParameters = new ArrayList<>();
        if (criteriaSearch.searchSentence != null && criteriaSearch.searchSentence.trim().length() > 0) {
            sqlRequest.append( " and ( upper(toghuser.firstName) like concat('%', upper( "+registerParameter( listParameters,  criteriaSearch.searchSentence)+" ), '%') "
                    + " or  upper(toghuser.lastName) like concat('%', upper( "+registerParameter( listParameters,  criteriaSearch.searchSentence)+" ), '%') "
                    + " or  upper(toghuser.phoneNumber) like concat('%', upper( "+registerParameter( listParameters,  criteriaSearch.searchSentence)+" ), '%')"
                    + " or  upper(toghuser.email) like concat('%', upper( "+registerParameter( listParameters,  criteriaSearch.searchSentence)+" ), '%') )");
        }
        if (criteriaSearch.connected)
            sqlRequest.append(" and toghuser.connectionStamp is not null ");
        if (criteriaSearch.block) {
            sqlRequest.append(" and toghuser.statusUser = "+registerParameter( listParameters, StatusUserEnum.BLOCKED));
        }
        if (criteriaSearch.administrator) {
            sqlRequest.append(" and toghuser.privilegeUser = "+registerParameter( listParameters, PrivilegeUserEnum.ADMIN));
        }
        if (criteriaSearch.premium) {
            sqlRequest.append(" and toghuser.subscriptionUser = "+registerParameter( listParameters, SubscriptionUserEnum.PREMIUM));
        }
        if (criteriaSearch.illimited) {
            sqlRequest.append(" and toghuser.subscriptionUser = "+registerParameter( listParameters, SubscriptionUserEnum.ILLIMITED));
        }
        sqlRequest.append(" order by toghuser.firstName asc");
        TypedQuery<ToghUserEntity> query = entityManager.createQuery(sqlRequest.toString(), ToghUserEntity.class);
        for (int i=0;i<listParameters.size();i++)
            query.setParameter(i+1, listParameters.get( i ));

        query.setFirstResult( page * numberPerPage);
        query.setMaxResults( numberPerPage);
        // return em.createQuery( “SELECT c FROM Customer c WHERE c.name LIKE ?1”) .setParameter(1, name) .getResultList();
        SearchUsersResult searchResult = new SearchUsersResult();

        searchResult.listUsers = query.getResultList();
        searchResult.countUsers = (long) searchResult.listUsers.size();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        return searchResult;
    }

    /**
     * JPA Impose to give an numberId to each "?" : like "?1". So, this method add the object in the list and return "?<list.size>"
     * @param listParameters
     * @param parameter
     * @return
     */
    private String registerParameter( List<Object> listParameters, Object parameter) {
        listParameters.add( parameter);
        return "?"+listParameters.size();
    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Privilege */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    /**
     * How many item an user can creates in an event ?
     * 
     * @param userEntity
     * @return
     */
    public int getPrivilegesNumberOfItems(ToghUserEntity toghUser) {
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
     * 
     * @param toghUserEntity
     * @return
     */
    public Map<String, Object> getPrivileges(ToghUserEntity toghUser) {
        Map<String, Object> result = new HashMap<>();
        result.put("NBITEMS", getPrivilegesNumberOfItems(toghUser));
        result.put("PRIVILEGEUSER", toghUser.getPrivilegeUser().toString());
        return result;
    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Update user */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Update an user
     * 
     * @param userId
     * @param attribut
     * @param value
     * @return
     */
    public List<LogEvent> updateUser(Long userId, String attributName, Object attributValue) {
        List<LogEvent> listEvents = new ArrayList<>();

        Optional<ToghUserEntity> toghUser = endUserRepository.findById(userId);
        if (!toghUser.isPresent()) {
            listEvents.add(new LogEvent(eventUnknowId, "Id[" + userId + "]"));
            return listEvents;
        }

        UpdateContext updateContext = new UpdateContext();
        updateContext.toghUser = null;
        updateContext.timeZoneOffset = 0;
        updateContext.eventService = null;

        EventOperationResult eventOperationResult = new EventOperationResult();

        JpaTool.updateEntityOperation(toghUser.get(),
                attributName,
                attributValue,
                updateContext,
                eventOperationResult);
        listEvents.addAll(eventOperationResult.listLogEvents);

        factoryService.getToghUserService().saveUser(toghUser.get());
        return listEvents;
    }

}

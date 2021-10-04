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
import com.togh.engine.tool.JpaTool;
import com.togh.entity.EventEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.*;
import com.togh.repository.ToghUserRepository;
import com.togh.service.EventService.UpdateContext;
import com.togh.service.NotifyService.NotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ToghUserService {

    private static final String TOGHADMIN_EMAIL = "toghadmin@togh.com";
    private static final String TOGHADMIN_USERNAME = "toghadmin";
    private static final String TOGHADMIN_PASSWORD = "togh";
    private Logger logger = Logger.getLogger(ToghUserService.class.getName());
    private static final String LOG_HEADER = ToghUserService.class.getName() + ":";

    private static final LogEvent eventUnknowId = new LogEvent(ToghUserService.class.getName(), 1, Level.APPLICATIONERROR, "Unknow user", "There is no user behind this ID", "Operation can't be done", "Check the ID");

    @Autowired
    FactoryService factoryService;

    @Autowired
    private ToghUserRepository endUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ToghUserEntity getUserFromId(long userId) {
        Optional<ToghUserEntity> toghUserEntity = endUserRepository.findById(userId);
        if (toghUserEntity.isPresent())
            return toghUserEntity.get();
        return null;
    }

    public ToghUserEntity getUserFromEmail(String email) {
        return endUserRepository.findByEmail(email);
    }

    public ToghUserEntity findToConnect(String emailOrName) {
        return endUserRepository.findToConnect(emailOrName);
    }

    public ToghUserEntity getUserFromConnectionStamp(String connectionStamp) {
        return endUserRepository.findByConnectionStamp(connectionStamp);
    }

    public void saveUser(ToghUserEntity user) {
        try {
            endUserRepository.save(user);
        } catch (Exception ex) {
            logger.severe(LOG_HEADER + "Can't save user: " + ex.toString());
            throw ex;
        }

    }

    /**
     * Register a new user
     */
    public ToghUserEntity registerNewUser(String firstName, String lastName, String password, String email, SourceUserEnum sourceUser) {
        ToghUserEntity endUser = ToghUserEntity.createNewUser(email, firstName, lastName, password, sourceUser);
        try {
            factoryService.getToghUserService().saveUser(endUser);
            return endUser;
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't create new user: " + e.toString());
            return null;
        }
    }

    /**
     * Check if the user toghadmin exist. If not, create it
     */
    @PostConstruct
    public void init() {
        ToghUserEntity adminUser = endUserRepository.findByName(TOGHADMIN_USERNAME);
        if (adminUser == null) {
            adminUser = new ToghUserEntity();
            adminUser.setName(TOGHADMIN_USERNAME);
            setPassword(adminUser, TOGHADMIN_PASSWORD);
            adminUser.setEmail(TOGHADMIN_EMAIL);
            adminUser.setPrivilegeUser(PrivilegeUserEnum.ADMIN);
            adminUser.setStatusUser(StatusUserEnum.ACTIF);
            adminUser.setSource(SourceUserEnum.SYSTEM);
            adminUser.setSubscriptionUser(SubscriptionUserEnum.EXCELLENCE);
            adminUser.setTypePicture(TypePictureEnum.TOGH);

            endUserRepository.save(adminUser);
        }

    }

    public CreationResult inviteNewUser(String email, ToghUserEntity invitedByUser, boolean useMyEmailAsFrom, EventEntity event) {
        CreationResult invitationStatus = new CreationResult();
        try {
            // Check the email now: we don't want to create a bad user
            invitationStatus.isEmailIsCorrect = true;

            // fullfill the event
            invitationStatus.toghUser = ToghUserEntity.createInvitedUser(email);

            factoryService.getToghUserService().saveUser(invitationStatus.toghUser);

            // send the email now
            invitationStatus.isEmailSent = true;
            NotifyService notifyService = factoryService.getNotifyService();
            NotificationStatus notificationStatus = notifyService.notifyNewUserInEvent(invitationStatus.toghUser,
                    true,
                    invitedByUser,
                    useMyEmailAsFrom,
                    event);

            invitationStatus.isEmailSent = notificationStatus.isCorrect();

            return invitationStatus;
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't create new user: " + e.toString());
            invitationStatus.toghUser = null;
            return invitationStatus;
        }
    }

    /**
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param email
     * @param page        page number, start at 0
     * @param pageCount   number of item per page. If this number is 0, then move to 1
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

    /**
     * Search users connected, but with no activity after the limeSearch time
     *
     * @param limitSearch
     * @param page
     * @param numberPerPage
     * @return
     */
    public SearchUsersResult searchConnectedUsersNoActivity(LocalDateTime limitSearch, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = endUserRepository.findConnectedUsersNoActivity(limitSearch, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = endUserRepository.countConnectedUsersNoActivity(limitSearch);
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

    public SearchUsersResult findUserByCriterias(CriteriaSearchUser criteriaSearch, int page, int numberPerPage) {

        StringBuilder sqlRequest = new StringBuilder();
        sqlRequest.append("select toghuser from ToghUserEntity toghuser where 1=1 ");
        /**
         * Search Sentence
         */
        List<Object> listParameters = new ArrayList<>();
        if (criteriaSearch.searchSentence != null && criteriaSearch.searchSentence.trim().length() > 0) {
            sqlRequest.append(" and ( upper(toghuser.firstName) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%') "
                    + " or  upper(toghuser.lastName) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%') "
                    + " or  upper(toghuser.phoneNumber) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%')"
                    + " or  upper(toghuser.email) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%') )");
        }
        if (criteriaSearch.connected)
            sqlRequest.append(" and toghuser.connectionStamp is not null ");
        if (criteriaSearch.block) {
            sqlRequest.append(" and toghuser.statusUser = " + registerParameter(listParameters, StatusUserEnum.BLOCKED));
        }
        if (criteriaSearch.administrator) {
            sqlRequest.append(" and toghuser.privilegeUser = " + registerParameter(listParameters, PrivilegeUserEnum.ADMIN));
        }
        if (criteriaSearch.premium) {
            sqlRequest.append(" and toghuser.subscriptionUser = " + registerParameter(listParameters, SubscriptionUserEnum.PREMIUM));
        }
        if (criteriaSearch.excellence) {
            sqlRequest.append(" and toghuser.subscriptionUser = " + registerParameter(listParameters, SubscriptionUserEnum.EXCELLENCE));
        }
        sqlRequest.append(" order by toghuser.firstName asc");
        TypedQuery<ToghUserEntity> query = entityManager.createQuery(sqlRequest.toString(), ToghUserEntity.class);
        for (int i = 0; i < listParameters.size(); i++)
            query.setParameter(i + 1, listParameters.get(i));

        query.setFirstResult(page * numberPerPage);
        query.setMaxResults(numberPerPage);
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
     *
     * @param listParameters
     * @param parameter
     * @return
     */
    private String registerParameter(List<Object> listParameters, Object parameter) {
        listParameters.add(parameter);
        return "?" + listParameters.size();
    }

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
        if (toghUser.getSubscriptionUser() == SubscriptionUserEnum.EXCELLENCE)
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

    public OperationUser updateUser(Long userId, String attributName, Object attributValue) {
        OperationUser operationUser = new OperationUser();

        Optional<ToghUserEntity> toghUser = endUserRepository.findById(userId);
        if (!toghUser.isPresent()) {
            operationUser.listLogEvents.add(new LogEvent(eventUnknowId, "Id[" + userId + "]"));
            return operationUser;
        }
        operationUser.toghUserEntity = toghUser.get();
        UpdateContext updateContext = new UpdateContext();
        updateContext.toghUser = null;
        updateContext.timezoneOffset = 0;
        updateContext.factoryService = null;

        operationUser.listLogEvents.addAll(JpaTool.updateEntityOperation(toghUser.get(),
                attributName,
                attributValue,
                updateContext));

        factoryService.getToghUserService().saveUser(operationUser.toghUserEntity);


        // we have to propagate this change to any another service
        factoryService.getLoginService().userIsUpdated(operationUser.toghUserEntity);

        return operationUser;
    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Privilege */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Set the password in the user. The password will be encrypted at this moment.
     * Object toghUser is not saved
     *
     * @param toghUser
     * @param password
     * @param saveImmediately
     */
    public void setPassword(ToghUserEntity toghUser, String password) {
        toghUser.setPassword(encryptPassword(password));
    }

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
        public boolean excellence = false;

    }
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Update user */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

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


    // --------------------------------------------------------------
    // 
    // Encrypt password
    // 
    // --------------------------------------------------------------

    /**
     * Update an user
     *
     * @param userId
     * @param attribut
     * @param value
     * @return
     */
    public class OperationUser {
        public List<LogEvent> listLogEvents = new ArrayList<>();
        public ToghUserEntity toghUserEntity = null;
    }

    private static String salt = "EqdmPh53c9x33EygXpTpcoJvc4VXLK";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static String encryptPassword(String password) {
        char[] passwordChar = password.toCharArray();
        byte[] saltChar = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(passwordChar, saltChar, ITERATIONS, KEY_LENGTH);
        Arrays.fill(passwordChar, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] securePassword = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(securePassword);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

}

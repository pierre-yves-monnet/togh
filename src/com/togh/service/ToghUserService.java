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
import com.togh.tool.ToolCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ToghUserService {

    private static final String TOGHADMIN_EMAIL = "toghadmin@togh.com";
    private static final String TOGHADMIN_USERNAME = "toghadmin";
    private static final String TOGHADMIN_PASSWORD = "togh";
    private static final String LOG_HEADER = ToghUserService.class.getName() + ":";
    private static final LogEvent eventUnknowId = new LogEvent(ToghUserService.class.getName(), 1, Level.APPLICATIONERROR, "Unknow user", "There is no user behind this ID", "Operation can't be done", "Check the ID");
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String SALT = "EqdmPh53c9x33EygXpTpcoJvc4VXLK";
    @Autowired
    FactoryService factoryService;
    private Logger logger = Logger.getLogger(ToghUserService.class.getName());
    @Autowired
    private ToghUserRepository toghUserRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private static final String CONNECTION_LAST_MONTH = "select TO_CHAR(dateCreation, 'YYYY-MM-DD') as dateLog, count(*) as value"
            + " from LoginLogEntity "
            + " where statusconnection='" + LoginService.LoginStatus.OK + "' "
            + " and dateCreation > ?1"
            + " group by dateLog"
            + " order by dateLog";
    private static final String CONNECTION_FIVE_YEARS = "select TO_CHAR(dateCreation, 'YYYY-MM') as dateLog, count(*) as value"
            + " from LoginLogEntity "
            + " where statusConnection='" + LoginService.LoginStatus.OK + "' "
            + " and dateCreation > ?1"
            + " group by dateLog"
            + " order by dateLog";
    private static final String CONNECTION_BAD_PASSWORD = "select TO_CHAR(datecreation, 'YYYY-MM-DD') as dateLog,"
            + " sum(numberOfTentatives) as value"
            + " from LoginLogEntity"
            + " where (statusConnection='" + LoginService.LoginStatus.BADPASSWORD + "' or statusConnection='" + LoginService.LoginStatus.UNKNOWUSER + "')"
            + " and dateCreation > ?1"
            + " group by dateLog"
            + " order by dateLog";

    public Optional<ToghUserEntity> getUserFromEmail(String email) {
        return Optional.ofNullable(toghUserRepository.findByEmail(email));
    }

    public ToghUserEntity findToConnect(String emailOrName) {
        return toghUserRepository.findToConnect(emailOrName);
    }

    public ToghUserEntity getUserFromConnectionStamp(String connectionStamp) {
        return toghUserRepository.findByConnectionStamp(connectionStamp);
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
            logger.severe(LOG_HEADER + "Can't create new user: " + e);
            return null;
        }
    }

    public void saveUser(ToghUserEntity user) {
        try {
            toghUserRepository.save(user);
        } catch (Exception ex) {
            logger.severe(LOG_HEADER + "Can't save user: " + ex);
            throw ex;
        }

    }

    /**
     * Check if the user toghAdmin exist. If not, create it
     */
    @PostConstruct
    public void init() {
        ToghUserEntity adminUser = toghUserRepository.findByName(TOGHADMIN_USERNAME);
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

            toghUserRepository.save(adminUser);
        }

    }

    /**
     * Invite a new user
     *
     * @param email            email of the user
     * @param invitedByUser    whom invite this user
     * @param useMyEmailAsFrom use the user who invite as the "From" in the email for more accurancy
     * @param event            event to invite into
     * @return creation + invitation status. User may not exist in Togh
     */
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
                    invitedByUser,
                    useMyEmailAsFrom,
                    event);

            invitationStatus.isEmailSent = notificationStatus.isCorrect();

            return invitationStatus;
        } catch (Exception e) {
            logger.severe(LOG_HEADER + "Can't create new user: " + e);
            invitationStatus.toghUser = null;
            return invitationStatus;
        }
    }

    /**
     * Search users from criteria
     *
     * @param firstName     first name criteria
     * @param lastName      last name criteria
     * @param phoneNumber   phone number criteria
     * @param email         email criteria
     * @param page          page number, start at 0
     * @param numberPerPage number of item per page. If this number is 0, then move to 1
     * @return result of search
     */
    public SearchUsersResult searchUsers(String firstName, String lastName, String phoneNumber, String email, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = toghUserRepository.findPublicUsers(firstName, lastName, phoneNumber, email, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = toghUserRepository.countPublicUsers(firstName, lastName, phoneNumber, email);
        return searchResult;
    }

    /**
     * Search users connected, but with no activity after the limeSearch time
     *
     * @param limitSearch   Limit To search user connected
     * @param page          page (start at 1)
     * @param numberPerPage number of items per page
     * @return the Search result
     */
    public SearchUsersResult searchConnectedUsersNoActivity(LocalDateTime limitSearch, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = toghUserRepository.findConnectedUsersNoActivity(limitSearch, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = toghUserRepository.countConnectedUsersNoActivity(limitSearch);
        return searchResult;
    }


    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Administration */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    public SearchUsersResult searchUsersOutEvent(String firstName, String lastName, String phoneNumber, String email, long eventId, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = toghUserRepository.findPublicUsersOutEvent(firstName, lastName, phoneNumber, email, eventId, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = toghUserRepository.countPublicUsersOutEvent(firstName, lastName, phoneNumber, email, eventId);
        return searchResult;
    }

    /**
     * Search user form an administrative point of view
     *
     * @param searchUserSentence Search user by a sentence
     * @param page               page number (start at 1)
     * @param numberPerPage      number of items per pages
     * @return a Search User Result
     */
    public SearchUsersResult searchAdminUsers(String searchUserSentence, int page, int numberPerPage) {
        SearchUsersResult searchResult = new SearchUsersResult();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        searchResult.listUsers = toghUserRepository.findSentenceUsers(searchUserSentence, PageRequest.of(searchResult.page, searchResult.numberPerPage));
        searchResult.countUsers = toghUserRepository.countSentenceUsers(searchUserSentence);
        return searchResult;
    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Privilege */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * JPA Impose to give an numberId to each "?" : like "?1". So, this method add the object in the list and return "?<list.size>"
     *
     * @param listParameters list parameters
     * @param parameter      Number
     * @return the string plus the parameter number
     */
    private String registerParameter(List<Object> listParameters, Object parameter) {
        listParameters.add(parameter);
        return "?" + listParameters.size();
    }

    private static final String USER_CREATION_LAST_MONTH = "select TO_CHAR(dateCreation, 'YYYY-MM-DD') as dateLog, count(*) as value"
            + " from ToghUserEntity "
            + " where "
            + " dateCreation > ?1"
            + " group by dateLog"
            + " order by dateLog";
    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Statistics on user */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    private static final String USER_CREATION_FIVE_YEARS = "select TO_CHAR(dateCreation, 'YYYY-MM') as dateLog, count(*) as value"
            + " from ToghUserEntity "
            + " where "
            + " dateCreation > ?1"
            + " group by dateLog"
            + " order by dateLog";

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Update user */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    private static final String PARTICIPANT_CREATION_LAST_MONTH = "select TO_CHAR(dateCreation, 'YYYY-MM-DD') as dateLog, count(*) as value"
            + " from ParticipantEntity "
            + " where "
            + " dateCreation > ?1"
            + " group by dateLog"
            + " order by dateLog";
    private final List<StatisticsSqlItem> listStatisticsSqlItem = Arrays.asList(
            new StatisticsSqlItem("total", "count(*)"),
            new StatisticsSqlItem("connected", "sum( case when connectionStamp is not null then 1 else 0 end)"),
            new StatisticsSqlItem("map_status_blocked", "sum( case when statusUser= 'BLOCKED' then 1 else 0 end)"),
            new StatisticsSqlItem("map_status_invited", "sum( case when statusUser= 'INVITED' then 1 else 0 end)"),
            new StatisticsSqlItem("map_status_disabled", "sum( case when statusUser= 'DISABLED' then 1 else 0 end)"),
            new StatisticsSqlItem("map_status_actif", "sum( case when statusUser= 'ACTIF' then 1 else 0 end)"),

            new StatisticsSqlItem("map_source_portal", "sum( case when source= 'PORTAL' then 1 else 0 end)"),
            new StatisticsSqlItem("map_source_google", "sum( case when source= 'GOOGLE' then 1 else 0 end)"),
            new StatisticsSqlItem("map_source_invited", "sum( case when source= 'INVITED' then 1 else 0 end)"),
            new StatisticsSqlItem("map_source_system", "sum( case when source= 'SYSTEM' then 1 else 0 end)"),

            new StatisticsSqlItem("map_privilege_admin", "sum( case when privilegeUser= 'ADMIN' then 1 else 0 end)"),
            new StatisticsSqlItem("map_privilege_trans", "sum( case when privilegeUser= 'TRANS' then 1 else 0 end)"),
            new StatisticsSqlItem("map_privilege_user", "sum( case when privilegeUser= 'USER' then 1 else 0 end)"),

            new StatisticsSqlItem("show_tips", "sum( case when showTipsUser= true then 1 else 0 end)"),
            new StatisticsSqlItem("searchable", " sum( case when searchable= true then 1 else 0 end)"),

            new StatisticsSqlItem("map_emailVisibility_always", "sum( case when emailVisibility= 'ALWAYS' then 1 else 0 end)"),
            new StatisticsSqlItem("map_emailVisibility_noSearch", "sum( case when emailVisibility= 'ALWAYBUTSEARCH' then 1 else 0 end)"),
            new StatisticsSqlItem("map_emailVisibility_limitedEvent", "sum( case when emailVisibility= 'LIMITEDEVENT' then 1 else 0 end)"),
            new StatisticsSqlItem("map_emailVisibility_never", "sum( case when emailVisibility= 'NEVER' then 1 else 0 end)"),

            new StatisticsSqlItem("map_phoneVisibility_always", "sum( case when phoneNumberVisibility= 'ALWAYS' then 1 else 0 end)"),
            new StatisticsSqlItem("map_phoneVisibility_noSearch", "sum( case when phoneNumberVisibility= 'ALWAYBUTSEARCH' then 1 else 0 end)"),
            new StatisticsSqlItem("map_phoneVisibility_limitedEvent", "sum( case when phoneNumberVisibility= 'LIMITEDEVENT' then 1 else 0 end)"),
            new StatisticsSqlItem("map_phoneVisibility_never", "sum( case when phoneNumberVisibility= 'NEVER' then 1 else 0 end)"),

            new StatisticsSqlItem("map_subscription_free", "sum( case when subscriptionUser= 'FREE' then 1 else 0 end)"),
            new StatisticsSqlItem("map_subscription_premium", "sum( case when subscriptionUser= 'PREMIUM' then 1 else 0 end)"),
            new StatisticsSqlItem("map_subscription_excellence", "sum( case when subscriptionUser= 'EXCELLENCE' then 1 else 0 end)")

    );

    public static String encryptPassword(String password) {
        // we don't want to use a secret random: we need to encrypt again a password to compare it
        char[] passwordChar = password.toCharArray();
        byte[] saltChar = SALT.getBytes();
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

    /**
     * Return the map of privilege for this user. Then, the interface can work with these privilege
     *
     * @param toghUserEntity ToghUser to get pr
     * @return map of privileges
     */
    public Map<String, Object> getPrivileges(ToghUserEntity toghUserEntity) {
        Map<String, Object> result = new HashMap<>();
        result.put("NBITEMS", getPrivilegesNumberOfItems(toghUserEntity));
        result.put("PRIVILEGEUSER", toghUserEntity.getPrivilegeUser().toString());
        return result;
    }

    /**
     * How many items a user can create in an event ?
     *
     * @param toghUserEntity the user
     * @return the default number of items
     */
    public int getPrivilegesNumberOfItems(ToghUserEntity toghUserEntity) {
        if (toghUserEntity.getSubscriptionUser() == SubscriptionUserEnum.FREE)
            return 15;
        if (toghUserEntity.getSubscriptionUser() == SubscriptionUserEnum.PREMIUM)
            return 100;
        if (toghUserEntity.getSubscriptionUser() == SubscriptionUserEnum.EXCELLENCE)
            return 1000;
        // not a FREE user, so this is a very limited one
        return 2;
    }

    public ToghUserEntity getUserFromId(long userId) {
        Optional<ToghUserEntity> toghUserEntity = toghUserRepository.findById(userId);
        return toghUserEntity.orElse(null);
    }

    /**
     * Find by criteria
     *
     * @param criteriaSearch Criteria to search users
     * @param page           page number (starts at 1)
     * @param numberPerPage  number of users per page
     * @return users found
     */
    public SearchUsersResult findUserByCriterias(CriteriaSearchUser criteriaSearch, int page, int numberPerPage) {

        StringBuilder sqlRequest = new StringBuilder();
        sqlRequest.append("select toghuser from ToghUserEntity toghuser where 1=1 ");
        /*
         * Search Sentence
         */
        List<Object> listParameters = new ArrayList<>();
        if (criteriaSearch.searchSentence != null && criteriaSearch.searchSentence.trim().length() > 0) {
            sqlRequest.append(" and ( upper(toghuser.firstName) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%') ");
            sqlRequest.append("  or upper(toghuser.lastName) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%') ");
            sqlRequest.append("  or upper(toghuser.phoneNumber) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%')");
            sqlRequest.append("  or upper(toghuser.email) like concat('%', upper( " + registerParameter(listParameters, criteriaSearch.searchSentence) + " ), '%') )");
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

        query.setFirstResult((page - 1) * numberPerPage);
        query.setMaxResults(numberPerPage);
        SearchUsersResult searchResult = new SearchUsersResult();

        searchResult.listUsers = query.getResultList();
        searchResult.countUsers = (long) searchResult.listUsers.size();
        searchResult.page = page;
        searchResult.numberPerPage = numberPerPage == 0 ? 1 : numberPerPage;
        return searchResult;
    }

    public OperationUser updateUser(Long userId, String attributName, Object attributValue) {
        OperationUser operationUser = new OperationUser();

        Optional<ToghUserEntity> toghUser = toghUserRepository.findById(userId);
        if (toghUser.isEmpty()) {
            operationUser.listLogEvents.add(new LogEvent(eventUnknowId, "Id[" + userId + "]"));
            return operationUser;
        }
        operationUser.toghUserEntity = toghUser.get();
        UpdateContext updateContext = new UpdateContext(toghUser.get(), 0, null, null);

        operationUser.listLogEvents.addAll(JpaTool.updateEntityOperation(toghUser.get(),
                attributName,
                attributValue,
                updateContext));

        factoryService.getToghUserService().saveUser(operationUser.toghUserEntity);


        // we have to propagate this change to any another service
        factoryService.getLoginService().userIsUpdated(operationUser.toghUserEntity);

        return operationUser;
    }

    /**
     * return statistics on user
     *
     * @return statistics on users
     */
    public StatisticsUsers statisticsOnUsers() {
        StatisticsUsers statisticsUsers = new StatisticsUsers();


        // ----------------- Statistics on Users
        StringBuilder sqlRequest = new StringBuilder();
        sqlRequest.append("select ");
        sqlRequest.append(listStatisticsSqlItem.stream()
                .map(t -> t.sql + " as " + t.name)
                .collect(Collectors.joining(",")));
        sqlRequest.append(" from ToghUserEntity");

        Query query = entityManager.createQuery(sqlRequest.toString());
        Object[] resultQuery = (Object[]) query.getSingleResult();
        for (int i = 0; i < listStatisticsSqlItem.size(); i++) {
            long markerValue = ToolCast.getLong(resultQuery[i], 0L);
            String markerName = listStatisticsSqlItem.get(i).name;
            if (markerName.startsWith("map_")) {
                StringTokenizer st = new StringTokenizer(markerName, "_");
                st.nextToken();
                String collectionName = st.nextToken();
                String nameInCollection = st.nextToken();
                Map<String, Object> collection = (Map<String, Object>) statisticsUsers.users.getOrDefault(collectionName, new HashMap<String, Object>());
                collection.put(nameInCollection, markerValue);
                statisticsUsers.users.put(collectionName, collection);
            } else {
                statisticsUsers.users.put(listStatisticsSqlItem.get(i).name, markerValue);
            }
        }


        // ----------------- Statistics on Connection
        Calendar onMonth = Calendar.getInstance();
        onMonth.add(Calendar.MONTH, -1);
        Calendar fiveYears = Calendar.getInstance();
        fiveYears.add(Calendar.YEAR, -5);

        executeAndComplete("connection", CONNECTION_LAST_MONTH, onMonth, Calendar.DAY_OF_YEAR, statisticsUsers);
        executeAndComplete("connectionFiveYears", CONNECTION_FIVE_YEARS, fiveYears, Calendar.MONTH, statisticsUsers);
        executeAndComplete("badpassword", CONNECTION_BAD_PASSWORD, onMonth, Calendar.DAY_OF_YEAR, statisticsUsers);

        executeAndComplete("userCreation", USER_CREATION_LAST_MONTH, onMonth, Calendar.DAY_OF_YEAR, statisticsUsers);
        executeAndComplete("userCreationFiveYears", USER_CREATION_FIVE_YEARS, fiveYears, Calendar.MONTH, statisticsUsers);

        executeAndComplete("participantCreation", PARTICIPANT_CREATION_LAST_MONTH, onMonth, Calendar.DAY_OF_YEAR, statisticsUsers);

        return statisticsUsers;
    }


    /**
     * Execute the request, and populate the list in statistics users
     *
     * @param listName        name of the list
     * @param sqlRequest      sqlRequest to execute
     * @param startDate       all value from this startDate to now will be populated
     * @param stepCalendar    may be Calendar.DAY_OF_YEAR or Calendar.MONTH.
     * @param statisticsUsers populate this object
     */
    private void executeAndComplete(String listName, String sqlRequest, Calendar startDate, int stepCalendar, StatisticsUsers statisticsUsers) {

        Query query = entityManager.createQuery(sqlRequest);
        query.setParameter(1, LocalDateTime.ofInstant(startDate.getTime().toInstant(), ZoneOffset.UTC));


        List<Object[]> listResultQuery = query.getResultList();
        Map<String, Object> resultPerDate = new HashMap<>();
        for (Object[] recordIterator : listResultQuery) {
            resultPerDate.put(recordIterator[0].toString(), recordIterator[1]);
        }

        // now, populate the result
        Date dateEnd = new Date();
        Calendar dateIterator = (Calendar) startDate.clone();
        SimpleDateFormat sdf = new SimpleDateFormat(stepCalendar == Calendar.DAY_OF_YEAR ? "yyyy-MM-dd" : "yyyy-MM");
        while (dateIterator.getTime().before(dateEnd)) {
            String dateFormat = sdf.format(dateIterator.getTime());
            Map<String, Object> recordDate = Map.of("label", dateFormat,
                    "value", resultPerDate.getOrDefault(dateFormat, 0));
            statisticsUsers.addInList(listName, recordDate);

            dateIterator.add(stepCalendar, 1);
        }

    }

    public static class StatisticsUsers {
        Map<String, Object> users = new HashMap<>();

        public Map<String, Object> getMap() {

            return users;
        }

        public void addInList(String listName, Map<String, Object> recordToAdd) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) users.getOrDefault(listName, new ArrayList<>());
            list.add(recordToAdd);
            users.put(listName, list);
        }
    }


    private static class StatisticsSqlItem {
        public String name;
        public String sql;

        StatisticsSqlItem(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }
    }


    // --------------------------------------------------------------
    // 
    // Encrypt password
    // 
    // --------------------------------------------------------------

    /**
     * Set the password in the user. The password will be encrypted at this moment.
     * Object toghUser is not saved
     *
     * @param toghUserEntity the toghUser
     * @param password       the password
     */
    public void setPassword(ToghUserEntity toghUserEntity, String password) {
        toghUserEntity.setPassword(encryptPassword(password));
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

    /**
     * Search users
     */
    public static class SearchUsersResult {

        public List<ToghUserEntity> listUsers;
        public int page = 1;
        public int numberPerPage = 1;

        public Long countUsers;
    }

    /**
     * Invite a new user: we register it with the status Invited, then we sent an email
     */
    public static class CreationResult {

        public ToghUserEntity toghUser;
        public boolean isEmailIsCorrect = false;
        public boolean isEmailSent = false;
    }

    /**
     * Update a user
     */
    public static class OperationUser {
        public List<LogEvent> listLogEvents = new ArrayList<>();
        public ToghUserEntity toghUserEntity = null;
    }

}

package com.togh.restcontroller;


import com.togh.entity.ToghUserEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.serialization.BaseSerializer;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.LoginService.OperationLoginUser;
import com.togh.service.ToghUserService;
import com.togh.service.ToghUserService.CriteriaSearchUser;
import com.togh.service.ToghUserService.OperationUser;
import com.togh.service.ToghUserService.SearchUsersResult;
import com.togh.tool.ToolCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestUserControler */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestUserController {

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private ToghUserService toghUserService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private FactorySerializer factorySerializer;

    @Autowired
    private FactoryUpdateGrantor factoryUpdateGrantor;

    /**
     * Call for the invitation for example, to search a user according some criteria. User should accept to publish some information
     *
     * @param firstName          Search by firstName
     * @param lastName           Search by lastName
     * @param phoneNumber        search by phone
     * @param email              search by Email
     * @param onlyNonInvitedUser Only non invited user in the eventId
     * @param eventId            Event Id for the nonInvitedUser parameter
     * @param connectionStamp    Information on the connected user
     * @return all users found plus additional information
     */
    @CrossOrigin
    @GetMapping("/api/user/search")
    public Map<String, Object> searchUser(@RequestParam(RestJsonConstants.CST_PARAM_FIRST_NAME) String firstName,
                                          @RequestParam(RestJsonConstants.CST_PARAM_LAST_NAME) String lastName,
                                          @RequestParam(RestJsonConstants.CST_PARAM_PHONE_NUMBER) String phoneNumber,
                                          @RequestParam(RestJsonConstants.CST_PARAM_EMAIL) String email,
                                          @RequestParam(RestJsonConstants.CST_PARAM_ONLY_NON_INVITED_USER) Boolean onlyNonInvitedUser,
                                          @RequestParam(RestJsonConstants.CST_INJSON_EVENTID) Long eventId,
                                          @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
                                          @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);

        Map<String, Object> payload = new HashMap<>();
        SearchUsersResult searchUsers;
        if (Boolean.TRUE.equals(onlyNonInvitedUser) && eventId != null)
            searchUsers = factoryService.getToghUserService().searchUsersOutEvent(firstName, lastName, phoneNumber, email, eventId, 0, 20);
        else
            searchUsers = factoryService.getToghUserService().searchUsers(firstName, lastName, phoneNumber, email, 0, 20);


        List<Map<String, Object>> listUsersMap = new ArrayList<>();
        for (ToghUserEntity toghUserEntityIterator : searchUsers.listUsers) {
            BaseSerializer serializer = factorySerializer.getFromEntity(toghUserEntityIterator);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.SEARCH);
            listUsersMap.add(serializer.getMap(toghUserEntityIterator, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }

        payload.put(RestJsonConstants.LISTUSERS, listUsersMap);
        payload.put(RestJsonConstants.NUMBER_OF_ITEMS, searchUsers.countUsers);
        payload.put(RestJsonConstants.PAGE, searchUsers.page);
        payload.put(RestJsonConstants.ITEMS_PER_PAGE, searchUsers.numberPerPage);
        payload.put(RestJsonConstants.NUMBER_OF_PAGES, (int) (searchUsers.countUsers / searchUsers.numberPerPage) + 1);

        return payload;

    }

    /**
     * search users as an admin
     *
     * @param searchUserSentence  search user sentence
     * @param filterConnected     filter connected
     * @param filterBlock         filter block
     * @param filterAdministrator filter administrator
     * @param filterPremium       filter premium
     * @param filterExcellence    filter excellence
     * @param timezoneOffset      time Zone Offset of the browser
     * @param connectionStamp     Information on the connected user
     * @return list of Users
     */
    @CrossOrigin
    @GetMapping(value = "/api/user/admin/search", produces = "application/json")
    public Map<String, Object> searchAdminUser(
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_SENTENCE, required = false) String searchUserSentence,
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_CONNECTED, required = false) boolean filterConnected,
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_BLOCK, required = false) boolean filterBlock,
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_ADMINSTRATOR, required = false) boolean filterAdministrator,
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_PREMIUM, required = false) boolean filterPremium,
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_EXCELLENCE, required = false) boolean filterExcellence,
            @RequestParam(name = RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
            @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);


        CriteriaSearchUser criteriaSearch = new CriteriaSearchUser();
        criteriaSearch.searchSentence = searchUserSentence;
        criteriaSearch.connected = filterConnected;
        criteriaSearch.block = filterBlock;
        criteriaSearch.administrator = filterAdministrator;
        criteriaSearch.premium = filterPremium;
        criteriaSearch.excellence = filterExcellence;

        Map<String, Object> payload = new HashMap<>();

        SearchUsersResult searchUsers;
        searchUsers = toghUserService.findUserByCriterias(criteriaSearch, 1, 50);


        List<Map<String, Object>> listUsersMap = new ArrayList<>();
        for (ToghUserEntity toghUserEntityIterator : searchUsers.listUsers) {
            BaseSerializer serializer = factorySerializer.getFromEntity(toghUserEntityIterator);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            listUsersMap.add(serializer.getMap(toghUserEntityIterator, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }

        payload.put(RestJsonConstants.LISTUSERS, listUsersMap);
        payload.put(RestJsonConstants.PAGE, searchUsers.page);
        payload.put(RestJsonConstants.ITEMS_PER_PAGE, searchUsers.numberPerPage);
        payload.put(RestJsonConstants.NUMBER_OF_PAGES, (searchUsers.countUsers / searchUsers.numberPerPage) + 1);
        payload.put(RestJsonConstants.NUMBER_OF_ITEMS, searchUsers.countUsers);

        return payload;

    }

    /**
     * Update the profile of user
     *
     * @param updateMap       Information to update user
     * @param connectionStamp Information on the connected user
     * @return result of update
     */
    @CrossOrigin
    @PostMapping(value = "/api/user/update", produces = "application/json")
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> updateMap,
                                          @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);

        Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);

        String attribut = ToolCast.getString(updateMap, RestJsonConstants.CST_PARAM_ATTRIBUT, "");
        Object value = updateMap.get(RestJsonConstants.CST_PARAM_VALUE);

        Map<String, Object> payload = new HashMap<>();
        OperationUser operationUser = toghUserService.updateUser(toghUser.getId(), attribut, value);
        if (operationUser.toghUserEntity != null) {
            BaseSerializer serializer = factorySerializer.getFromEntity(operationUser.toghUserEntity);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            payload.put(RestJsonConstants.CST_USER, serializer.getMap(operationUser.toghUserEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        payload.put(RestJsonConstants.LOG_EVENTS, operationUser.listLogEvents);

        return payload;
    }

    /**
     * @param updateMap       Information to update
     * @param connectionStamp Information on the connected user
     * @return result of update
     */
    @CrossOrigin
    @PostMapping(value = "/api/user/admin/update", produces = "application/json")
    public Map<String, Object> updateAdminUser(@RequestBody Map<String, Object> updateMap,
                                               @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);

        Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);

        Long userId = ToolCast.getLong(updateMap, RestJsonConstants.CST_PARAM_USERID, 0L);
        String attribut = ToolCast.getString(updateMap, RestJsonConstants.CST_PARAM_ATTRIBUT, "");
        Object value = updateMap.get(RestJsonConstants.CST_PARAM_VALUE);

        Map<String, Object> payload = new HashMap<>();
        OperationUser operationUser = toghUserService.updateUser(userId, attribut, value);
        if (operationUser.toghUserEntity != null) {
            BaseSerializer serializer = factorySerializer.getFromEntity(operationUser.toghUserEntity);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            payload.put(RestJsonConstants.CST_USER, serializer.getMap(operationUser.toghUserEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        payload.put(RestJsonConstants.LOG_EVENTS, operationUser.listLogEvents);


        return payload;

    }

    /**
     * Disconnect a user
     *
     * @param updateMap       update information
     * @param connectionStamp Information on the connected user
     * @return result of update
     */
    @CrossOrigin
    @PostMapping(value = "/api/user/admin/disconnect", produces = "application/json")
    public Map<String, Object> disconnectUser(@RequestBody Map<String, Object> updateMap,
                                              @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);
        Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);
        Long userId = ToolCast.getLong(updateMap, RestJsonConstants.CST_PARAM_USERID, 0L);
        Map<String, Object> payload = new HashMap<>();
        OperationLoginUser operationUser = loginService.disconnectUser(userId);

        if (operationUser.toghUserEntity != null) {
            BaseSerializer serializer = factorySerializer.getFromEntity(operationUser.toghUserEntity);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            payload.put(RestJsonConstants.CST_USER, serializer.getMap(operationUser.toghUserEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        payload.put(RestJsonConstants.LOG_EVENTS, operationUser.listLogEvents);
        return payload;
    }


}

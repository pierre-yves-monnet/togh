package com.togh.restcontroller;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.LoginLogEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import com.togh.serialization.BaseSerializer;
import com.togh.serialization.FactorySerializer;
import com.togh.serialization.SerializerOptions;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.ToghUserService;
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
/* RestUserController */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestAdminUsersController {

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
    @GetMapping(value = "/api/admin/users/search", produces = "application/json")
    public Map<String, Object> searchAdminUser(
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_SENTENCE, required = false) String searchUserSentence,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_CONNECTED, required = false) boolean filterConnected,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_BLOCK, required = false) boolean filterBlock,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_ADMINSTRATOR, required = false) boolean filterAdministrator,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_PREMIUM, required = false) boolean filterPremium,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_EXCELLENCE, required = false) boolean filterExcellence,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
            @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isAdministratorConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);


        ToghUserService.CriteriaSearchUser criteriaSearch = new ToghUserService.CriteriaSearchUser();
        criteriaSearch.searchSentence = searchUserSentence;
        criteriaSearch.connected = filterConnected;
        criteriaSearch.block = filterBlock;
        criteriaSearch.administrator = filterAdministrator;
        criteriaSearch.premium = filterPremium;
        criteriaSearch.excellence = filterExcellence;

        Map<String, Object> payload = new HashMap<>();

        ToghUserService.SearchUsersResult searchUsers;
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
     * @param updateMap       Information to update
     * @param connectionStamp Information on the connected user
     * @return result of update
     */
    @CrossOrigin
    @PostMapping(value = "/api/admin/users/update", produces = "application/json")
    public Map<String, Object> updateAdminUser(@RequestBody Map<String, Object> updateMap,
                                               @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isAdministratorConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);

        Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);

        Long userId = ToolCast.getLong(updateMap, RestJsonConstants.PARAM_USERID, 0L);
        String attribut = ToolCast.getString(updateMap, RestJsonConstants.PARAM_ATTRIBUT, "");
        Object value = updateMap.get(RestJsonConstants.PARAM_VALUE);

        Map<String, Object> payload = new HashMap<>();
        ToghUserService.OperationUser operationUser = toghUserService.updateUser(userId, attribut, value);
        if (operationUser.toghUserEntity != null) {
            BaseSerializer serializer = factorySerializer.getFromEntity(operationUser.toghUserEntity);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            payload.put(RestJsonConstants.USER, serializer.getMap(operationUser.toghUserEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
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
    @PostMapping(value = "/api/admin/users/disconnect", produces = "application/json")
    public Map<String, Object> disconnectUser(@RequestBody Map<String, Object> updateMap,
                                              @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isAdministratorConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);
        Long timezoneOffset = ToolCast.getLong(updateMap, RestJsonConstants.PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);
        Long userId = ToolCast.getLong(updateMap, RestJsonConstants.PARAM_USERID, 0L);
        Map<String, Object> payload = new HashMap<>();
        LoginService.OperationLoginUser operationUser = loginService.disconnectUser(userId);

        if (operationUser.toghUserEntity != null) {
            BaseSerializer serializer = factorySerializer.getFromEntity(operationUser.toghUserEntity);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            payload.put(RestJsonConstants.USER, serializer.getMap(operationUser.toghUserEntity, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }
        payload.put(RestJsonConstants.LOG_EVENTS, operationUser.listLogEvents);
        return payload;
    }

    /**
     * @param connectionStamp Information on the connected user
     * @return result of update
     */
    @CrossOrigin
    @GetMapping(value = "/api/admin/users/stats", produces = "application/json")
    public Map<String, Object> usersStats(@RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);

        ToghUserService.StatisticsUsers statisticsUsers = toghUserService.statisticsOnUsers();
        return statisticsUsers.getMap();

    }

    @CrossOrigin
    @GetMapping(value = "/api/admin/users/searchloginlog", produces = "application/json")
    public Map<String, Object> searchAdminLogingLog(
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_SENTENCE, required = false) String searchUserSentence,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_OK, required = false) boolean filterOk,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_UNKNOWNUSER, required = false) boolean filterUnknownUser,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_BADPASSWORD, required = false) boolean filterBadPassword,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_UNDERATTACK, required = false) boolean filterUnderAttack,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_DATESTART, required = false) String filterDateStart,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_DATEEND, required = false) String filterDateEnd,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_TIMESTART, required = false) String filterTimeStart,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_TIMEEND, required = false) String filterTimeEnd,
            @RequestParam(name = RestJsonConstants.PARAM_SEARCHLOGINLOG_TIMEZONEOFFSET, required = false) Long timezoneOffset,
            @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isAdministratorConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.HTTPCODE_NOTCONNECTED);

        var criteriaSearchLogingLog = new ToghUserService.CriteriaSearchLogingLog();
        criteriaSearchLogingLog.searchSentence = searchUserSentence;
        criteriaSearchLogingLog.ok = filterOk;
        criteriaSearchLogingLog.unknownUser = filterUnknownUser;
        criteriaSearchLogingLog.badPassword = filterBadPassword;
        criteriaSearchLogingLog.underAttack = filterUnderAttack;

        criteriaSearchLogingLog.dateTimeStart = EngineTool.stringToDateTime(filterDateStart, timezoneOffset, filterTimeStart);
        criteriaSearchLogingLog.dateTimeEnd = EngineTool.stringToDateTime(filterDateEnd, timezoneOffset, filterTimeEnd);

        Map<String, Object> payload = new HashMap<>();

        var searchLogingLogResult = toghUserService.findLoginLogByCriteria(criteriaSearchLogingLog, 1, 50);


        List<Map<String, Object>> listLoginLogMap = new ArrayList<>();
        for (LoginLogEntity loginLogEntityIterator : searchLogingLogResult.listLogs) {
            BaseSerializer serializer = factorySerializer.getFromEntity(loginLogEntityIterator);
            SerializerOptions serializerOptions = new SerializerOptions(toghUser, timezoneOffset, SerializerOptions.ContextAccess.ADMIN);
            listLoginLogMap.add(serializer.getMap(loginLogEntityIterator, null, serializerOptions, factorySerializer, factoryUpdateGrantor));
        }

        payload.put(RestJsonConstants.LISTLOGINLOG, listLoginLogMap);
        payload.put(RestJsonConstants.PAGE, searchLogingLogResult.page);
        payload.put(RestJsonConstants.ITEMS_PER_PAGE, searchLogingLogResult.numberPerPage);
        payload.put(RestJsonConstants.NUMBER_OF_PAGES, (searchLogingLogResult.countLogConnection / searchLogingLogResult.numberPerPage) + 1);
        payload.put(RestJsonConstants.NUMBER_OF_ITEMS, searchLogingLogResult.countLogConnection);

        return payload;

    }
}


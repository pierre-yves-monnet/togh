package com.togh.restcontroller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.LoginService.OperationLoginUser;
import com.togh.service.ToghUserService;
import com.togh.service.ToghUserService.CriteriaSearchUser;
import com.togh.service.ToghUserService.OperationUser;
import com.togh.service.ToghUserService.SearchUsersResult;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestUserControler */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
public class RestUserController {

    @Autowired
    private FactoryService factoryService;

    @Autowired
    private ToghUserService toghUserService;

    @Autowired
    private LoginService loginService;
    
    
    /**
     * Call for the invitation for example, to search a user according some criteria. User should accept to publish some information
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param email
     * @param onlyNonInvitedUser
     * @param eventId
     * @param connectionStamp
     * @return
     */
    @CrossOrigin
    @GetMapping("/api/user/search")
    public Map<String, Object> searchUser(@RequestParam( RestJsonConstants.CST_PARAM_FIRST_NAME) String firstName, 
            @RequestParam( RestJsonConstants.CST_PARAM_LAST_NAME ) String lastName,
            @RequestParam( RestJsonConstants.CST_PARAM_PHONE_NUMBER) String phoneNumber,
            @RequestParam( RestJsonConstants.CST_PARAM_EMAIL) String email, 
            @RequestParam( RestJsonConstants.CST_PARAM_ONLY_NON_INVITED_USER) Boolean onlyNonInvitedUser,
            @RequestParam( RestJsonConstants.CST_INJSON_EVENTID ) Long eventId,
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUserEntity == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        Map<String, Object> payload = new HashMap<>();
        SearchUsersResult searchUsers;
        if (Boolean.TRUE.equals( onlyNonInvitedUser ) && eventId != null)
            searchUsers = factoryService.getToghUserService().searchUsersOutEvent( firstName, lastName, phoneNumber, email, eventId.longValue(), 0,20);
        else
            searchUsers = factoryService.getToghUserService().searchUsers( firstName, lastName, phoneNumber, email, 0,20);
        
        
        List<Map<String,Object >> listUsersMap = new ArrayList<>();
        for (ToghUserEntity toghUserEntityIterator : searchUsers.listUsers) {
            listUsersMap.add( toghUserEntityIterator.getMap( ContextAccess.SEARCH, timezoneOffset));
        }
        
        payload.put( RestJsonConstants.CST_LISTUSERS, listUsersMap);
        payload.put( RestJsonConstants.CST_COUNTUSERS, searchUsers.countUsers);
        payload.put( RestJsonConstants.CST_PAGE, searchUsers.page);
        payload.put( RestJsonConstants.CST_NUMBERPERPAGE, searchUsers.numberPerPage);

        return payload;

    }

    @CrossOrigin
    @GetMapping( value="/api/user/admin/search",  produces = "application/json")
    public Map<String, Object> searchAdminUser( 
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_SENTENCE, required = false) String searchUserSentence,            
           
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_CONNECTED, required = false) boolean filterConnected,             
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_BLOCK, required = false) boolean filterBlock, 
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_ADMINSTRATOR, required = false) boolean filterAdministrator,
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_PREMIUM, required = false) boolean filterPremium,
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_EXCELLENCE, required = false) boolean filterExcellence,
            @RequestParam( name=RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, required = false) Long timezoneOffset,
            
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUserEntity == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        

        CriteriaSearchUser criteriaSearch = new CriteriaSearchUser();
        criteriaSearch.searchSentence = searchUserSentence;
        criteriaSearch.connected = filterConnected;
        criteriaSearch.block = filterBlock;
        criteriaSearch.administrator = filterAdministrator;
        criteriaSearch.premium = filterPremium;
        criteriaSearch.excellence = filterExcellence;
        
        Map<String, Object> payload = new HashMap<>();
        
        SearchUsersResult searchUsers;
        searchUsers = toghUserService.findUserByCriterias( criteriaSearch, 0,20);
        
        
        List<Map<String,Object >> listUsersMap = new ArrayList<>();
        for (ToghUserEntity toghUserEntityIterator : searchUsers.listUsers) {
            listUsersMap.add( toghUserEntityIterator.getMap( ContextAccess.ADMIN, timezoneOffset));
        }
        
        payload.put( RestJsonConstants.CST_LISTUSERS, listUsersMap);
        payload.put( RestJsonConstants.CST_COUNTUSERS, searchUsers.countUsers);
        payload.put( RestJsonConstants.CST_PAGE, searchUsers.page);
        payload.put( RestJsonConstants.CST_NUMBERPERPAGE, searchUsers.numberPerPage);

        return payload;

    }

    /**
     * Update yourself
     * @param updateMap
     * @param connectionStamp
     * @return
     */
    @CrossOrigin
    @PostMapping(value="/api/user/update", produces = "application/json")
    public Map<String, Object> updateUser( @RequestBody Map<String, Object> updateMap,        
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUserEntity == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        // Long timezoneOffset             = RestTool.getLong(updateMap, "timezoneoffset", 0L);
        Long timezoneOffset             = RestTool.getLong(updateMap, RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);

        String attribut                 = RestTool.getString(updateMap, RestJsonConstants.CST_PARAM_ATTRIBUT, "");
        Object value                    = updateMap.get( RestJsonConstants.CST_PARAM_VALUE);
        
        Map<String, Object> payload = new HashMap<>();
        OperationUser operationUser = toghUserService.updateUser( toghUserEntity.getId(), attribut, value);
        
        payload.put( RestJsonConstants.CST_USER, operationUser.toghUserEntity==null ? null :  operationUser.toghUserEntity.getMap( ContextAccess.ADMIN, timezoneOffset));
        payload.put( RestJsonConstants.CST_LISTLOGEVENTS, operationUser.listLogEvents );

        return payload;
    }
  
    
    @CrossOrigin
    @PostMapping(value="/api/user/admin/update", produces = "application/json")
    public Map<String, Object> updateAdminUser( @RequestBody Map<String, Object> updateMap,        
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUserEntity == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        // Long timezoneOffset             = RestTool.getLong(updateMap, "timezoneoffset", 0L);
        Long timezoneOffset             = RestTool.getLong(updateMap, RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);

        Long userId                     = RestTool.getLong(updateMap, RestJsonConstants.CST_PARAM_USERID, 0L);
        String attribut                 = RestTool.getString(updateMap, RestJsonConstants.CST_PARAM_ATTRIBUT, "");
        Object value                    = updateMap.get( RestJsonConstants.CST_PARAM_VALUE);
        
        Map<String, Object> payload = new HashMap<>();
        OperationUser operationUser = toghUserService.updateUser( userId, attribut, value);
        
        payload.put( RestJsonConstants.CST_USER, operationUser.toghUserEntity==null ? null :  operationUser.toghUserEntity.getMap( ContextAccess.ADMIN, timezoneOffset));
        payload.put( RestJsonConstants.CST_LISTLOGEVENTS, operationUser.listLogEvents );


        return payload;

    }
    
    @CrossOrigin
    @PostMapping(value="/api/user/admin/disconnect", produces = "application/json")
    public Map<String, Object> disconnectUser( @RequestBody Map<String, Object> updateMap, 
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);
        Long timezoneOffset             = RestTool.getLong(updateMap, RestJsonConstants.CST_PARAM_SEARCHUSER_TIMEZONEOFFSET, 0L);
        // Long timezoneOffset             = RestTool.getLong(updateMap, "timezoneoffset", 0L);
        Long userId                     = RestTool.getLong(updateMap, RestJsonConstants.CST_PARAM_USERID, 0L);
        Map<String, Object> payload = new HashMap<>();
        OperationLoginUser operationUser = loginService.disconnectUser( userId );
        
        payload.put( RestJsonConstants.CST_USER, operationUser.toghUserEntity==null ? null :  operationUser.toghUserEntity.getMap( ContextAccess.ADMIN, timezoneOffset));

        payload.put( RestJsonConstants.CST_LISTLOGEVENTS, operationUser.listLogEvents );

        
        return payload;
 }
    
    
}

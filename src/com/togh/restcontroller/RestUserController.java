package com.togh.restcontroller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.service.FactoryService;
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

    
    @CrossOrigin
    @GetMapping("/api/user/search")
    public Map<String, Object> searchUser(@RequestParam( RestJsonConstants.CST_PARAM_FIRST_NAME) String firstName, 
            @RequestParam( RestJsonConstants.CST_PARAM_LAST_NAME ) String lastName,
            @RequestParam( RestJsonConstants.CST_PARAM_PHONE_NUMBER) String phoneNumber,
            @RequestParam( RestJsonConstants.CST_PARAM_EMAIL) String email, 
            @RequestParam( RestJsonConstants.CST_PARAM_ONLY_NON_INVITED_USER) Boolean onlyNonInvitedUser,
            @RequestParam( RestJsonConstants.CST_INJSON_EVENTID ) Long eventId,
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUser == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        Map<String, Object> payload = new HashMap<>();
        SearchUsersResult searchUsers;
        if (Boolean.TRUE.equals( onlyNonInvitedUser ) && eventId != null)
            searchUsers = factoryService.getToghUserService().searchUsersOutEvent( firstName, lastName, phoneNumber, email, eventId.longValue(), 0,20);
        else
            searchUsers = factoryService.getToghUserService().searchUsers( firstName, lastName, phoneNumber, email, 0,20);
        
        
        List<Map<String,Object >> listUsersMap = new ArrayList<>();
        for (ToghUserEntity togUser : searchUsers.listUsers) {
            listUsersMap.add( togUser.getMap( ContextAccess.SEARCH));
        }
        
        payload.put( RestJsonConstants.CST_USERS, listUsersMap);
        payload.put( RestJsonConstants.CST_COUNTUSERS, searchUsers.countUsers);
        payload.put( RestJsonConstants.CST_PAGE, searchUsers.page);
        payload.put( RestJsonConstants.CST_NUMBERPERPAGE, searchUsers.numberPerPage);

        return payload;

    }



}

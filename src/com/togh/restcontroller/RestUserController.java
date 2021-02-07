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
    public Map<String, Object> searchUser(@RequestParam("firstName") String firstName, 
            @RequestParam("lastName") String lastName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email, 
            @RequestParam("onlyNonInvitedUser") Boolean onlyNonInvitedUser,
            @RequestParam("eventid") Long eventId,
            @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);
        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

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
        
        payload.put("users", listUsersMap);
        payload.put("countusers", searchUsers.countUsers);
        payload.put("page", searchUsers.page);
        payload.put("numberperpage", searchUsers.numberPerPage);

        return payload;

    }



}

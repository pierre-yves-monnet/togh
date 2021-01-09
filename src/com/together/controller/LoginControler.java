package com.together.controller;
/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login */
/*                                                                      */
/* -------------------------------------------------------------------- */


import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.LoginService.LoginStatus;

@RestController
public class LoginControler {
	
    @Autowired
    private LoginService loginService;

    @Autowired
    private EventService eventService;
  
  
    @PostMapping(value = "/login",produces = "application/json")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = loginService.connectWithEmail(userData.get("email"), userData.get("password"));
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return loginStatus.getMap();
    }
    
    @GetMapping(value = "/logout",produces = "application/json")
    public String logout( @CookieValue(value="togh") String connectionStamp) {
        loginService.disconnectUser(connectionStamp);
        return "{}";
    }
  
    @PostMapping(value = "/registernewuser",produces = "application/json")
    @ResponseBody
    public Map<String, Object> registerNewUser(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = loginService.registerNewUser(userData.get("email"), userData.get("firstname"), userData.get("lastname"), userData.get("password"));
        if (loginStatus.isCorrect)
            loginStatus = loginService.connectNoVerification(userData.get("email"));
            
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return loginStatus.getMap();
    }
  
    
    /**
     * Is the user is connected ?
     * @param connectionStamp
     * @return userId or null if not connected
     */
    private Long isConnected(String connectionStamp) {
        // LoginService loginService = serciceAccessor.getLoginService();
        return loginService.isConnected(connectionStamp);

    }
}

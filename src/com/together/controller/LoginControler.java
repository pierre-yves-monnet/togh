package com.together.controller;
/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login */
/*                                                                      */
/* -------------------------------------------------------------------- */


import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.together.entity.EndUserEntity.SourceUserEnum;
import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.LoginService.LoginStatus;

@RestController
public class LoginControler {
	
    @Autowired
    private LoginService loginService;

    @Autowired
    private EventService eventService;
  
    private final static String googleClientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com";
  
    /**
     * Login from the portal via a email / password
     * @param userData
     * @param response
     * @return
     */
    @PostMapping(value = "/login",produces = "application/json")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = loginService.connectWithEmail(userData.get("email"), userData.get("password"));
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return loginStatus.getMap();
    }
    
    /**
     * Logout
     * @param connectionStamp
     * @return
     */
    @GetMapping(value = "/logout",produces = "application/json")
    public String logout( @CookieValue(value="togh") String connectionStamp) {
        loginService.disconnectUser(connectionStamp);
        return "{}";
    }
  
    
    /**
     * Login via Google
     * @param idTokenGoogle
     * @param response
     * @return
     */
    // visit https://developers.google.com/identity/sign-in/web/backend-auth#send-the-id-token-to-your-server    
    @GetMapping(value="/logingoogle" ) 
    public Map<String, Object> loginGoogle(@RequestParam("idtokengoogle") String idTokenGoogle, HttpServletResponse response) {     
        LoginStatus loginStatus = new LoginStatus();
        try {

            final NetHttpTransport transport = new NetHttpTransport();
            final GsonFactory jsonFactory = new GsonFactory();

            final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Arrays.asList(googleClientId))
                    // To learn about getting a Server Client ID, see this link
                    // https://developers.google.com/identity/sign-in/android/start
                    // And follow step 4
                    // .setIssuer("https://accounts.google.com").build();
                    // .setIssuer("http://localhost:8080/bonita")
                    .build();

  
            final GoogleIdToken idToken = verifier.verify(idTokenGoogle);
             if (idToken != null) {
                final Payload payload = idToken.getPayload();
                String userName = (String) payload.get("name");
                String email = (String) payload.get("email");
                String firstName=(String) payload.get("given_name");
                String lastName=(String) payload.get("family_name");
                String picture = (String) payload.get("picture");
                // fr, en..
                String language= (String) payload.get("locale");
                loginStatus = loginService.connectSSO( email, true);
                if (! loginStatus.isConnected) {
                    // register it now !
                    loginStatus = loginService.registerNewUser( email, firstName, lastName, /** No password */ null, SourceUserEnum.GOOGLE);
                    if (loginStatus.isCorrect)
                        loginStatus = loginService.connectNoVerification(email );
                }

                Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
                response.addCookie(cookieConnection);
                return loginStatus.getMap();
             }
        } catch(Exception e) {
            // TODO ToghEvent
        }
        return loginStatus.getMap();
        
    }
    
    /**
     * Register a new user
     * @param userData
     * @param response
     * @return
     */
    @PostMapping(value = "/registernewuser",produces = "application/json")
    @ResponseBody
    public Map<String, Object> registerNewUser(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = loginService.registerNewUser(userData.get("email"), userData.get("firstname"), userData.get("lastname"), userData.get("password"), SourceUserEnum.PORTAL);
        if (loginStatus.isCorrect)
            loginStatus = loginService.connectNoVerification(userData.get("email"));
            
        Cookie cookieConnection = new Cookie("togh", loginStatus.connectionStamp);
        response.addCookie(cookieConnection);
        return loginStatus.getMap();
    }
  
}

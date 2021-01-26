package com.togh.controller;
/* -------------------------------------------------------------------- */
/*                                                                      */
/* Login */
/*                                                                      */
/* -------------------------------------------------------------------- */


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.service.EventService;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.LoginService.LoginStatus;

@RestController
public class RestLoginControler {
   
    private Logger logger = Logger.getLogger(RestLoginControler.class.getName());
    private final static String logHeader = "LoginControler: ";
    
    @Autowired
    private FactoryService factoryService;


    private final static String googleClientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com";
  
    /**
     * Login from the portal via a email / password
     * @param userData
     * @param response
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/login",produces = "application/json")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = factoryService.getLoginService().connectWithEmail(userData.get("email"), userData.get("password"));
        return loginStatus.getMap();
    }
    
    /**
     * Logout
     * @param connectionStamp
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/logout",produces = "application/json")
    public String logout( @RequestHeader("Authorization") String connectionStamp) {
        factoryService.getLoginService().disconnectUser(connectionStamp);
        return "{}";
    }
  
    
    /**
     * Login via Google
     * @param idTokenGoogle
     * @param response
     * @return
     */
    // visit https://developers.google.com/identity/sign-in/web/backend-auth#send-the-id-token-to-your-server
    @CrossOrigin
    @GetMapping(value="/api/logingoogle" ) 
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
                loginStatus = factoryService.getLoginService().connectSSO( email, true);
                if (! loginStatus.isConnected) {
                    // register it now !
                    loginStatus = factoryService.getLoginService().registerNewUser( email, firstName, lastName, /** No password */ null, SourceUserEnum.GOOGLE);
                    if (loginStatus.isCorrect)
                        loginStatus = factoryService.getLoginService().connectNoVerification(email );
                }

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
    @CrossOrigin
    @PostMapping(value = "/api/login/registernewuser",produces = "application/json")
    @ResponseBody
    public Map<String, Object> registerNewUser(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginStatus loginStatus = factoryService.getLoginService().registerNewUser(userData.get("email"), userData.get("firstname"), userData.get("lastname"), userData.get("password"), SourceUserEnum.PORTAL);
        if (loginStatus.isCorrect)
            loginStatus = factoryService.getLoginService().connectNoVerification(userData.get("email"));
            
        return loginStatus.getMap();
    }
  
    /**
     * 
     * @param message
     * @return
     */
    @CrossOrigin
    @GetMapping(value = "/api/ping",produces = "application/json")
    public Map<String,Object>  ping( @RequestParam(required = false) String message) {
        logger.info(logHeader+"Ping!");
        Map<String,Object> result = new HashMap<>();
        result.put("now", LocalDateTime.now());
        if (message!=null)
            result.put("message", message);
        
        return result;
    }
  
}

/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;
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

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
import com.togh.entity.ToghUserEntity.TypePictureEnum;
import com.togh.service.ApiKeyService;
import com.togh.service.FactoryService;
import com.togh.service.LoginService;
import com.togh.service.LoginService.LoginResult;
import com.togh.service.LoginService.LoginStatus;

@RestController
@RequestMapping("togh")
public class RestLoginController {
   
    private Logger logger = Logger.getLogger(RestLoginController.class.getName());
    private final static String logHeader = RestLoginController.class.getSimpleName()+": ";
    
    @Autowired
    private FactoryService factoryService;

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private ApiKeyService apiKeyService;

    @Value("${dictionary.lang-path}")
    private String propertyDictionaryPath;

    private final static String googleClientId="393158240427-ltcco0ve39nukr7scbbdcm4r36mi4v4n.apps.googleusercontent.com";
  
    /**
     * Login from the portal via a email / password
     * Nota: the Administrator is created at the startup. There is here nothing special to connect him
     * See ToghUserService.TOGHADMIN_USERNAME
     * @param userData
     * @param response
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "api/login",produces = "application/json")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginResult loginStatus = loginService.connectWithEmail(userData.get("email"), userData.get("password"));
        Map<String, Object> finalStatus = new HashMap<>();
        finalStatus.putAll( loginStatus.getMap());
        if (loginStatus.isConnected) {
            finalStatus.put("apikeys", apiKeyService.getApiKeyForUser( loginStatus.userConnected));
        }
        return finalStatus;
    }
    
    /**
     * Logout
     * @param connectionStamp    Information on the connected user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/logout",produces = "application/json")
    public String logout( @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
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
    @CrossOrigin
    @GetMapping(value="/api/logingoogle" ) 
    public Map<String, Object> loginGoogle(@RequestParam("idtokengoogle") String idTokenGoogle, HttpServletResponse response) {     
        LoginResult loginStatus = new LoginResult();
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
                // String userName = (String) payload.get("name");
                String email = (String) payload.get("email");
                String firstName=(String) payload.get("given_name");
                String lastName=(String) payload.get("family_name");
                String picture = (String) payload.get("picture");
                // fr, en..
                // String language= (String) payload.get("locale");
                loginStatus = loginService.connectSSO( email, true);
                if (! loginStatus.isConnected) {
                    // register it now !
                    loginStatus = loginService.registerNewUser( email, firstName, lastName, /** No password */ null, SourceUserEnum.GOOGLE, TypePictureEnum.URL, picture);
                    if (loginStatus.status == LoginStatus.OK)
                        loginStatus = loginService.connectNoVerification( email );
                }

                return loginStatus.getMap();
             }
        } catch(Exception e) {
            logger.info(logHeader+"Error when creating a Google user "+e.toString());

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
        LoginResult loginStatus = loginService.registerNewUser(userData.get("email"), 
                userData.get("firstName"), 
                userData.get("lastName"), 
                userData.get("password"), 
                SourceUserEnum.PORTAL,
                TypePictureEnum.TOGH,
                null
                );
        if (loginStatus.status == LoginStatus.OK)
            loginStatus = loginService.connectNoVerification(userData.get("email"));
            
        return loginStatus.getMap();
    }
  
    
    /**
     * Register a new user
     * @param userData
     * @param response
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/login/lostmypassword",produces = "application/json")
    @ResponseBody
    public Map<String, Object> lostMyPassword(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginResult loginStatus = loginService.lostMyPassword(userData.get("email"));
        Map<String, Object> payLoad=new HashMap<>();
        switch( loginStatus.status ) {
            case BADEMAIL:
                payLoad.put("status", "BADEMAIL");
                break;
            case SERVERISSUE:
                payLoad.put("status", "SERVERISSUE");
                break;
            case OK:
                payLoad.put("status", "OK");
                break;
            default:
                payLoad.put("status", "SERVERISSUE");
                break;
        }                    

        return payLoad;
    }
    
    
    
    @CrossOrigin
    @PostMapping(value = "/api/login/resetPasswordInfo",produces = "application/json")
    @ResponseBody
    public Map<String, Object> resetPasswordInfo(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginResult loginStatus = loginService.getFromUUID(userData.get("uuid"));
        Map<String, Object> finalStatus = new HashMap<>();
        finalStatus.putAll( loginStatus.getMap());
        return finalStatus;
    }
 
    
    @CrossOrigin
    @PostMapping(value = "/api/login/resetPassword",produces = "application/json")
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> userData, HttpServletResponse response) {
        LoginResult loginStatus = loginService.changePasswordAndConnect(userData.get("uuid"), userData.get("password"));
        Map<String, Object> finalStatus = new HashMap<>();
        finalStatus.putAll( loginStatus.getMap());
        return finalStatus;
    }

    /**
     *
     * @param userData
     * @param connectionStamp    Information on the connected user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/login/changePassword",produces = "application/json")
    @ResponseBody
    public Map<String, Object> changePassword(@RequestBody Map<String, String> userData,
            @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp ) {
        
        ToghUserEntity toghUserEntity = factoryService.getLoginService().isConnected(connectionStamp);
        if (toghUserEntity == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        LoginResult loginStatus = loginService.changePassword(toghUserEntity, userData.get("password"));
        Map<String, Object> finalStatus = new HashMap<>();
        finalStatus.putAll( loginStatus.getMap());
        return finalStatus;
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

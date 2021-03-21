package com.togh.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.admin.translate.TranslateDictionary;
import com.togh.admin.translate.TranslateDictionary.TranslateResult;
import com.togh.entity.ToghUserEntity;
import com.togh.service.FactoryService;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminTranslator */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
public class RestAdminTranslator {
    
    @Autowired
    private FactoryService factoryService;

    @Autowired
    private TranslateDictionary translateDictionnary;
    
    
    @CrossOrigin
    @GetMapping(value ="/api/admin/translator/status",  produces = "application/json")
      public TranslateResult translatorStatus( @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        
        getAndControlUser(connectionStamp);
   
        return translateDictionnary.check(); 
        
    }

    @CrossOrigin
    @PostMapping(value ="/api/admin/translator/complete",  produces = "application/json")
      public TranslateResult translator( @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        
        getAndControlUser(connectionStamp);

        return translateDictionnary.complete(); 
        
    }
    
    /**
     * check that the user can access this RestAdminTranslator
     * @param connectionStamp
     * @return
     * @throws ResponseStatusException
     */
    private ToghUserEntity getAndControlUser( String connectionStamp ) {
        ToghUserEntity toghUser = factoryService.getLoginService().isConnected(connectionStamp);

        if (toghUser == null)
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTCONNECTED);

        // check if the user is an administrator
        if  ( ! factoryService.getLoginService().isAdministrator( toghUser)) {
            throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, RestHttpConstant.CST_HTTPCODE_NOTANADMINISTRATOR);
        }
        return toghUser;
    }
}

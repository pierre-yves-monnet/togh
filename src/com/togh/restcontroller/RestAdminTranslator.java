package com.togh.restcontroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.togh.admin.translate.TranslateDictionary;
import com.togh.admin.translate.TranslateDictionary.TranslateResult;
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
      public TranslateResult translatorStatus( @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);

        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        // check if the user is an administrator
        if  ( ! factoryService.getLoginService().isAdministrator( userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not an adminstrator");
            
        }
        
        return translateDictionnary.check(); 
        
    }

    @CrossOrigin
    @PostMapping(value ="/api/admin/translator/complete",  produces = "application/json")
      public TranslateResult translator( @RequestHeader("Authorization") String connectionStamp) {
        Long userId = factoryService.getLoginService().isConnected(connectionStamp);

        if (userId == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not connected");

        // check if the user is an administrator
        if  ( ! factoryService.getLoginService().isAdministrator( userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Not an adminstrator");
            
        }
        
        return translateDictionnary.complete(); 
        
    }
}

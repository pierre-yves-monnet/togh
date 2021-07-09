/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.togh.admin.translate.TranslateDictionary;
import com.togh.admin.translate.TranslateDictionary.TranslateResult;
import com.togh.service.LoginService;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminTranslator */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
public class RestAdminTranslator {
    
    @Autowired
    private LoginService loginService;

    @Autowired
    private TranslateDictionary translateDictionnary;
    
    
    @CrossOrigin
    @GetMapping(value ="/api/admin/translator/status",  produces = "application/json")
      public TranslateResult translatorStatus( @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        
        loginService.isAdministratorConnected(connectionStamp);
   
        return translateDictionnary.check(); 
        
    }

    @CrossOrigin
    @PostMapping(value ="/api/admin/translator/complete",  produces = "application/json")
      public TranslateResult translator( @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        
        loginService.isAdministratorConnected(connectionStamp);

        return translateDictionnary.complete(); 
        
    }
    
    
}

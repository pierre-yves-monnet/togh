/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.togh.service.LoginService;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminTranslator */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
public class RestAdminInfo {

    @Autowired
    private LoginService loginService;
    
    @CrossOrigin
    @GetMapping(value = "/api/admin/info", produces = "application/json")
    public List<Map<String,Object>> getApiKeys(@RequestHeader(RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {

        loginService.isAdministratorConnected(connectionStamp);
        List<Map<String,Object>> listInformations = new ArrayList<>();

        listInformations.add( addInformation("java version", System.getProperty("java.version")));
        
        return listInformations;
    }
    
    public Map<String,Object> addInformation( String name, Object value) {
        Map<String,Object> info = new HashMap<>();
        info.put("name", name);
        info.put("value", value);
        return info;
    }
}
    

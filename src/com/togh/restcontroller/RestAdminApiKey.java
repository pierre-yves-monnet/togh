/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.APIKeyEntity;
import com.togh.service.ApiKeyService;
import com.togh.service.LoginService;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminTranslator */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestAdminApiKey {
    
    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiKeyService apiKeyService;

    /**
     *
     * @param connectionStamp    Information on the connected user
     * @return
     */
    @CrossOrigin
    @GetMapping(value ="/api/admin/apikey/get",  produces = "application/json")
      public List<APIKeyEntity> getApiKeys( @RequestHeader( RestJsonConstants.CST_PARAM_AUTHORIZATION ) String connectionStamp) {
        
        loginService.isAdministratorConnected(connectionStamp);
        return apiKeyService.getListApiKeys();
            
    }

    /**
     *
     * @param updateMap
     * @param connectionStamp    Information on the connected user
     * @return
     */
    @CrossOrigin
    @PostMapping(value = "/api/admin/apikey/update", produces = "application/json")
    @ResponseBody
    public Map<String, Object> updateKey(
            @RequestBody Map<String, Object> updateMap,
            @RequestHeader(RestJsonConstants.CST_PARAM_AUTHORIZATION) String connectionStamp) {
        
        Map<String,Object> payload = new HashMap<>();
        loginService.isAdministratorConnected(connectionStamp);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> listApiKey = RestTool.getList(updateMap, "listkeys", new ArrayList<>() );
        List<LogEvent> listLogEvent = apiKeyService.updateKeys( listApiKey );
        
        payload.put( RestJsonConstants.CST_LIST_LOG_EVENTS, LogEventFactory.getJson(listLogEvent));
        
        return payload;
    }
}

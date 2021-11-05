/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.restcontroller;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEventFactory;
import com.togh.entity.APIKeyEntity;
import com.togh.service.*;
import com.togh.tool.ToolCast;
import com.togh.tool.email.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* RestAdminEmail */
/*                                                                      */
/* -------------------------------------------------------------------- */

@RestController
@RequestMapping("togh")
public class RestAdminEmail {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private FactoryService factoryService;

    /**
     * @param connectionStamp Information on the connected user
     * @return List of APIKeyEntity
     */
    @CrossOrigin
    @GetMapping(value = "/api/admin/email/get", produces = "application/json")
    public List<APIKeyEntity> getApiKeys(@RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

        loginService.isAdministratorConnected(connectionStamp);
        return apiKeyService.getListApiKeys(ApiKey.listKeysEmail);

    }

    /**
     * @param updateMap       information to update
     * @param connectionStamp Information on the connected user
     * @return status of key updated
     */
    @CrossOrigin
    @PostMapping(value = "/api/admin/email/update", produces = "application/json")
    @ResponseBody
    public Map<String, Object> updateKey(
            @RequestBody Map<String, Object> updateMap,
            @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

        loginService.isAdministratorConnected(connectionStamp);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listApiKey = ToolCast.getList(updateMap, "listKeys", new ArrayList<>());
        List<LogEvent> listLogEvent = apiKeyService.updateKeys(listApiKey);

        Map<String, Object> payload = new HashMap<>();
        payload.put(RestJsonConstants.LOG_EVENTS, LogEventFactory.getJson(listLogEvent));

        return payload;
    }

    /**
     * @param updateMap       information to test the email
     * @param connectionStamp Information on the connected user
     * @return status of operation
     */
    @CrossOrigin
    @PostMapping(value = "/api/admin/email/test", produces = "application/json")
    @ResponseBody
    public Map<String, Object> testEmail(
            @RequestBody Map<String, Object> updateMap,
            @RequestHeader(RestJsonConstants.PARAM_AUTHORIZATION) String connectionStamp) {

        Map<String, Object> payload = new HashMap<>();
        loginService.isAdministratorConnected(connectionStamp);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listApiKey = ToolCast.getList(updateMap, "listKeys", new ArrayList<>());
        String emailTo = (String) updateMap.get("sendEmailTo");
        // send an email
        SendEmail sendEmail = new SendEmail();
        LocalSmtpService localSmtpService = new LocalSmtpService(listApiKey);
        List<LogEvent> listLogEvent = sendEmail.sendOneEmail(emailTo,
                null,
                "Test from Togh",
                "This is a test from Togh",

                localSmtpService);

        payload.put(RestJsonConstants.LOG_EVENTS, LogEventFactory.getJson(listLogEvent));

        return payload;
    }

    private static class LocalSmtpService implements SmtpKeyService {
        final Map<String, Object> mapKeys = new HashMap<>();

        LocalSmtpService(List<Map<String, Object>> listKeys) {
            for (Map<String, Object> recordKey : listKeys) {
                this.mapKeys.put((String) recordKey.get("name"), recordKey.get("keyApi"));
            }
        }

        @Override
        public String getSmtpHost() {
            return ToolCast.getString(mapKeys, ApiKey.SMTP_HOST.getName(), "");
        }

        @Override
        public int getSmtpPort() {
            return ToolCast.getLong(mapKeys, ApiKey.SMTP_PORT.getName(), 0L).intValue();
        }

        @Override
        public String getSmtpUserName() {
            return ToolCast.getString(mapKeys, ApiKey.SMTP_USER_NAME.getName(), "");
        }

        @Override
        public String getSmtpUserPassword() {
            return ToolCast.getString(mapKeys, ApiKey.SMTP_USER_PASSWORD.getName(), "");
        }

        @Override
        public String getSmtpFrom() {
            return ToolCast.getString(mapKeys, ApiKey.SMTP_FROM.getName(), "");
        }
    }
}

/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.entity.APIKeyEntity;
import com.togh.entity.APIKeyEntity.PrivilegeKeyEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.repository.ApiKeyEntityRepository;

@Service
public class ApiKeyService {

    private static final String HTTP_TOGH_SERVER = "HttpToghServer";

    private static final String SMTP_USER_PASSWORD = "SmtpUserPassword";

    private static final String SMTP_USER_NAME = "SmtpUserName";

    private static final String SMTP_PORT = "StmpPort";

    private static final String SMTP_FROM = "SmtpFrom";

    private static final String SMTP_HOST = "SmtpHost";

    private static final String GEOCODE_API_KEY = "geocodeAPIKey";

    private static final String GOOGLE_API_KEY = "googleAPIKey";

    private static final String TRANSLATE_KEY_API = "TranslateKeyAPI";

    @Autowired
    ApiKeyEntityRepository apiKeyRepository;

    public List<String> listKeysSystem = Arrays.asList(TRANSLATE_KEY_API, HTTP_TOGH_SERVER, SMTP_HOST, SMTP_FROM, SMTP_PORT, SMTP_USER_NAME, SMTP_USER_PASSWORD);
    public List<String> listKeysBrowser = Arrays.asList(GOOGLE_API_KEY, GEOCODE_API_KEY);

    /**
     * A premium API Key is not limited as the FREE key are.
     */
    public List<PrivilegeKeyEnum> listSuffixPrivilege = Arrays.asList(PrivilegeKeyEnum.PREMIUM, PrivilegeKeyEnum.FREE);

    private static final LogEvent eventUnknowCode = new LogEvent(ApiKeyService.class.getName(), 1, Level.ERROR, "Unknow code", "This APIKey is unknow",
            "A code is unknow, and can't be updated in the database", "Verify the code");
    private static final LogEvent eventKeysUpdated = new LogEvent(ApiKeyService.class.getName(), 2, Level.SUCCESS, "Keys updated", "API Key are updated with success");

    @PostConstruct
    public void init() {
        // Verify that all key are here
        for (String codeApi : listKeysSystem) {
            APIKeyEntity codeApiEntity = apiKeyRepository.findByName(codeApi);
            if (codeApiEntity == null) {
                codeApiEntity = new APIKeyEntity();
                codeApiEntity.setName(codeApi);
                codeApiEntity.setPrivilegeKey(PrivilegeKeyEnum.PREMIUM);
                apiKeyRepository.save(codeApiEntity);
            }
        }
        for (String codeApi : listKeysBrowser) {
            for (PrivilegeKeyEnum priviledge : listSuffixPrivilege) {
                APIKeyEntity codeApiEntity = apiKeyRepository.findByName(getFinalCode(codeApi, priviledge));
                if (codeApiEntity == null) {
                    codeApiEntity = new APIKeyEntity();
                    codeApiEntity.setName(codeApi + "_" + priviledge.toString());
                    codeApiEntity.setPrivilegeKey(priviledge);
                    apiKeyRepository.save(codeApiEntity);
                }
            }
        }
    }

    /**
     * @return
     */
    public List<APIKeyEntity> getListApiKeys() {
        List<APIKeyEntity> listKey = new ArrayList<>();

        for (String codeApi : listKeysSystem) {
            APIKeyEntity codeApiEntity = apiKeyRepository.findByName(codeApi);
            if (codeApiEntity != null) {
                listKey.add(codeApiEntity);
            }
        }

        for (String codeApi : listKeysBrowser) {
            for (PrivilegeKeyEnum priviledge : listSuffixPrivilege) {
                APIKeyEntity codeApiEntity = apiKeyRepository.findByName(getFinalCode(codeApi, priviledge));
                if (codeApiEntity != null) {
                    listKey.add(codeApiEntity);
                }
            }
        }
        return listKey;
    }

    /**
     * Update the list of KEY
     * 
     * @param listApiKey
     * @return
     */
    public List<LogEvent> updateKeys(List<Map<String, Object>> listApiKey) {
        List<LogEvent> listLogEvent = new ArrayList<>();
        for (Map<String, Object> oneKey : listApiKey) {
            APIKeyEntity codeApiEntity = apiKeyRepository.findByName((String) oneKey.get("name"));
            if (codeApiEntity != null) {
                codeApiEntity.setKeyApi((String) oneKey.get("keyApi"));
                apiKeyRepository.save(codeApiEntity);
            } else
                listLogEvent.add(new LogEvent(eventUnknowCode, "Code[" + oneKey.get("name") + "]"));
        }
        if (listLogEvent.isEmpty())
            listLogEvent.add(eventKeysUpdated);
        return listLogEvent;
    }

    /**
     * Return all the key according the user, and it's accredidation
     * 
     * @param toghUser
     * @return
     */
    public Map<String, String> getApiKeyForUser(ToghUserEntity toghUser) {
        Map<String, String> result = new HashMap<>();

        PrivilegeKeyEnum priviledge = PrivilegeKeyEnum.FREE;
        // SubscriptionUserEnum { FREE, PREMIUM, ILLIMITED }
        if ((toghUser.getSubscriptionUser() == SubscriptionUserEnum.PREMIUM)
                || (toghUser.getSubscriptionUser() == SubscriptionUserEnum.EXCELLENCE))
            priviledge = PrivilegeKeyEnum.PREMIUM;

        for (String codeApi : listKeysBrowser) {
            APIKeyEntity codeApiEntity = apiKeyRepository.findByName(getFinalCode(codeApi, priviledge));
            if (codeApiEntity != null)
                result.put(codeApi, codeApiEntity.getKeyApi());
        }
        return result;
    }

    /**
     * Return the Translate Key API
     * 
     * @return
     */
    public String getApiKeyGoogleTranslate() {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(TRANSLATE_KEY_API);
        if (codeApiEntity != null) {
            return codeApiEntity.getKeyApi();
        }
        return null;
    }

    public String getHttpToghServer(String defaultHttp) {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(HTTP_TOGH_SERVER);
        if (codeApiEntity != null) {
            return codeApiEntity.getKeyApi();
        }
        return defaultHttp;
    }
    
    /**
     * ReturnSmtpHostName
     * 
     * @return
     */
    public String getSmtpHost() {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(SMTP_HOST);
        if (codeApiEntity != null) {
            return codeApiEntity.getKeyApi();
        }
        return null;
    }

    /**
     * @return
     */
    public String getSmtpFrom() {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(SMTP_FROM);
        if (codeApiEntity != null) {
            return codeApiEntity.getKeyApi();
        }
        return null;
    }

    /**
     * getSmtpPort
     * 
     * @return
     */
    public Long getSmtpPort() {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(SMTP_PORT);
        if (codeApiEntity != null) {
            try {
                return Long.parseLong(codeApiEntity.getKeyApi());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public String getSmtpUserName() {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(SMTP_USER_NAME);
        if (codeApiEntity != null) {
            String key = codeApiEntity.getKeyApi();
            if (key != null && key.isEmpty())
                return null;
            return key;
        }
        return null;
    }

    /**
     * @return
     */
    public String getSmtpUserPassword() {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(SMTP_USER_PASSWORD);
        if (codeApiEntity != null) {
            String key = codeApiEntity.getKeyApi();
            if (key != null && key.isEmpty())
                return null;
            return key;
        }
        return null;
    }

    /**
     * getFinalCode
     * 
     * @param codeApi
     * @param priviledge
     * @return
     */
    private String getFinalCode(String codeApi, PrivilegeKeyEnum priviledge) {
        return codeApi + "_" + priviledge.toString();
    }
}

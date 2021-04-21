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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.entity.APIKeyEntity;
import com.togh.entity.APIKeyEntity.PrivilegeKeyEnum;
import com.togh.repository.ApiKeyEntityRepository;

@Service
public class ApiKeyService {

    private static final String TRANSLATE_KEY_API = "TranslateKeyAPI";

    @Autowired
    ApiKeyEntityRepository apiKeyRepository;

    public List<String> listKeysPremium = Arrays.asList(TRANSLATE_KEY_API);

    private static final LogEvent eventUnknowCode = new LogEvent(ApiKeyService.class.getName(), 1, Level.ERROR, "Unknow code", "This APIKey is unknow",
            "A code is unknow, and can't be updated in the database", "Verify the code");

    @PostConstruct
    public void init() {
        // Verify that all key are here
        for (String codeApi : listKeysPremium) {
            APIKeyEntity codeApiEntity = apiKeyRepository.findByName(codeApi);
            if (codeApiEntity == null) {
                codeApiEntity = new APIKeyEntity();
                codeApiEntity.setName(codeApi);
                codeApiEntity.setPrivilegeKey(PrivilegeKeyEnum.PREMIUM);
                apiKeyRepository.save(codeApiEntity);
            }
        }
    }

    public List<APIKeyEntity> getListKeys() {
        List<APIKeyEntity> listKey = new ArrayList<>();

        for (String codeApi : listKeysPremium) {
            APIKeyEntity codeApiEntity = apiKeyRepository.findByName(codeApi);
            if (codeApiEntity != null) {
                listKey.add(codeApiEntity);
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
        return listLogEvent;
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
}

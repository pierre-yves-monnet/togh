/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.engine.logevent.LogEvent;
import com.togh.engine.logevent.LogEvent.Level;
import com.togh.entity.APIKeyEntity;
import com.togh.entity.APIKeyEntity.PrivilegeKeyEnum;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.repository.ApiKeyEntityRepository;
import com.togh.tool.ToolCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Logger;


/* ******************************************************************************** */
/*                                                                                  */
/*  ApiKeyService,                                                                  */
/*                                                                                  */
/* Manage all keys                                                                  */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
@Service
public class ApiKeyService implements SmtpKeyService {

  private final Logger logger = Logger.getLogger(ApiKeyService.class.getName());

  private static final LogEvent eventUnknownCode = new LogEvent(ApiKeyService.class.getName(), 1, Level.ERROR, "Unknown code", "This APIKey is unknow",
      "A code is unknown, and can't be updated in the database", "Verify the code");
  private static final LogEvent eventKeysUpdated = new LogEvent(ApiKeyService.class.getName(), 2, Level.SUCCESS, "Keys updated", "API Key are updated with success");
  /**
   * A premium API Key is not limited as the FREE key are.
   */
  public final List<PrivilegeKeyEnum> listSuffixPrivilege = Arrays.asList(PrivilegeKeyEnum.PREMIUM, PrivilegeKeyEnum.FREE);
  @Autowired
  ApiKeyEntityRepository apiKeyRepository;

  @PostConstruct
  public void init() {
    logger.info("ApiKeyService: Check existence API Key");
    // Verify that all keys are here
    for (ApiKey codeApi : ApiKey.listKeysServer) {
      APIKeyEntity codeApiEntity = apiKeyRepository.findByName(codeApi.getName());
      if (codeApiEntity == null) {
        logger.info("ApiKeyService: add API Key [" + codeApi.getName() + "]");

        codeApiEntity = new APIKeyEntity();
        codeApiEntity.setName(codeApi.getName());
        codeApiEntity.setPrivilegeKey(PrivilegeKeyEnum.PREMIUM);
        apiKeyRepository.save(codeApiEntity);
      }
    }
    for (ApiKey codeApi : ApiKey.listKeysBrowser) {
      for (PrivilegeKeyEnum priviledge : listSuffixPrivilege) {
        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(getFinalCode(codeApi, priviledge));
        if (codeApiEntity == null) {
          logger.info("ApiKeyService: add API Key [" + codeApi.getName() + "]");

          codeApiEntity = new APIKeyEntity();
          codeApiEntity.setName(codeApi + "_" + priviledge);
          codeApiEntity.setPrivilegeKey(priviledge);
          apiKeyRepository.save(codeApiEntity);
        }
      }
    }
  }

  /**
   * @param listKeys list of key to return. If null, then the list of keys are all
   * @return the list of all API Key.
   */
  public List<APIKeyEntity> getListApiKeys(List<ApiKey> listKeys) {
    List<APIKeyEntity> listKey = new ArrayList<>();
    List<ApiKey> listSourceKey = new ArrayList<>();
    if (listKeys != null) {
      listSourceKey.addAll(listKeys);
    } else {
      listSourceKey.addAll(ApiKey.getAlls());
    }
    for (ApiKey codeApi : listSourceKey) {

      if (codeApi.isPrivilegeKey()) {
        for (PrivilegeKeyEnum privilege : listSuffixPrivilege) {
          APIKeyEntity codeApiEntity = apiKeyRepository.findByName(getFinalCode(codeApi, privilege));
          if (codeApiEntity != null) {
            listKey.add(codeApiEntity);
          }
        }
      } else {

        APIKeyEntity codeApiEntity = apiKeyRepository.findByName(codeApi.getName());
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
   * @param listApiKey list of all API keys to update
   * @return list of event
   */
  public List<LogEvent> updateKeys(List<Map<String, Object>> listApiKey) {
    List<LogEvent> listLogEvent = new ArrayList<>();
    for (Map<String, Object> oneKey : listApiKey) {
      APIKeyEntity codeApiEntity = apiKeyRepository.findByName((String) oneKey.get("name"));
      if (codeApiEntity != null) {
        codeApiEntity.setKeyApi((String) oneKey.get("keyApi"));
        apiKeyRepository.save(codeApiEntity);
      } else
        listLogEvent.add(new LogEvent(eventUnknownCode, "Code[" + oneKey.get("name") + "]"));
    }
    if (listLogEvent.isEmpty())
      listLogEvent.add(eventKeysUpdated);
    return listLogEvent;
  }

  /**
   * Return all the key according the user, and it's accreditation
   *
   * @param toghUser the user
   * @return list of key according the user's accreditation
   */
  public Map<String, String> getApiKeyForUser(ToghUserEntity toghUser) {
    Map<String, String> result = new HashMap<>();

    PrivilegeKeyEnum privilege = PrivilegeKeyEnum.FREE;
    // SubscriptionUserEnum  FREE, or PREMIUM or EXCELLENCE
    if ((toghUser.getSubscriptionUser() == SubscriptionUserEnum.PREMIUM)
        || (toghUser.getSubscriptionUser() == SubscriptionUserEnum.EXCELLENCE))
      privilege = PrivilegeKeyEnum.PREMIUM;

    for (ApiKey codeApi : ApiKey.listKeysBrowser) {
      APIKeyEntity codeApiEntity = apiKeyRepository.findByName(getFinalCode(codeApi, privilege));
      if (codeApiEntity != null)
        result.put(codeApi.getName(), codeApiEntity.getKeyApi());
    }
    return result;
  }

  /**
   * Return the Translate Key API
   *
   * @return Google Translate key
   */
  public String getApiKeyGoogleTranslate() {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.TRANSLATE_KEY_API.getName());
    if (codeApiEntity != null) {
      return codeApiEntity.getKeyApi();
    }
    return null;
  }

  /**
   * @param defaultHttp default value
   * @return value of Togh Server
   */
  public String getHttpToghServer(String defaultHttp) {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.HTTP_TOGH_SERVER.getName());
    if (codeApiEntity != null) {
      return codeApiEntity.getKeyApi();
    }
    return defaultHttp;
  }

  /**
   * ReturnSmtpHostName
   *
   * @return SmtpHost
   */
  public String getSmtpHost() {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.SMTP_HOST.getName());
    if (codeApiEntity != null) {
      return codeApiEntity.getKeyApi();
    }
    return null;
  }

  /**
   * @return StmpFrom SmtpFrom
   */
  public String getSmtpFrom() {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.SMTP_FROM.getName());
    if (codeApiEntity != null) {
      return codeApiEntity.getKeyApi();
    }
    return null;
  }

  /**
   * getSmtpPort
   *
   * @return Smtp port number
   */
  public int getSmtpPort() {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.SMTP_PORT.getName());
    if (codeApiEntity != null) {
      try {
        return ToolCast.getLong(codeApiEntity.getKeyApi(), 0L).intValue();
      } catch (Exception e) {
        return 0;
      }
    }
    return 0;
  }

  /**
   * @return Smtp user name
   */
  public String getSmtpUserName() {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.SMTP_USER_NAME.getName());
    if (codeApiEntity != null) {
      String key = codeApiEntity.getKeyApi();
      if (key != null && key.isEmpty())
        return null;
      return key;
    }
    return null;
  }

  /**
   * @return Smtp user Password
   */
  public String getSmtpUserPassword() {
    APIKeyEntity codeApiEntity = apiKeyRepository.findByName(ApiKey.SMTP_USER_PASSWORD.getName());
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
   * @param codeApi   Code API
   * @param privilege Privilege
   * @return a final code
   */
  private String getFinalCode(ApiKey codeApi, PrivilegeKeyEnum privilege) {
    return codeApi.getName() + "_" + privilege.toString();
  }
}

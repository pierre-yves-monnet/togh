/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.service;

import com.togh.eventgrantor.update.FactoryUpdateGrantor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* ******************************************************************************** */
/*                                                                                  */
/*  FactoryService,                                                                 */
/*                                                                                  */
/*  Here a factory to deliver service. With Spring, this should not be necessary    */
/*  But I face a nullPointerException soo...                                        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Service
public class FactoryService {


    @Autowired
    private LoginService loginService;

    @Autowired
    private ToghUserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private TranslatorService translatorService;

    @Autowired
    private FactoryUpdateGrantor factoryGrantor;


    public LoginService getLoginService() {
        return loginService;
    }

    public ToghUserService getToghUserService() {
        return userService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public MonitorService getMonitorService() {
        return monitorService;
    }

    public NotifyService getNotifyService() {
        return notifyService;
    }

    public ApiKeyService getApiKeyService() {
        return apiKeyService;
    }

    public StatsService getStatsService() {
        return statsService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public TranslatorService getTranslatorService() {
        return translatorService;
    }

    public FactoryUpdateGrantor getFactoryGrantor() {
        return factoryGrantor;
    }


}

package com.together.service.accessor;

import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.MonitorService;
import com.together.service.UserService;

public interface ServiceAccessor {
    
    public EventService getEventService();

    public LoginService getLoginService();
    
    public UserService getUserService();
    
    public MonitorService getMonitorService();
}

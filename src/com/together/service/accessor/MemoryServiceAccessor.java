package com.together.service.accessor;

import com.together.repository.mem.EndUserMemRepository;
import com.together.repository.mem.EventMemRepository;
import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.MonitorService;
import com.together.service.UserService;

public class MemoryServiceAccessor implements ServiceAccessor {

    private EventMemRepository eventMemRepository = new EventMemRepository();
    private EndUserMemRepository userMemRepository = new EndUserMemRepository();

    private final static MemoryServiceAccessor memoryServiceAccessor = new MemoryServiceAccessor();

    public static MemoryServiceAccessor getInstance() {
        return memoryServiceAccessor;
    }

    private EventService eventService = new EventService();

    /**
     * Return the event Service using the SprintRepository
     * 
     * @return
     */
    @Override
    public EventService getEventService() {
        eventService.setAccessor(this);
        eventService.setEventRepository(eventMemRepository);
        return eventService;
    }

    
    private LoginService loginService = new LoginService();

    @Override
    public LoginService getLoginService() {
        loginService.setAccessor(this);
        return loginService;
    }

    
    
    private UserService userService = new UserService();

    @Override
    public UserService getUserService() {
        userService.setAccessor(this);
        userService.setEndUserRepository(userMemRepository);
        return userService;
    }

    private MonitorService monitorService = new MonitorService();

    @Override
    public MonitorService getMonitorService() {
        monitorService.setAccessor(this);
        return monitorService;
    }
}

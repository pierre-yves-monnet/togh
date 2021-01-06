package com.together.service.accessor;

import org.springframework.beans.factory.annotation.Autowired;

import com.together.repository.EventRepository;
import com.together.repository.spring.EventSpringRepository;
import com.together.service.EventService;
import com.together.service.LoginService;
import com.together.service.MonitorService;
import com.together.service.UserService;

public class SpringServiceAccessor implements ServiceAccessor {
    
    
    private EventSpringRepository eventSpringRepository;

  
    
//    public static SpringServiceAccessor getInstance() {
//        return springServiceAccessor;
//    }
    
    private final static SpringServiceAccessor springServiceAccessor = new SpringServiceAccessor();
    
    /**
     * Return the event Service using the SprintRepository
     * @return
     */
    public EventService getEventService( ) {
        EventService eventService = new EventService();
        eventService.setAccessor( this );
        // eventService.setEventRepository(eventSpringRepository);
        return eventService;
    }

    @Override
    public LoginService getLoginService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserService getUserService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MonitorService getMonitorService() {
        // TODO Auto-generated method stub
        return null;
    }

}

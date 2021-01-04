package com.together.service;

import com.together.service.accessor.ServiceAccessor;

public class ToghService {
    
    /**
     * a method because this should exist in Spring
     */
    ServiceAccessor serviceAccessor;
    public void setAccessor(ServiceAccessor serviceAccessor ) {
        this.serviceAccessor = serviceAccessor;
    }

}

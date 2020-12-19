package com.together.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.together.data.entity.EventEntity;

@Service

public class EventService {


    public String index() {
        return "Greetings from Spring Boot!";
    }
    
    public List<EventEntity> getEvents(Long userId) {
        return new ArrayList<>(); // directoryRepository.findByParentIdAndSpaceId(parentId, spaceId);
    }
}

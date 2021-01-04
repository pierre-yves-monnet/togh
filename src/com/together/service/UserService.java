package com.together.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.together.data.entity.EndUserEntity;
import com.together.repository.EndUserRepository;

public class UserService  extends ToghService {

    private EndUserRepository endUserRepository;
    
    public void setEndUserRepository( EndUserRepository endUserRepository ) {
        this.endUserRepository = endUserRepository;
    }
    
    public EndUserEntity getUserFromId(long userId ) {
        return endUserRepository.getUserFromId( userId );
    }
    
    public EndUserEntity getFromEmail(String email ) {
        return endUserRepository.getUserFromEmail(email);
    }
    public EndUserEntity getUserFromConnectionStamp(String connectionStamp ) {
        return endUserRepository.getUserFromConnectionStamp(connectionStamp);
    }
    

 
    
}

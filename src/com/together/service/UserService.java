package com.together.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.together.entity.EndUserEntity;
import com.together.repository.EndUserRepository;

@Service
public class UserService {

	@Autowired
    private EndUserRepository endUserRepository;
    
    
    public EndUserEntity getUserFromId(long userId ) {
        return endUserRepository.findById( userId );
    }
    
    public EndUserEntity getFromEmail(String email ) {
        return endUserRepository.findByEmail(email);
    }
    public EndUserEntity getUserFromConnectionStamp(String connectionStamp ) {
        return endUserRepository.findByConnectionStamp(connectionStamp);
    }
    
    public void saveUser(EndUserEntity user ) {
        endUserRepository.save( user );
    }

 
    
}

package com.togh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.togh.entity.ToghUserEntity;
import com.togh.repository.EndUserRepository;

@Service
public class ToghUserService {

	@Autowired
    private EndUserRepository endUserRepository;
    
    
    public ToghUserEntity getUserFromId(long userId ) {
        return endUserRepository.findById( userId );
    }
    
    public ToghUserEntity getFromEmail(String email ) {
        return endUserRepository.findByEmail(email);
    }
    public ToghUserEntity getUserFromConnectionStamp(String connectionStamp ) {
        return endUserRepository.findByConnectionStamp(connectionStamp);
    }
    
    public void saveUser(ToghUserEntity user ) {
        endUserRepository.save( user );
    }

 
    
}

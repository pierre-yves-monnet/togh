package com.together.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.together.entity.EndUserEntity;

public interface EndUserRepository extends JpaRepository<EndUserEntity, Long>  {
    
    public EndUserEntity findById(long id);
    
    public EndUserEntity findByEmail(String email);
    
    public EndUserEntity findByConnectionStamp(String connectionStamp );
}

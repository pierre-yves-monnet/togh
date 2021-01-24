package com.togh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.togh.entity.ToghUserEntity;

public interface EndUserRepository extends JpaRepository<ToghUserEntity, Long>  {
    
    public ToghUserEntity findById(long id);
    
    public ToghUserEntity findByEmail(String email);
    
    public ToghUserEntity findByConnectionStamp(String connectionStamp );
}

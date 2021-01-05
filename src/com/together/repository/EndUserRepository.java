package com.together.repository;

import org.springframework.data.repository.CrudRepository;

import com.together.entity.EndUserEntity;

public interface EndUserRepository extends CrudRepository<EndUserEntity, Long>  {
    
    public EndUserEntity findById(long id);
    
    public EndUserEntity findByEmail(String email);

    
    public EndUserEntity findByConnectionStamp(String connectionStamp );
}

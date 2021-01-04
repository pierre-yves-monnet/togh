package com.together.repository;

import com.together.data.entity.EndUserEntity;

public interface EndUserRepository {
    
    public EndUserEntity getUserFromId(long id);
    
    public EndUserEntity getUserFromEmail(String email);

    
    public EndUserEntity getUserFromConnectionStamp(String connectionStamp );
}

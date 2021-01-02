package com.together.repository;

import com.together.data.entity.EndUserEntity;

public interface EndUserRepository {
    
    public EndUserEntity getUserFromEmail(String email);

}

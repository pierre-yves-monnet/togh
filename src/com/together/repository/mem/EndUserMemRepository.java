package com.together.repository.mem;

import java.util.HashMap;
import java.util.Map;

import com.together.data.entity.EndUserEntity;
import com.together.repository.EndUserRepository;

public class EndUserMemRepository implements EndUserRepository {

    private Map<Long,EndUserEntity> mapEndUserEntity = new HashMap<>();

    @Override
    public EndUserEntity getUserFromEmail(String email) {
        for (EndUserEntity endUser : mapEndUserEntity.values()) {
            if (endUser.getEmail().equals(email))
                return endUser;
        }
        return null;
    } 
    
}
    
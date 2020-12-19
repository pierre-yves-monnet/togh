package com.together.data.entity.base;

import javax.persistence.Column;

public class UserEntity extends BaseEntity {
    
  
    public UserEntity( Long authorId, String name) {
        super( name );
        set("authorId", authorId);
    }
    
    @Column(name="AUTHORID")
    public Long getAuthorId() {
        return getLong("authorId");
    }
}

package com.together.data.entity.base;

import javax.persistence.Column;

public class UserEntity extends BaseEntity {
    
    /**
     * AccessData: default is local, so only a list of user who can access the event can access it.
     * Default Constructor.
     * @param authorId
     * @param name
     */
    public UserEntity( Long authorId, String name) {
        super( name );
        set("authorId", authorId);
        set("accessdata", "local");
    }
    
    @Column(name="AUTHORID")
    public Long getAuthorId() {
        return getLong("authorId");
    }
    
    @Column(name="ACCESSDATA", length=20)
    public String getAccessData() {
        return getString("accessdata");
    }
    public void setAccessData( String accessData) {
        set("accessdata", accessData,20);
    }
}

package com.together.entity.base;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Inheritance
public abstract class UserEntity extends BaseEntity {

    
    @Column(name="AUTHORID")
    private Long authorId;
    
    @Column(name="ACCESSDATA", length=20)
    private String accessdata = "local";

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAccessdata() {
		return accessdata;
	}

	public void setAccessdata(String accessdata) {
		this.accessdata = accessdata;
	}

}

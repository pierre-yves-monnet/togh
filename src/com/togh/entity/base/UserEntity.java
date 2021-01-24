package com.togh.entity.base;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import com.togh.entity.ToghUserEntity;

@MappedSuperclass
@Inheritance
public abstract class UserEntity extends BaseEntity {

    
    
    
    @Column(name="authorid") 
    private Long authorId;
    
    // @ C o lumn(name="author")
    // private ToghUserEntity author;
    
    @Column(name="ACCESSDATA", length=20)
    private String accessdata = "local";

    public UserEntity(ToghUserEntity author, String name) {
        super(name);
        this.authorId = author.getId();
    }
    public UserEntity() {
        super();
    }
    
	public Long getAuthorId() {
		return authorId;
	}
    /*public ToghUserEntity getAuthor() {
        return author;
    }*/

	public void setAuthor(ToghUserEntity author) {
		this.authorId = author.getId();
	}

	public String getAccessdata() {
		return accessdata;
	}

	public void setAccessdata(String accessdata) {
		this.accessdata = accessdata;
	}

}

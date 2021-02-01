package com.togh.entity.base;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;

@MappedSuperclass
@Inheritance
public abstract class UserEntity extends BaseEntity {

    
    
    /*
    @Column(name="authorid") 
    private Long authorId;
    */
    @OneToOne( fetch = FetchType.EAGER)
    // @JoinColumn(name = "author_id", referencedColumnName = "id")
    private ToghUserEntity author;
    
    @Column(name="ACCESSDATA", length=20)
    private String accessdata = "local";

    public UserEntity(ToghUserEntity author, String name) {
        super(name);
        this.author = author;
    }
    public UserEntity() {
        super();
    }
    
	public Long getAuthorId() {
	    
		return (this.author !=null ? this.author.getId() : null);
	}
	public ToghUserEntity getAuthor() {
        return this.author;
    }

	public void setAuthor(ToghUserEntity author) {
		this.author = author;
	}

	public String getAccessdata() {
		return accessdata;
	}

	public void setAccessdata(String accessdata) {
		this.accessdata = accessdata;
	}
	
	/**
	 * 
	 * @param levelInformation
	 * @return
	 */
    public Map<String,Object> getMap(ContextAccess contextAccess) {
        Map<String,Object> resultMap = super.getMap( contextAccess );
        if (contextAccess== contextAccess.ADMIN)
            resultMap.put("authorid", this.getAuthorId());
        return resultMap;
    }

}

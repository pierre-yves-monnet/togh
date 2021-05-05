/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity.base;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.entity.EventExpenseEntity;
import com.togh.entity.ToghUserEntity;
import com.togh.entity.ToghUserEntity.ContextAccess;



/* ******************************************************************************** */
/*                                                                                  */
/*  UserEntity,                                                                     */
/*                                                                                  */
/*  Entity is created / modified by an user.                                        */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@MappedSuperclass
@Inheritance
public abstract class UserEntity extends BaseEntity {

    
    
    @ManyToOne( fetch = FetchType.EAGER)
    @JoinColumn(name = "authorid")
    private ToghUserEntity author;
    
    @Column(name="accessdata", length=20)
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
	
	/*
	 * if the entioty accept expense, it has to override this tow method
	*/
	public boolean acceptExpense() {
	    return false;
	}
    public void setExpense(EventExpenseEntity expense ) {
        return;
    }
	/**
	 * 
	 * @param levelInformation
	 * @return
	 */
    @Override
    public Map<String,Object> getMap(ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        if (contextAccess== ContextAccess.ADMIN)
            resultMap.put("authorid", this.getAuthorId());
        return resultMap;
    }

}

package com.together.data.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.together.data.entity.base.BaseEntity;

/* ******************************************************************************** */
/*                                                                                  */
/*     For each physical user, an endUser is registered                                                                             */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "ENDUSER")
public class EndUserEntity extends BaseEntity {

    /** Name is the userName */
    public EndUserEntity(String userName) {
        super(userName);
    }
    
    @Column(name = "firstname", length=100)
    public String getFirstName() {
        return getString("firstname");
    }
    public void setFirstName(String firstName) {
        set("firstname", firstName,100);
    }
    
    @Column(name = "lastname", length=100)
    public String getLastName() {
        return getString("lastname");
    }
    public void setLastName(String lastName) {
        set("lastname", lastName,100);
    }
    
    @Column(name = "password", length=100)
    public void setPassword(String password) {
        set("password", password,100);    
    }
    
    
    public boolean checkPassword(String passwordToCompare)
    {
        if (passwordToCompare==null)
            return false;
        String password = getString("password");
        return passwordToCompare.equals(password);
    }
    @Column(name = "email", length=100)
    public String getEmail() {
        return getString("email");
    }
    public void setEmail(String email) {
        set("email", email,100);
    }
    
    
    
    @Column(name = "googleid", length=100)
    public String getGoogleId() {
        return getString("googleid");
    }
    
    // jetlack, currency, notifications
    

    
    @Column(name = "DateRegistration")
    public LocalDateTime getDateEvent() {
        return getDate("dateregistration");
    }

    

}

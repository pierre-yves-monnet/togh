package com.togh.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.togh.entity.base.BaseEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* For each physical user, an endUser is registered */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "ENDUSER")
public class ToghUserEntity extends BaseEntity {

	@Column(name = "firstname", length = 100)
	private String firstName;

	@Column(name = "lastname", length = 100)
	private String lastName;

	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "googleid", length = 100)
	private String googleId;

	@Column(name = "ConnectStamp", length = 100)
	private String connectionStamp;

	@Column(name = "connectiontime")
	private LocalDateTime connectionTime;

	@Column(name = "connectionlastactivity")
	public LocalDateTime ConnectionLastActivity;

   @Column(name = "sourceUser", length=10)

	public boolean checkPassword(String passwordToCompare) {
		if (passwordToCompare == null)
			return false;
		return passwordToCompare.equals(password);
	}

	public String  getUserName() {
    	return getName();
    }

	public String getFirstname() {
		return firstName;
	}

	public void setFirstname(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * INVITED : an invitation is sent, the user did not confirm yet
	 *
	 */

	public enum SourceUserEnum {
	    PORTAL(0), GOOGLE(1), INVITED(2);
	    private int valueEnum;
	    private SourceUserEnum( int value ) {
	        this.valueEnum=value;
	    }
	}
	SourceUserEnum sourceUser;
	public void setSourceUser(SourceUserEnum sourceUser) {
        this.sourceUser= sourceUser;
    }
    public SourceUserEnum getSourceUser() {
        return sourceUser;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getConnectionStamp() {
		return connectionStamp;
	}

	public void setConnectionStamp(String connectionStamp) {
		this.connectionStamp = connectionStamp;
	}

	public LocalDateTime getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(LocalDateTime connectionTime) {
		this.connectionTime = connectionTime;
	}

	public LocalDateTime getConnectionLastActivity() {
		return ConnectionLastActivity;
	}

	public void setConnectionLastActivity(LocalDateTime connectionLastActivity) {
		ConnectionLastActivity = connectionLastActivity;
	}

	public String toString() {
		return getUserName() + " Gid@" + getGoogleId() + " " + getFirstname() + " " + getLastName() + " email:"
				+ getEmail() + ")";
	}

}

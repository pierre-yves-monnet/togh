package com.togh.entity;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.togh.entity.ParticipantEntity.ParticipantRoleEnum;
import com.togh.entity.base.BaseEntity;

/* ******************************************************************************** */
/*                                                                                  */
/* For each physical user, an endUser is registered */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "TOGHUSER")
public class ToghUserEntity extends BaseEntity {

    @Column(name = "googleid", length = 100)
    private String googleId;

	@Column(name = "firstname", length = 100)
	private String firstName;

	@Column(name = "lastname", length = 100)
	private String lastName;

	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "email", length = 100)
	private String email;

	public enum VisibilityEnum {
	        ALWAYS("ALWAYS"), ALWAYBUTSEARCH("NOSEARCH"), LIMITEDEVENT("LIMITEDEVT"), NEVER("NEVER");
	        private String valueEnum;
	        private VisibilityEnum( String value ) {
	            this.valueEnum=value;
	        }       
	    }
    @Column(name = "EmailVisibility", length=10)
    private  VisibilityEnum emailVisibility = VisibilityEnum.ALWAYS;

    @Column(name = "phoneNumber", length = 100)
    private String phoneNumber;
	
    @Column(name = "PhoneVisibility" , length=10)
    private VisibilityEnum phoneNumberVisibility = VisibilityEnum.ALWAYS;
    
    

	@Column(name = "ConnectStamp", length = 100)
	private String connectionStamp;

	@Column(name = "connectiontime")
	private LocalDateTime connectionTime;

	@Column(name = "connectionlastactivity")
	public LocalDateTime ConnectionLastActivity;

	/**
	 * The user accept to be part of a search result, to be invited directly in an event
	 */
	@Column(name = "searchable")
	Boolean searchable=true;
	
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

	public boolean isSearchable() {
	    return searchable;
	}
	public void setSearchable() {
	    this.searchable = searchable;
	}
	
	public String toString() {
		return getId()+":"+getUserName() 
		    + (getGoogleId()!=null ? " Gid@" + getGoogleId() : "")		           
		    + (getFirstname()!=null ? " " + getFirstname() + " " + getLastName() : "" )
		    + (getEmail()!=null ? " email:"+ getEmail(): "")
		    + ")";
	}

	/**
	 * Get the user Label, to add in an email, or explanation
	 * @return
	 */
	public String getLabel() {
	    if (firstName!=null || lastName != null)
	        return (firstName!=null ? firstName+" ": "") + (lastName!=null ? lastName:"");
	    return email;
	}
	// define the user access :
	// SEARCH : the user show up in a public search
	// PUBLICACCESS : access is from a public event : event is public or limited, but the user who want to access is only an observer, or not yet confirmed. So, show only what user want to show to the public
	// FRIENDACCESS : access is from an LimitedEvent. The user who want to access is registered in this LimitedEvent, so show what the user want to shopw to hist friend
	// SECRETEVENT : access is from a SECRET event, then show only a first name, nothing else
	// ADMIN : administrator access, give back everything
	public enum ContextAccess { SEARCH, PUBLICACCESS, FRIENDACCESS, SECRETACCESS, ADMIN }
	  /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * @param levelInformation
     * 
     * @return
     */
	@Override
	public Map<String,Object> getMap(ContextAccess contextAccess) {
	    Map<String,Object> resultMap =super.getMap( contextAccess );
	    
	    resultMap.put("firstName",firstName);
	    // if the context is SECRET, the last name is not visible
	    if (contextAccess != ContextAccess.SECRETACCESS)
	        resultMap.put("lastName", lastName);
	    
	    if (isVisible(  emailVisibility, contextAccess))
	        resultMap.put("email", email);
	    else
	        resultMap.put("email", "*********");
        
        if (isVisible( phoneNumberVisibility, contextAccess))
            resultMap.put("phoneNumber", phoneNumber);
        else
            resultMap.put("phoneNumber", "*********");
        return resultMap;
	}

	
	private boolean isVisible( VisibilityEnum visibility, ContextAccess userAccess) {
	    // first rule : admin, return true
	    if (userAccess == ContextAccess.ADMIN)
	        return true;
	    // second rule : secret : never.
	    if (userAccess== ContextAccess.SECRETACCESS) 
	        return false;
	    // then depends of the visibily and the policy
	    if (emailVisibility==VisibilityEnum.ALWAYS)
	        return true;
	    // it's visible only for accepted user in the event
	    if ( userAccess == ContextAccess.FRIENDACCESS && (visibility == VisibilityEnum.LIMITEDEVENT || visibility == VisibilityEnum.ALWAYBUTSEARCH))
	        return true;
	    // in all other case, refuse
	     return false;
	}
}

package com.togh.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.togh.entity.ToghUserEntity.PrivilegeUserEnum;
import com.togh.entity.ToghUserEntity.SourceUserEnum;
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

	public enum VisibilityEnum {ALWAYS, ALWAYBUTSEARCH, LIMITEDEVENT, NEVER }
	
    @Column(name = "emailvisibility", length=15, nullable=false)
    @Enumerated(EnumType.STRING)    
    private VisibilityEnum emailVisibility = VisibilityEnum.ALWAYS;

    @Column(name = "phonenumber", length = 100)
    private String phoneNumber;
	
    @Column(name = "phonevisibility" , length=15, nullable=false)
    @Enumerated(EnumType.STRING)
    private VisibilityEnum phoneNumberVisibility = VisibilityEnum.ALWAYS;
    
    

	@Column(name = "connectstamp", length = 100)
	private String connectionStamp;

	@Column(name = "connectiontime")
	private LocalDateTime connectionTime;

	@Column(name = "connectionlastactivity")
	public LocalDateTime connectionLastActivity;

	/**
	 * The user accept to be part of a search result, to be invited directly in an event
	 */
	@Column(name = "searchable")
	Boolean searchable=true;
	
	public static ToghUserEntity getNewUser(String firstName, String lastName,String email, String password, SourceUserEnum sourceUser) {
	    ToghUserEntity endUser = new ToghUserEntity();
	    endUser.setEmail(email);
        endUser.setFirstName(firstName);
        endUser.setLastName(lastName);
        endUser.setName( firstName+" "+lastName);
        endUser.setPassword(password);
        endUser.setSource(sourceUser);
        LocalDateTime dateNow = LocalDateTime.now(ZoneOffset.UTC);
        endUser.setDatecreation( dateNow );
        endUser.setDatemodification( dateNow );
        endUser.setPrivilegeUser( PrivilegeUserEnum.USER);
        return endUser;
	}
	
	
	public boolean checkPassword(String passwordToCompare) {
		if (passwordToCompare == null)
			return false;
		return passwordToCompare.equals(password);
	}

	public String getFirstname() {
		return firstName;
	}

	public void setFirstName(String firstName) {
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

	public enum SourceUserEnum { PORTAL, GOOGLE, INVITED }
	
	@Column( name="source", length=10,  nullable = false)
	@Enumerated(EnumType.STRING)
	 
	SourceUserEnum source;
	public void setSource(SourceUserEnum sourceUser) {
        this.source= sourceUser;
    }
    public SourceUserEnum getSource() {
        return source;
    }

    /**
     * Level of privilege
     * an ADMIN can administrate the complete application
     * a TRANSlator access all translation function
     * a USER use the application
     *
     */
    public enum PrivilegeUserEnum { ADMIN, TRANS, USER }
    
    @Column( name="privilegeuser", length=10)
    @Enumerated(EnumType.STRING)
     
    PrivilegeUserEnum privilegeUser;
    public void setPrivilegeUser(PrivilegeUserEnum privilegeUser) {
        this.privilegeUser= privilegeUser;
    }
    public PrivilegeUserEnum getPrivilegeUser() {
        return privilegeUser;
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
		return connectionLastActivity;
	}

	public void setConnectionLastActivity(LocalDateTime connectionLastActivity) {
		this.connectionLastActivity = connectionLastActivity;
	}

	public boolean isSearchable() {
	    return searchable;
	}
	public void setSearchable(boolean searchable ) {
	    this.searchable = searchable;
	}
	
	public String toString() {
		return getId()+":"+getName() 
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
	    
	    StringBuilder label = new StringBuilder();
	    
	    if (getName()!=null)
	        label.append( getName() );
	    
        resultMap.put("name", getName());
        resultMap.put("firstName",firstName);
	    // if the context is SECRET, the last name is not visible
	    if (contextAccess != ContextAccess.SECRETACCESS)
	        resultMap.put("lastName", lastName);
	    
	    if (isVisible(  emailVisibility, contextAccess)) {
	        resultMap.put("email", email);
	        if (email!=null)
	            label.append(" ("+email+")");
	    }
	    else
	        resultMap.put("email", "*********");
        
        if (isVisible( phoneNumberVisibility, contextAccess)) {
            resultMap.put("phoneNumber", phoneNumber);
            if (phoneNumber!=null)
                label.append(" "+phoneNumber);
        }
        else
            resultMap.put("phoneNumber", "*********");
        
        resultMap.put("label", label.toString());
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

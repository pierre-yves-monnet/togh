/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.togh.engine.tool.EngineTool;
import com.togh.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/* For each physical user, an endUser is registered */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "TOGHUSER")
@EqualsAndHashCode(callSuper = false)
public @Data class ToghUserEntity extends BaseEntity {

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

    @Column(name = "language", length = 5 )
    @org.hibernate.annotations.ColumnDefault("'en'")
    private String language;

    
    public enum VisibilityEnum {
        ALWAYS, ALWAYBUTSEARCH, LIMITEDEVENT, NEVER
    }

    @Column(name = "emailvisibility", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private VisibilityEnum emailVisibility = VisibilityEnum.ALWAYS;

    @Column(name = "phonenumber", length = 100)
    private String phoneNumber;

    @Column(name = "phonevisibility", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private VisibilityEnum phoneNumberVisibility = VisibilityEnum.ALWAYS;

    @Column(name = "connectstamp", length = 100)
    private String connectionStamp;

    @Column(name = "connectiontime")
    private LocalDateTime connectionTime;

    @Column(name = "connectionlastactivity")
    public LocalDateTime connectionLastActivity;

    public enum StatusUserEnum {
        ACTIF, DISABLED, BLOCKED
    }

    @Column(name = "statususer", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'ACTIF'")
    StatusUserEnum statusUser;

    /**
     * The user accept to be part of a search result, to be invited directly in an event
     */
    @Column(name = "searchable")
    Boolean searchable = true;

    public static ToghUserEntity getNewUser(String firstName, String lastName, String email, String password, SourceUserEnum sourceUser) {
        ToghUserEntity endUser = new ToghUserEntity();
        endUser.setEmail(email);
        endUser.setFirstName(firstName);
        endUser.setLastName(lastName);
        endUser.setName(firstName + " " + lastName);
        endUser.setPassword(password);
        endUser.setSource(sourceUser);
        LocalDateTime dateNow = LocalDateTime.now(ZoneOffset.UTC);
        endUser.setDatecreation(dateNow);
        endUser.setDatemodification(dateNow);
        endUser.setPrivilegeUser(PrivilegeUserEnum.USER);
        endUser.setStatusUser(StatusUserEnum.ACTIF);
        endUser.setSubscriptionUser(SubscriptionUserEnum.FREE);
        return endUser;
    }

    public boolean checkPassword(String passwordToCompare) {
        if (passwordToCompare == null)
            return false;
        return passwordToCompare.equals(password);
    }

    /**
     * INVITED : an invitation is sent, the user did not confirm yet
     */

    public enum SourceUserEnum {
        PORTAL, GOOGLE, INVITED, SYSTEM
    }

   

    @Column(name = "source", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)

    SourceUserEnum source;
    
    public enum TypePictureEnum {
        TOGH, CYPRIS, URL, IMAGE
    }
    @Column(name = "typepicture", length = 10, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'TOGH'")
    @Enumerated(EnumType.STRING)

    TypePictureEnum typePicture;

    @Column(name = "picture", length = 300)
    String picture;

    /**
     * Level of privilege
     * an ADMIN can administrate the complete application
     * a TRANSlator access all translation function
     * a USER use the application
     */
    public enum PrivilegeUserEnum {
        ADMIN, TRANS, USER
    }

    @Column(name = "privilegeuser", length = 10)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'USER'")
    PrivilegeUserEnum privilegeUser;

    /**
     * attention, this value is used in different entity
     */
    public enum SubscriptionUserEnum {
        FREE, PREMIUM, EXCELLENCE
    }

    @Column(name = "subscriptionuser", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'FREE'")
    SubscriptionUserEnum subscriptionUser;

    @Column(name = "showtipsuser")
    @org.hibernate.annotations.ColumnDefault("'1'")
    Boolean showTipsUser;

    public String toString() {
        return getId() + ":" + getName()
                + (getGoogleId() != null ? " Gid@" + getGoogleId() : "")
                + (getFirstName() != null ? " " + getFirstName() + " " + getLastName() : "")
                + (getEmail() != null ? " email:" + getEmail() : "")
                + ")";
    }

    /**
     * Get the user Label, to add in an email, or explanation
     * 
     * @return
     */
    public String getLabel() {
        if (firstName != null || lastName != null)
            return (firstName != null ? firstName + " " : "") + (lastName != null ? lastName : "");
        return email;
    }

    // define the user access :
    // SEARCH : the user show up in a public search
    // PUBLICACCESS : access is from a public event : event is public or limited, but the user who want to access is only an observer, or not yet confirmed. So, show only what user want to show to the public
    // FRIENDACCESS : access is from an LimitedEvent. The user who want to access is registered in this LimitedEvent, so show what the user want to shopw to hist friend
    // SECRETEVENT : access is from a SECRET event, then show only a first name, nothing else
    // ADMIN : administrator access, give back everything
    // MYPROFILE : I want to access my profile
    public enum ContextAccess {
        SEARCH, PUBLICACCESS, FRIENDACCESS, SECRETACCESS, ADMIN, MYPROFILE
    }

    /**
     * Get the information as the levelInformation in the event. A OWNER see more than a OBSERVER for example
     * 
     * @param levelInformation
     * @return
     */
    @Override
    public Map<String, Object> getMap(ContextAccess contextAccess, Long timezoneOffset) {
        Map<String, Object> resultMap = super.getMap(contextAccess, timezoneOffset);

        StringBuilder label = new StringBuilder();
        StringBuilder longlabel = new StringBuilder();

        if (getName() != null) {
            label.append(getName());
            longlabel.append(getName());
        }

        resultMap.put("tagid",System.currentTimeMillis() % 10000);
        resultMap.put("name", getName());
        resultMap.put("firstName", firstName);
        resultMap.put("typePicture", typePicture);
        resultMap.put("picture", picture);
        
        resultMap.put("statusUser", statusUser.toString());

        // if the context is SECRET, the last name is not visible
        if (contextAccess != ContextAccess.SECRETACCESS)
            resultMap.put("lastName", lastName);

        if (isVisible(emailVisibility, contextAccess)) {
            resultMap.put("email", email);
            if (email != null && email.trim().length() > 0) {
                // the label is the email only if there is no label at this moment
                if (label.length() == 0)
                    label.append(" (" + email + ")");
                longlabel.append(" (" + email + ")");
            }
        } else
            resultMap.put("email", "*********");

        if (isVisible(phoneNumberVisibility, contextAccess)) {
            resultMap.put("phoneNumber", phoneNumber);
            if (phoneNumber != null) {
                if (label.length() == 0)
                    label.append(" " + phoneNumber);
                longlabel.append(" " + phoneNumber);
            }
        } else
            resultMap.put("phoneNumber", "*********");

        resultMap.put("label", label.toString());
        resultMap.put("longlabel", longlabel.toString());

        if (contextAccess == ContextAccess.MYPROFILE || contextAccess == ContextAccess.ADMIN) {
            resultMap.put("subscriptionuser", subscriptionUser.toString());
            resultMap.put("showTipsUser", showTipsUser);

        }
        if (contextAccess == ContextAccess.ADMIN) {
            resultMap.put("privilegeuser", privilegeUser.toString());

            resultMap.put("connectiontime", EngineTool.dateToString(connectionTime));

            LocalDateTime connectionTimeLocal = connectionTime.minusMinutes(timezoneOffset);
            resultMap.put("connectiontimest", EngineTool.dateToHumanString(connectionTimeLocal));

            resultMap.put("connectionlastactivity", EngineTool.dateToString(connectionLastActivity));

            LocalDateTime connectionLastActivityLocal = connectionLastActivity.minusMinutes(timezoneOffset);
            resultMap.put("connectionlastactivityst", EngineTool.dateToHumanString(connectionLastActivityLocal));

            resultMap.put("connected", connectionStamp == null ? "OFFLINE" : "ONLINE");
        }
        return resultMap;
    }

    private boolean isVisible(VisibilityEnum visibility, ContextAccess userAccess) {
        // first rule : admin, return true
        if (userAccess == ContextAccess.ADMIN)
            return true;
        // second rule : secret : never.
        if (userAccess == ContextAccess.SECRETACCESS)
            return false;
        // then depends of the visibily and the policy
        if (emailVisibility == VisibilityEnum.ALWAYS)
            return true;
        // it's visible only for accepted user in the event
        if (userAccess == ContextAccess.FRIENDACCESS && (visibility == VisibilityEnum.LIMITEDEVENT || visibility == VisibilityEnum.ALWAYBUTSEARCH))
            return true;
        // in all other case, refuse
        return false;
    }
}

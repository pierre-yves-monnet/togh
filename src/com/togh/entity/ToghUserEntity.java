/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

/* ******************************************************************************** */
/*                                                                                  */
/* For each physical user, an endUser is registered */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "TOGHUSER")
@EqualsAndHashCode(callSuper = false)
public @Data
class ToghUserEntity extends BaseEntity {

    private static Random random = new Random();
    @Column(name = "connectionlastactivity")
    public LocalDateTime connectionLastActivity;
    /**
     * The user accept to be part of a search result, to be invited directly in an event
     */
    @Column(name = "searchable")
    Boolean searchable = true;

    @Column(name = "source", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    SourceUserEnum source;

    @Column(name = "typepicture", length = 10, nullable = false)
    @org.hibernate.annotations.ColumnDefault("'TOGH'")
    @Enumerated(EnumType.STRING)

    TypePictureEnum typePicture;
    @Column(name = "picture", length = 300)
    String picture;
    @Column(name = "privilegeuser", length = 10)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'USER'")
    PrivilegeUserEnum privilegeUser;

    @Column(name = "subscriptionuser", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'FREE'")
    SubscriptionUserEnum subscriptionUser;

    @Column(name = "showtipsuser")
    @org.hibernate.annotations.ColumnDefault("'1'")
    Boolean showTipsUser;

    @Column(name = "googleid", length = 100)
    private String googleId;
    @Column(name = "firstname", length = 100)
    private String firstName;
    @Column(name = "lastname", length = 100)
    private String lastName;
    @Column(name = "password", length = 100)
    private String password;
    @Column(name = "lengthpassword")
    private Integer lengthPassword;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "language", length = 5)
    @org.hibernate.annotations.ColumnDefault("'en'")
    private String language;
    /**
     * Save the user time zone. Then, each communication (email...) will be translated to this time zone
     */
    @Column(name = "usertimezone", length = 10)
    private String userTimeZone;
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

    @Column(name = "statususer", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.ColumnDefault("'ACTIF'")
    private StatusUserEnum statusUser;

    @Column(name = "invitationstamp", length = 100)
    private String invitationStamp;

    public static ToghUserEntity createNewUser(String firstName, String lastName, String email, String password, SourceUserEnum sourceUser) {
        ToghUserEntity endUser = new ToghUserEntity();
        endUser.setEmail(email);
        endUser.setFirstName(firstName);
        endUser.setLastName(lastName);
        endUser.calculateName();
        endUser.setPassword(password);
        endUser.setSource(sourceUser);
        LocalDateTime dateNow = LocalDateTime.now(ZoneOffset.UTC);
        endUser.setDateCreation(dateNow);
        endUser.setDateModification(dateNow);
        endUser.setPrivilegeUser(PrivilegeUserEnum.USER);
        endUser.setStatusUser(StatusUserEnum.ACTIF);
        endUser.setSubscriptionUser(SubscriptionUserEnum.FREE);
        endUser.setTypePicture(TypePictureEnum.TOGH);

        return endUser;
    }

    public static ToghUserEntity createInvitedUser(String email) {
        String randomStamp = String.valueOf(System.currentTimeMillis()) + String.valueOf(random.nextInt(100000));

        ToghUserEntity toghUserEntity = createNewUser(null, null, email, null, SourceUserEnum.INVITED);
        toghUserEntity.setStatusUser(StatusUserEnum.INVITED);
        toghUserEntity.setInvitationStamp(randomStamp);

        return toghUserEntity;
    }

    /**
     * Calculate the name according the first and last name
     */
    public void calculateName() {
        setName((firstName == null ? "" : firstName + " ") + (lastName == null ? "" : lastName));
    }

    public boolean checkPassword(String passwordToCompare) {
        if (passwordToCompare == null)
            return false;
        return passwordToCompare.equals(password);
    }

    @Override
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
     * @return user label
     */
    public String getLabel() {
        if (isExist(firstName) && isExist(lastName))
            return (firstName != null ? firstName + " " : "") + (lastName != null ? lastName : "");
        return email;
    }

    private boolean isExist(String value) {
        return (value != null && value.trim().length() > 0);
    }

    public enum VisibilityEnum {
        ALWAYS, ALWAYBUTSEARCH, LIMITEDEVENT, NEVER
    }

    /**
     * Set the password. Update the length as well, to help person to maybe remind it?
     *
     * @param password new password to saved
     */
    public void setPassword(String password) {
        setLengthPassword(password == null ? 0 : password.length());
    }


    /**
     * Invited: email was sent, waiting to be confirmed
     *
     * @author Firstname Lastname
     */
    public enum StatusUserEnum {
        ACTIF, DISABLED, BLOCKED, INVITED
    }

    /**
     * INVITED : an invitation is sent, the user did not confirm yet
     */

    public enum SourceUserEnum {
        PORTAL, GOOGLE, INVITED, SYSTEM
    }

    public enum TypePictureEnum {
        TOGH, CYPRIS, URL, IMAGE
    }

    /**
     * Level of privilege
     * an ADMIN can administrate the complete application
     * a TRANSlator access all translation function
     * a USER use the application
     */
    public enum PrivilegeUserEnum {
        ADMIN, TRANS, USER
    }

    /**
     * attention, this value is used in different entity
     */
    public enum SubscriptionUserEnum {
        FREE, PREMIUM, EXCELLENCE
    }


}

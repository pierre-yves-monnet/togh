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

/* ******************************************************************************** */
/*                                                                                  */
/* For each physical user, an endUser is registered */
/*                                                                                  */
/* To get the userLabel to send to the browser, please use ToghUserSerializer       */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "TOGHUSER")
@EqualsAndHashCode(callSuper = false)
public @Data
class ToghUserEntity extends BaseEntity {

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

  @Column(name = "showtakeatour")
  @org.hibernate.annotations.ColumnDefault("'1'")
  Boolean showTakeATour;

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
   * Return a lobel when we need to log a user
   *
   * @return logLabel
   */
  public String getLogLabel() {
    StringBuilder label = new StringBuilder();
    if (getName() != null && getName().length() > 0)
      label.append(getName() + " ");
    if (getLastName() != null && getLastName().length() > 0)
      label.append(getLastName() + " ");
    if (label.length() == 0)
      label.append(getEmail());
    label.append(" (" + getId() + ")");
    return label.toString();
  }

  private boolean isExist(String value) {
    return (value != null && value.trim().length() > 0);
  }

  /**
   * Set the password. The password given here should be encrypted, so there is no sense to save the length at this moment
   *
   * @param password new password to saved
   */
  public void setPassword(String password) {
    this.password = password;
  }

  public enum VisibilityEnum {
    ALWAYS, ALWAYBUTSEARCH, LIMITEDEVENT, NEVER
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

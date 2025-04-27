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

/* ******************************************************************************** */
/*                                                                                  */
/* APIKeyEntity, save API Key */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


@Entity
@Table(name = "APIKEY")
@EqualsAndHashCode(callSuper = true)
public @Data
class APIKeyEntity extends BaseEntity {

  @Column(name = "privilegekey", length = 10)
  @Enumerated(EnumType.STRING)
  @org.hibernate.annotations.ColumnDefault("'FREE'")
  PrivilegeKeyEnum privilegeKey;


  @Column(name = "provider", length = 10)
  @Enumerated(EnumType.STRING)
  private TypeProviderEnum providerEnum;

  @Column(name = "keyapi", length = 200)
  private String keyApi;

  public enum TypeProviderEnum {
    GOOGLE, WHEATHER, OTHER
  }

  /**
   * Key may be different according the privilege of the user
   */
  public enum PrivilegeKeyEnum {FREE, PREMIUM}


}

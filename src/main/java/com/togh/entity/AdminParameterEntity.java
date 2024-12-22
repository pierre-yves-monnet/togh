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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/* ******************************************************************************** */
/*                                                                                  */
/* AdminParameter */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


@Entity
@Table(name = "ADMINPARAMETER")
@EqualsAndHashCode(callSuper = true)
public @Data
class AdminParameterEntity extends BaseEntity {

  // baseEntity contains a Name - use it for the key name

  @Column(name = "value", length = 200, unique = true)
  private String value;

}

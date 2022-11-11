/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity.base;

import com.togh.entity.ToghUserEntity;

import javax.persistence.*;



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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "authorid")
  private ToghUserEntity author;

  @Column(name = "accessdata", length = 20)
  private String accessdata = "local";

  protected UserEntity(ToghUserEntity author, String name) {
    super(name);
    this.author = author;
  }

  protected UserEntity() {
    super();
  }

  public Long getAuthorId() {

    return (this.author != null ? this.author.getId() : null);
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


}

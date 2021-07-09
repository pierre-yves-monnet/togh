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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "TOGHUSERLOSTPWD")
@EqualsAndHashCode(callSuper = false)
public @Data class ToghUserLostPasswordEntity extends BaseEntity {

    // User attached to this participant
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private ToghUserEntity user;

    /**
     * A UUID is given in the email, to be able to be sure the correct user respond
     */
    @Column(name = "uuid", length = 300, nullable=false)
    private String uuid;
    
    @Column(name = "datevalidity",  nullable=false)
    private LocalDateTime dateValidity;
    
    public enum StatusProcessEnum {
        PREPARATION, EMAILINERROR, SERVERISSUE, EMAILSENT, PAGEACCESS, RESETOK
    }

    @Column(name = "statusprocess", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusProcessEnum StatusProcess = StatusProcessEnum.PREPARATION;

}

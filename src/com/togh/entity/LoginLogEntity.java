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
import com.togh.service.LoginService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/* ******************************************************************************** */
/*                                                                                  */
/* Register operation on Login (success, failed) */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity
@Table(name = "LOGINLOG")
@EqualsAndHashCode(callSuper = false)
public @Data
class LoginLogEntity extends BaseEntity {

    @Column(name = "googleId", length = 100)
    private String googleId;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "ipAddress", length = 100)
    private String ipAddress;


    @Column(name = "statusConnection", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginService.LoginStatus statusConnection;

    @Column(name = "numberOfTentatives")
    private int numberOfTentatives;

    /**
     * Group record per timeslot of 15 mn. Information is YYYYMMDD-HH:MM where MM is 00/15/30/45
     * Example: 20211004-16:00
     */
    @Column(name = "timeSlot", length = 14, nullable = false)
    private String timeSlot;

}
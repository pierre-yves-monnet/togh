/* ******************************************************************************** */
/*                                                                                  */
/* Togh Project */
/*                                                                                  */
/* This component is part of the Togh Project, developed by Pierre-Yves Monnet */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.togh.entity.ToghUserEntity.SubscriptionUserEnum;
import com.togh.entity.base.BaseEntity;
import com.togh.service.SubscriptionService.LimitReach;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/* AdminStatsConnection, Keep connection day per day of the connection              */  
/*                                                                                  */
/*   yearMonthDay is yyyy-mm-dd                                                       */ 
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */


@Entity
@Table(name = "ADMSTATSUSAGE")
@EqualsAndHashCode(callSuper=true)
public @Data class AdminStatsUsageEntity extends BaseEntity {

    @Column(name = "yearmonthday", length = 10)
    public String yearMonthDay;

    public enum TypeStatsEnum { CONNECTION, ACCESS, LIMITSUBSCRIPT};
    @Column(name = "typestats", length = 15)
    @Enumerated(EnumType.STRING)
    public TypeStatsEnum typeStatistique;

    @Column(name = "subscriptionuser", length = 10)
    @Enumerated(EnumType.STRING)
    public SubscriptionUserEnum subscriptionUser; 

    @Column(name = "limitreach", length = 15)
    @Enumerated(EnumType.STRING)
    public LimitReach limitReach;
    
    
    @Column(name = "value")
    public Long value;

}
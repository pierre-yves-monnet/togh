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

import com.togh.entity.APIKeyEntity.TypeProviderEnum;
import com.togh.entity.base.BaseEntity;

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
@Table(name = "ADMSTATSCONNECTION")
@EqualsAndHashCode(callSuper=true)
public @Data class AdminStatsConnectionEntity extends BaseEntity {

    @Column(name = "yearmonthday", length = 10)
    public String yearMonthDay;

    public enum TypeStatsEnum { CONNECTION, ACCESS};
    @Column(name = "typestats", length = 15)
    @Enumerated(EnumType.STRING)
    private TypeStatsEnum typeStatistique;


    @Column(name = "value")
    public Long value;

}

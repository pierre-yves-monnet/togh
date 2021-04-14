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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.togh.entity.EventEntity.DatePolicyEnum;
import com.togh.entity.EventEntity.ScopeEnum;
import com.togh.entity.EventEntity.StatusEventEnum;
import com.togh.entity.EventEntity.TypeEventEnum;
import com.togh.entity.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/* APIKeyEntity, save API Key */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */



@Entity
@Table(name = "APIKEY")
@EqualsAndHashCode(callSuper=true)
public @Data class APIKeyEntity extends BaseEntity {

    public enum TypeProviderEnum {
        GOOGLE, WHEATHER, OTHER
    }

    @Column(name = "provider", length = 10)
    @Enumerated(EnumType.STRING)
    private TypeProviderEnum providerEnum;

    @Column(name = "code" length = 20)
    private String code;

    @Column(name = "apikey" length = 200)
    private String apikey;

}

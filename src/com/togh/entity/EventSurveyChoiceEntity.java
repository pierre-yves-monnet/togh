/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.togh.entity.EventShoppingListEntity.ShoppingStatusEnum;
import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventSurveyEntity,                                                                      */
/*                                                                                  */
/*  Manage Survey in a event                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTSURVEYCHOICE")
@EqualsAndHashCode(callSuper=true)
public @Data class EventSurveyChoiceEntity extends UserEntity {

    @Column(name = "code", nullable=false)
    private Integer code;

    @Column(name = "proptext", length=50)
    private String proptext;
    
    @Override
    public Map<String,Object> getMap( ContextAccess contextAccess) {
        Map<String,Object> resultMap = super.getMap( contextAccess );
        resultMap.put("code", code);
        resultMap.put("proptext", proptext);
        return resultMap;
    }

}

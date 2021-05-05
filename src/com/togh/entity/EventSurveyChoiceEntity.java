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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    public Map<String,Object> getMap( ContextAccess contextAccess, Long timezoneOffset) {
        Map<String,Object> resultMap = super.getMap( contextAccess, timezoneOffset );
        resultMap.put("code", code);
        resultMap.put("proptext", proptext);
        return resultMap;
    }

}

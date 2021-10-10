/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.ToghUserEntity.ContextAccess;
import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventSurveyChoiceEntity                                                         */
/*                                                                                  */
/*  In a survey, the choice (in survey Restaurant?, choice is Mexican, French)      */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTSURVEYCHOICE")
@EqualsAndHashCode(callSuper=true)
public @Data class EventSurveyChoiceEntity extends UserEntity {

    public static final String CST_SLABOPERATION_CHOICELIST = "choicelist";

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

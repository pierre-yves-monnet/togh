/* ******************************************************************************** */
/*                                                                                  */
/*  Togh Project                                                                    */
/*                                                                                  */
/*  This component is part of the Togh Project, developed by Pierre-Yves Monnet     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */
package com.togh.entity;

import com.togh.entity.base.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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


}

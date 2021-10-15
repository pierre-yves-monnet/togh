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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Map;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventSurveyEntity,                                                                      */
/*                                                                                  */
/*  Manage Survey in a event                                                          */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTSURVEYANSWER")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventSurveyAnswerEntity extends UserEntity {


    public static final String CST_SLABOPERATION_ANSWERLIST = "answerlist";

    // User attached to this task (maybe an external user, why not ?
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "whoid")
    private ToghUserEntity whoId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "EVTSURVEYANSWERCHOICE",
            joinColumns = {@JoinColumn(name = "surveyid", referencedColumnName = "id")})
    @Fetch(value = FetchMode.SELECT)
    @MapKeyColumn(name = "choice")
    @Column(name = "decision")

    private Map<String, Boolean> decision;


}

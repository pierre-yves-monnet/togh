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
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/* ******************************************************************************** */
/*                                                                                  */
/*  EventSurveyEntity,                                                              */
/*                                                                                  */
/*  Manage Survey in an event. A survey has different children                      */
/*    - EventSurveyChoiceEntity : the list of choices                               */
/*    - EventSurveyAnswerEntity : each participant has a AnswerEntity.              */
/*              This class has a Map<String,boolean> decision to store the vote     */
/*                                                                                  */
/*                                                                                  */
/* ******************************************************************************** */

@Entity

@Table(name = "EVTSURVEY")
@EqualsAndHashCode(callSuper = true)
public @Data
class EventSurveyEntity extends UserEntity {

    public static final String CST_SLABOPERATION_SURVEYLIST = "surveylist";


    @Column(name = "status", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private SurveyStatusEnum status;


    // name is part of the baseEntity
    @Column(name = "description", length = 400)
    private String description;

    // choice : list of "code/ proposition"
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @BatchSize(size = 100)
    @JoinColumn(name = "surveyid", nullable = false)
    @OrderBy("id")
    private List<EventSurveyChoiceEntity> choicelist = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @Column(name = "answer", length = 100)
    @JoinColumn(name = "surveyid")
    @OrderBy("id")
    private List<EventSurveyAnswerEntity> answerlist = new ArrayList<>();


    public enum SurveyStatusEnum {
        INPREPAR, OPEN, CLOSE
    }

}
